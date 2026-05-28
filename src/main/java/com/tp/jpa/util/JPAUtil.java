package com.tp.jpa.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Clase utilitaria Singleton para gestionar el EntityManagerFactory de JPA.
 * Centraliza la creación y cierre de la fábrica de EntityManagers,
 * asegurando que solo exista una instancia en toda la aplicación.
 */
public class JPAUtil {

    // La instancia se crea una única vez cuando se carga la clase (patrón Singleton)
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("FoodStorePU");

    // Constructor privado para evitar instanciación directa
    private JPAUtil() {}

    /**
     * Retorna la instancia única del EntityManagerFactory.
     * @return EntityManagerFactory configurado con la unidad "FoodStorePU"
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Cierra el EntityManagerFactory al finalizar la aplicación.
     * Debe llamarse una sola vez antes de terminar el programa.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
