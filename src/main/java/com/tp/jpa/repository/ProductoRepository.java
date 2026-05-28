package com.tp.jpa.repository;

import com.tp.jpa.model.Producto;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio concreto para la entidad Producto.
 *
 * <p>Extiende BaseRepository&lt;Producto&gt; heredando las operaciones CRUD base,
 * y agrega la consulta JPQL personalizada para filtrar productos por categoria.</p>
 */
public class ProductoRepository extends BaseRepository<Producto> {

    /**
     * Constructor que pasa Producto.class al repositorio base.
     */
    public ProductoRepository() {
        super(Producto.class);
    }

    /**
     * Retorna la lista de productos activos cargando la categoria de forma ansiosa (Eager)
     * para evitar LazyInitializationException en la vista/consola.
     */
    @Override
    public List<Producto> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE p.eliminado = false";
            return em.createQuery(jpql, Producto.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca un producto por ID cargando la categoria de forma ansiosa (Eager)
     * para evitar LazyInitializationException.
     */
    @Override
    public Optional<Producto> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE p.id = :id";
            List<Producto> resultados = em.createQuery(jpql, Producto.class)
                    .setParameter("id", id)
                    .getResultList();
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } finally {
            em.close();
        }
    }

    /**
     * Busca todos los productos activos (no eliminados) que pertenecen a una categoria especifica.
     * Carga la categoria de forma ansiosa (Eager).
     *
     * @param categoriaId El ID de la categoria por la cual filtrar
     * @return Lista de productos activos pertenecientes a la categoria indicada (puede ser vacia)
     */
    public List<Producto> buscarPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Producto p " +
                          "LEFT JOIN FETCH p.categoria " +
                          "WHERE p.categoria.id = :categoriaId " +
                          "AND p.eliminado = false";

            return em.createQuery(jpql, Producto.class)
                    .setParameter("categoriaId", categoriaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
