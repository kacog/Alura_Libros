package com.literalura.literalura.repository;

import com.literalura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoresRepository extends JpaRepository<Autor,Long> {
    Autor findAutorByNombreIgnoreCase(String nombre);
    List<Autor> findAutorByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(Integer fechaDeNacimiento, Integer fechaDeMuerte);


}
