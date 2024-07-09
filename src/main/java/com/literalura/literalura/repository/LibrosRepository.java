package com.literalura.literalura.repository;

import com.literalura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibrosRepository extends JpaRepository<Libro,Long> {
    Libro findLibroByTitulo(String nombre);
    List<Libro> findLibrosByIdiomasContaining(String idiomas);

}
