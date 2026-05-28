# Parcial JPA — Repositorios y ABM de Categorías y Productos

**Materia:** Programación III  
**Carrera:** Tecnicatura Universitaria en Programación (Virtual)  
**Entrega:** Evaluación Parcial — Java Persistence API (JPA)

---

## Descripción

Sistema de gestión de **Categorías** y **Productos** usando JPA (Hibernate 6) con base de datos H2 embebida.
Implementa un patrón de repositorio genérico (`BaseRepository<T>`) y expone un menú interactivo por consola
con operaciones de **Alta, Baja lógica y Modificación (ABM)**, más una consulta JPQL personalizada de productos por categoría.

### Estructura de paquetes

```
src/main/java/com/tp/jpa/
├── model/                  ← Entidades JPA (Base, Categoria, Producto, Usuario, Pedido, DetallePedido)
│   └── enums/              ← Enumeraciones (Estado, FormaPago, Rol)
├── util/
│   └── JPAUtil.java        ← Singleton para EntityManagerFactory
├── repository/             ← Repositorios JPA
│   ├── BaseRepository.java ← Repositorio genérico <T> (guardar, buscarPorId, listarActivos, eliminarLogico)
│   ├── CategoriaRepository.java
│   └── ProductoRepository.java ← Agrega buscarPorCategoria(Long id) con JPQL
└── Main.java               ← Menú principal de consola (ABM + Reportes)
```

---

## Instrucciones para ejecutar

### Prerrequisitos

- **Java 17** o superior instalado y en el PATH
- **Gradle** (incluido en el proyecto como wrapper)

### Pasos

1. Cloná o descomprimí el proyecto en una carpeta local.
2. Abrí una terminal en la carpeta raíz del proyecto (donde está `gradlew.bat`).
3. Ejecutá el siguiente comando:

```bash
# En Windows (PowerShell o CMD):
.\gradlew.bat run --console=plain

# En Linux/Mac:
./gradlew run --console=plain
```

> **Nota:** La flag `--console=plain` es necesaria para que el menú interactivo funcione correctamente en la consola.

4. Se abrirá el menú principal en la consola.

### Opciones del menú

```
MENÚ PRINCIPAL
  1. Categorías   → Alta, Baja lógica, Modificación, Listado
  2. Productos    → Alta, Baja lógica, Modificación, Listado
  3. Reportes     → Productos por categoría (consulta JPQL)
  0. Salir
```

### Base de datos

La base de datos H2 se crea automáticamente en `./data/foodstore_db.mv.db` al ejecutar por primera vez.
No es necesaria ninguna configuración adicional.

---

## Detalles técnicos

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Persistencia | JPA 3.0 / Hibernate 6.4 |
| Base de datos | H2 (modo archivo embebido) |
| Build | Gradle 8 con Kotlin DSL |
| Boilerplate | Lombok (getters, setters, builders) |

### Consulta JPQL personalizada

```java
// ProductoRepository.buscarPorCategoria(Long categoriaId)
SELECT p FROM Producto p
WHERE p.categoria.id = :categoriaId
AND p.eliminado = false
```

Usa `TypedQuery<Producto>` con parámetro nombrado, sin casteos manuales.
