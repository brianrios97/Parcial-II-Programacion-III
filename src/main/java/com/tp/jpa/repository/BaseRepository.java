package com.tp.jpa.repository;

import com.tp.jpa.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio genérico abstracto que implementa las operaciones CRUD comunes
 * para cualquier entidad JPA que extienda Base.
 *
 * <p>Cada método gestiona su propio EntityManager: lo abre al inicio
 * y lo cierra en un bloque finally para garantizar la liberación de recursos.</p>
 *
 * @param <T> Tipo de la entidad gestionada por este repositorio
 */
public abstract class BaseRepository<T> {

    /** Clase de la entidad genérica, necesaria para las operaciones de JPA (find, JPQL) */
    protected final Class<T> clazz;

    /** Fábrica de EntityManagers obtenida desde JPAUtil (Singleton) */
    protected final EntityManagerFactory emf;

    /**
     * Constructor que recibe la clase concreta de la entidad.
     * @param clazz La clase de la entidad (ej: Categoria.class, Producto.class)
     */
    protected BaseRepository(Class<T> clazz) {
        this.clazz = clazz;
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    /**
     * Persiste o actualiza la entidad en la base de datos usando merge().
     * Si la entidad tiene ID (ya existe en BD), la actualiza.
     * Si no tiene ID, la inserta como nuevo registro.
     *
     * @param entity La entidad a guardar o actualizar
     * @return La entidad administrada por JPA (con ID generado si era nueva)
     */
    public T guardar(T entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = em.merge(entity);
            em.getTransaction().commit();
            return resultado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la entidad: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Busca una entidad por su ID (clave primaria).
     *
     * @param id El ID de la entidad a buscar
     * @return Optional con la entidad si existe, Optional.empty() si no se encontró
     */
    public Optional<T> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            T entidad = em.find(clazz, id);
            return Optional.ofNullable(entidad);
        } finally {
            em.close();
        }
    }

    /**
     * Retorna la lista de entidades activas (no eliminadas lógicamente).
     * Usa JPQL filtrando por el campo 'eliminado = false' de la clase Base.
     *
     * @return Lista de entidades con eliminado = false
     */
    public List<T> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            // getSimpleName() retorna el nombre simple de la clase (ej: "Categoria", "Producto")
            // que coincide con el nombre de la entidad en JPQL
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.eliminado = false";
            return em.createQuery(jpql, clazz).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Realiza una baja lógica de la entidad: busca el registro por ID
     * y establece el campo 'eliminado = true' sin borrar el registro de la BD.
     *
     * @param id El ID de la entidad a dar de baja lógicamente
     * @return true si se encontró y eliminó lógicamente, false si no existe el ID
     */
    public boolean eliminarLogico(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            T entidad = em.find(clazz, id);
            if (entidad == null) {
                return false;
            }
            em.getTransaction().begin();
            // Usamos reflexión para llamar al método setEliminado(true) generado por Lombok en Base
            entidad.getClass().getMethod("setEliminado", boolean.class).invoke(entidad, true);
            em.merge(entidad);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar lógicamente la entidad: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
