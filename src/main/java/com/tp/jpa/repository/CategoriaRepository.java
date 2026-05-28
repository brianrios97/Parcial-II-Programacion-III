package com.tp.jpa.repository;

import com.tp.jpa.model.Categoria;

/**
 * Repositorio concreto para la entidad Categoria.
 *
 * <p>Extiende BaseRepository&lt;Categoria&gt; heredando todas las operaciones
 * CRUD: guardar, buscarPorId, listarActivos y eliminarLogico.</p>
 *
 * <p>No requiere métodos adicionales ya que todas las operaciones necesarias
 * están cubiertas por el repositorio base.</p>
 */
public class CategoriaRepository extends BaseRepository<Categoria> {

    /**
     * Constructor que pasa Categoria.class al repositorio base para que
     * JPA pueda trabajar con el tipo correcto en find() y las consultas JPQL.
     */
    public CategoriaRepository() {
        super(Categoria.class);
    }
}
