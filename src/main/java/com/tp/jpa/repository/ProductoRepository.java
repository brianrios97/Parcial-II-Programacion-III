package com.tp.jpa.repository;

import com.tp.jpa.model.Producto;
import jakarta.persistence.EntityManager;

import java.util.List;

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
     * Busca todos los productos activos (no eliminados) que pertenecen a una categoria especifica.
     *
     * <p><strong>Consulta JPQL:</strong> Navega desde el objeto Producto a traves de la
     * relacion @ManyToOne hasta el ID de su Categoria usando la notacion de punto (p.categoria.id).
     * El parametro nombrado :categoriaId evita SQL injection y mejora la legibilidad.
     * TypedQuery&lt;Producto&gt; garantiza type-safety sin necesidad de casteos manuales.</p>
     *
     * @param categoriaId El ID de la categoria por la cual filtrar
     * @return Lista de productos activos pertenecientes a la categoria indicada (puede ser vacia)
     */
    public List<Producto> buscarPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        try {
            // JPQL: Filtra productos activos de la categoria dada usando parametro nombrado :categoriaId
            String jpql = "SELECT p FROM Producto p " +
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
