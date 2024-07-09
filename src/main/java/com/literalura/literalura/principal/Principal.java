package com.literalura.literalura.principal;

import com.literalura.literalura.model.*;
import com.literalura.literalura.repository.AutoresRepository;
import com.literalura.literalura.repository.LibrosRepository;
import com.literalura.literalura.service.ConsumoAPI;
import com.literalura.literalura.service.ConvierteDatos;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Principal {
    private Integer opcion = -1;
    private Boolean RunApp = true;
    private final Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "http://gutendex.com/books/";
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final AutoresRepository autoresRepository;
    private final LibrosRepository librosRepository;


    public Principal(AutoresRepository autoresRepository, LibrosRepository librosRepository) {
        this.autoresRepository = autoresRepository;
        this.librosRepository = librosRepository;

    }

    public void muestraElMenu() {
        String menu = """
                ******************************************************
                Elija una opción:

                1-  Buscar libro en Web por título
                2-  Listar libros registrados
                3-  Listar autores registrados
                4-  Listar autores vivos en un detemrinado año
                5-  Listar libros por idioma
                6-  Estadística
                0-  Salir
                                    
                ******************************************************

                """;
        while (RunApp) {
            try {
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();
                switch (opcion) {
                    case 1:
                        agregarLibro();
                        break;
                    case 2:
                        listarLibroRegistrados();
                        break;
                case 3:
                    mostrarAutores();
                    break;
                case 4:
                    mostrarAutoresPorAño();
                    break;
                case 5:
                    mostrasLibrosPorIdioma();
                    break;
                case 6:
                    estadisticaLibros();
                    break;
                    case 0:
                        RunApp = false;
                        System.out.println("Programa Finalizado");
                        break;
                    default:
                        System.out.println("Opción inválida");

                }

            } catch (InputMismatchException e) {
                teclado.nextLine();
                System.out.println("Ingrese un número de opción valido: " + e.getMessage());

            }

        }
    }

    private void estadisticaLibros() {
        System.out.println("------ Estadísticas ------");

        List<Libro> libros = librosRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }
        DoubleSummaryStatistics estadisticas = libros.stream()
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        double maxDescargas = estadisticas.getMax();
        double minDescargas = estadisticas.getMin();
        double promedioDescargas = estadisticas.getAverage();

        System.out.printf("Libro con más descargas: %.0f descargas%n", maxDescargas);
        System.out.printf("Libro con menos descargas: %.0f descargas%n", minDescargas);
        System.out.printf("Promedio de descargas: %.2f descargas%n", promedioDescargas);
    }

    private boolean esIdiomaValido(String idioma) {
        return idioma.equals("es") || idioma.equals("en") || idioma.equals("fr") || idioma.equals("pt");
    }

    private void mostrasLibrosPorIdioma() {
        String menu = """
            Ingrese la categoría de idioma:
            es  - español
            en  - inglés
            fr  - francés
            pt  - portugués
            
            """;
        System.out.println(menu);

        String idioma = teclado.nextLine().trim().toLowerCase();

        if (!esIdiomaValido(idioma)) {
            System.out.println("No existe idioma");
            return;
        }

        List<Libro> librosPorIdioma = librosRepository.findLibrosByIdiomasContaining(idioma);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No hay libros registrados con ese idioma");
        } else {
            int cantidadLibros = librosPorIdioma.size();
            System.out.printf("Total de libros registrados en %s: %d%n", Idioma.fromString(idioma), cantidadLibros);
            librosPorIdioma.forEach(libro -> System.out.println(libro));
        }
    }

    private void mostrarAutoresPorAño() {
        System.out.println("Ingrese el valor para buscar Autor por Año: ");
        var fechaAutor=teclado.nextInt();
        teclado.nextLine();
        if (fechaAutor<0){
            System.out.println("Fecha no válida");
        } else {
            List<Autor> fechaAutores=autoresRepository.findAutorByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(fechaAutor,fechaAutor);
            if (fechaAutores.isEmpty()){
                System.out.println("No hay autores registrados en ese año.");
            } else {
                fechaAutores.forEach(System.out::println);
            }

        }
    }

    private void mostrarAutores() {
        List<Autor> autores=autoresRepository.findAll();
        if (autores.isEmpty()){
            System.out.println("No hay autores registrados");
        } else {
            autores.forEach(System.out::println);
        }

    }

    private Datos buscarLibroPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var titulolibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + titulolibro.replace(" ", "+"));
        return conversor.obtenerDatos(json, Datos.class);
    }

    private void agregarLibro() {
        Datos datosResultado = buscarLibroPorTitulo();
        if (!datosResultado.resultados().isEmpty()) {
            DatosLibro datosLibro = datosResultado.resultados().get(0);
            DatosAutor datosAutor = datosLibro.autor().get(0);
            var tituloDeLibro = librosRepository.findLibroByTitulo(datosLibro.titulo());
            if (tituloDeLibro != null) {
                System.out.println("No se puede registrar el mismo libro más de una vez");

            } else {
                var autorDeLibro = autoresRepository.findAutorByNombreIgnoreCase(datosAutor.nombre());
                Libro libro;
                if (autorDeLibro != null) {
                    libro = new Libro(datosLibro, autorDeLibro);
                } else {
                    Autor autor = new Autor(datosAutor);
                    autoresRepository.save(autor);
                    libro = new Libro(datosLibro, autor);

                }
                librosRepository.save(libro);
                System.out.println("Libro guardado en Base de datos");
                System.out.println(libro);


            }

        }

    }

    private void listarLibroRegistrados() {
        List<Libro> libros=librosRepository.findAll();
        if (libros.isEmpty()){
            System.out.println("No hay libros guardados");
        } else {
            libros.forEach(System.out::println);
        }
    }

}
