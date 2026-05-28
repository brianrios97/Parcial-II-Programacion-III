package com.tp.jpa;

import com.tp.jpa.model.Categoria;
import com.tp.jpa.model.Producto;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.ProductoRepository;
import com.tp.jpa.util.JPAUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Clase principal con menú de consola para el ABM de Categorías y Productos.
 * Expone también una consulta JPQL personalizada de productos por categoría.
 */
public class Main {

    // Repositorios reutilizables durante toda la sesión
    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GESTIÓN — FoodStore JPA     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccioná una opción: ");
            switch (opcion) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuReportes();
                case 0 -> {
                    ejecutando = false;
                    System.out.println("\n¡Hasta luego!");
                }
                default -> System.out.println("⚠ Opción inválida. Intentá de nuevo.");
            }
        }

        // Cerrar recursos al finalizar
        scanner.close();
        JPAUtil.close();
    }

    // =========================================================
    // MENÚS DE NAVEGACIÓN
    // =========================================================

    private static void mostrarMenuPrincipal() {
        System.out.println("\n══════════════ MENÚ PRINCIPAL ══════════════");
        System.out.println("  1. Categorías");
        System.out.println("  2. Productos");
        System.out.println("  3. Reportes");
        System.out.println("  0. Salir");
        System.out.println("════════════════════════════════════════════");
    }

    // =========================================================
    // SUBMENÚ: CATEGORÍAS
    // =========================================================

    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n────────── GESTIÓN DE CATEGORÍAS ──────────");
            System.out.println("  1. Alta de categoría");
            System.out.println("  2. Baja lógica de categoría");
            System.out.println("  3. Modificación de categoría");
            System.out.println("  4. Listado de categorías activas");
            System.out.println("  0. Volver al menú principal");
            System.out.println("───────────────────────────────────────────");

            int opcion = leerEntero("Seleccioná una opción: ");
            switch (opcion) {
                case 1 -> altaCategoria();
                case 2 -> bajaCategoria();
                case 3 -> modificarCategoria();
                case 4 -> listarCategorias();
                case 0 -> volver = true;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    /**
     * HU-03: Alta de categoría.
     * Solicita nombre (obligatorio) y descripción, persiste y muestra el ID generado.
     */
    private static void altaCategoria() {
        System.out.println("\n--- ALTA DE CATEGORÍA ---");

        String nombre = leerTextoObligatorio("Nombre de la categoría: ");
        System.out.print("Descripción (opcional, Enter para omitir): ");
        String descripcion = scanner.nextLine().trim();

        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        nueva.setDescripcion(descripcion.isEmpty() ? null : descripcion);

        Categoria guardada = categoriaRepo.guardar(nueva);
        System.out.println("✔ Categoría creada con ID: " + guardada.getId());
    }

    /**
     * HU-05: Baja lógica de categoría.
     * Solicita el ID y marca eliminado = true. Muestra error si no existe.
     */
    private static void bajaCategoria() {
        System.out.println("\n--- BAJA LÓGICA DE CATEGORÍA ---");
        listarCategorias();

        long id = leerLong("Ingresá el ID de la categoría a eliminar: ");
        boolean eliminado = categoriaRepo.eliminarLogico(id);

        if (eliminado) {
            System.out.println("✔ Categoría ID " + id + " dada de baja correctamente.");
        } else {
            System.out.println("✘ Error: no existe una categoría activa con ID " + id + ".");
        }
    }

    /**
     * HU-04: Modificación de categoría.
     * Solicita el ID, muestra valores actuales, permite editar nombre y/o descripción.
     * Si el campo queda vacío, conserva el valor anterior.
     */
    private static void modificarCategoria() {
        System.out.println("\n--- MODIFICACIÓN DE CATEGORÍA ---");
        listarCategorias();

        long id = leerLong("Ingresá el ID de la categoría a modificar: ");
        Optional<Categoria> opt = categoriaRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("✘ Error: no existe una categoría activa con ID " + id + ".");
            return;
        }

        Categoria categoria = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre     : " + categoria.getNombre());
        System.out.println("  Descripción: " + (categoria.getDescripcion() != null ? categoria.getDescripcion() : "(vacía)"));

        System.out.print("Nuevo nombre (Enter para conservar '" + categoria.getNombre() + "'): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) {
            categoria.setNombre(nuevoNombre);
        }

        System.out.print("Nueva descripción (Enter para conservar): ");
        String nuevaDesc = scanner.nextLine().trim();
        if (!nuevaDesc.isEmpty()) {
            categoria.setDescripcion(nuevaDesc);
        }

        categoriaRepo.guardar(categoria);
        System.out.println("✔ Categoría ID " + id + " actualizada correctamente.");
    }

    /**
     * Muestra todas las categorías activas con ID, nombre y descripción.
     */
    private static void listarCategorias() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("  (No hay categorías activas registradas)");
            return;
        }
        System.out.println("\n┌──────┬──────────────────────────┬─────────────────────────────────┐");
        System.out.printf( "│ %-4s │ %-24s │ %-31s │%n", "ID", "Nombre", "Descripción");
        System.out.println("├──────┼──────────────────────────┼─────────────────────────────────┤");
        for (Categoria c : categorias) {
            System.out.printf("│ %-4d │ %-24s │ %-31s │%n",
                    c.getId(),
                    truncar(c.getNombre(), 24),
                    truncar(c.getDescripcion() != null ? c.getDescripcion() : "", 31));
        }
        System.out.println("└──────┴──────────────────────────┴─────────────────────────────────┘");
    }

    // =========================================================
    // SUBMENÚ: PRODUCTOS
    // =========================================================

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n─────────── GESTIÓN DE PRODUCTOS ───────────");
            System.out.println("  1. Alta de producto");
            System.out.println("  2. Baja lógica de producto");
            System.out.println("  3. Modificación de producto");
            System.out.println("  4. Listado de productos activos");
            System.out.println("  0. Volver al menú principal");
            System.out.println("────────────────────────────────────────────");

            int opcion = leerEntero("Seleccioná una opción: ");
            switch (opcion) {
                case 1 -> altaProducto();
                case 2 -> bajaProducto();
                case 3 -> modificarProducto();
                case 4 -> listarProductos();
                case 0 -> volver = true;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    /**
     * HU-06: Alta de producto.
     * Lista categorías activas para seleccionar, solicita nombre, precio, descripción y stock.
     * Valida que precio sea positivo y stock no negativo. Resuelve la relación @ManyToOne.
     */
    private static void altaProducto() {
        System.out.println("\n--- ALTA DE PRODUCTO ---");

        // Listar categorías disponibles para selección
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("✘ No hay categorías activas. Creá una categoría primero.");
            return;
        }
        listarCategorias();

        long catId = leerLong("Seleccioná el ID de la categoría: ");
        Optional<Categoria> optCat = categorias.stream()
                .filter(c -> c.getId().equals(catId))
                .findFirst();

        if (optCat.isEmpty()) {
            System.out.println("✘ Error: no existe una categoría activa con ID " + catId + ".");
            return;
        }

        String nombre = leerTextoObligatorio("Nombre del producto: ");

        double precio = -1;
        while (precio < 0) {
            precio = leerDouble("Precio (número positivo): ");
            if (precio < 0) System.out.println("⚠ El precio debe ser un número positivo.");
        }

        System.out.print("Descripción (opcional, Enter para omitir): ");
        String descripcion = scanner.nextLine().trim();

        int stock = -1;
        while (stock < 0) {
            stock = leerEntero("Stock (número >= 0): ");
            if (stock < 0) System.out.println("⚠ El stock no puede ser negativo.");
        }

        Producto nuevo = new Producto();
        nuevo.setNombre(nombre);
        nuevo.setPrecio(precio);
        nuevo.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        nuevo.setStock(stock);
        nuevo.setCategoria(optCat.get()); // Resuelve la relación @ManyToOne
        nuevo.setDisponible(true);

        Producto guardado = productoRepo.guardar(nuevo);
        System.out.println("✔ Producto '" + guardado.getNombre() + "' creado con ID: " + guardado.getId());
    }

    /**
     * HU-08: Baja lógica de producto.
     * Solicita el ID. Si no existe o ya está dado de baja, muestra error con nombre si aplica.
     */
    private static void bajaProducto() {
        System.out.println("\n--- BAJA LÓGICA DE PRODUCTO ---");
        listarProductos();

        long id = leerLong("Ingresá el ID del producto a eliminar: ");
        Optional<Producto> opt = productoRepo.buscarPorId(id);

        if (opt.isEmpty()) {
            System.out.println("✘ Error: no existe ningún producto con ID " + id + ".");
            return;
        }
        if (opt.get().isEliminado()) {
            System.out.println("✘ Error: el producto '" + opt.get().getNombre() + "' ya está dado de baja.");
            return;
        }

        boolean eliminado = productoRepo.eliminarLogico(id);
        if (eliminado) {
            System.out.println("✔ Producto '" + opt.get().getNombre() + "' (ID " + id + ") dado de baja correctamente.");
        }
    }

    /**
     * HU-07: Modificación de producto.
     * Muestra valores actuales, permite editar nombre, precio y stock con validaciones.
     * Campo vacío conserva el valor anterior.
     */
    private static void modificarProducto() {
        System.out.println("\n--- MODIFICACIÓN DE PRODUCTO ---");
        listarProductos();

        long id = leerLong("Ingresá el ID del producto a modificar: ");
        Optional<Producto> opt = productoRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("✘ Error: no existe un producto activo con ID " + id + ".");
            return;
        }

        Producto producto = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre : " + producto.getNombre());
        System.out.println("  Precio : $" + producto.getPrecio());
        System.out.println("  Stock  : " + producto.getStock());

        // Nombre
        System.out.print("Nuevo nombre (Enter para conservar '" + producto.getNombre() + "'): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) {
            producto.setNombre(nuevoNombre);
        }

        // Precio con validación
        System.out.print("Nuevo precio (Enter para conservar $" + producto.getPrecio() + "): ");
        String precioStr = scanner.nextLine().trim();
        if (!precioStr.isEmpty()) {
            try {
                double nuevoPrecio = Double.parseDouble(precioStr);
                if (nuevoPrecio < 0) {
                    System.out.println("⚠ Precio inválido, se conserva el valor anterior.");
                } else {
                    producto.setPrecio(nuevoPrecio);
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Valor no numérico, se conserva el precio anterior.");
            }
        }

        // Stock con validación
        System.out.print("Nuevo stock (Enter para conservar " + producto.getStock() + "): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(stockStr);
                if (nuevoStock < 0) {
                    System.out.println("⚠ Stock inválido, se conserva el valor anterior.");
                } else {
                    producto.setStock(nuevoStock);
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Valor no numérico, se conserva el stock anterior.");
            }
        }

        productoRepo.guardar(producto);
        System.out.println("✔ Producto ID " + id + " actualizado correctamente.");
    }

    /**
     * Muestra todos los productos activos con ID, nombre, precio, stock y nombre de categoría.
     */
    private static void listarProductos() {
        List<Producto> productos = productoRepo.listarActivos();
        if (productos.isEmpty()) {
            System.out.println("  (No hay productos activos registrados)");
            return;
        }
        System.out.println("\n┌──────┬──────────────────────┬──────────┬───────┬──────────────────────┐");
        System.out.printf( "│ %-4s │ %-20s │ %-8s │ %-5s │ %-20s │%n",
                "ID", "Nombre", "Precio", "Stock", "Categoría");
        System.out.println("├──────┼──────────────────────┼──────────┼───────┼──────────────────────┤");
        for (Producto p : productos) {
            String nombreCat = (p.getCategoria() != null) ? p.getCategoria().getNombre() : "(sin categoría)";
            System.out.printf("│ %-4d │ %-20s │ $%-7.2f │ %-5d │ %-20s │%n",
                    p.getId(),
                    truncar(p.getNombre(), 20),
                    p.getPrecio(),
                    p.getStock(),
                    truncar(nombreCat, 20));
        }
        System.out.println("└──────┴──────────────────────┴──────────┴───────┴──────────────────────┘");
    }

    // =========================================================
    // SUBMENÚ: REPORTES (JPQL personalizada)
    // =========================================================

    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n──────────────── REPORTES ────────────────");
            System.out.println("  1. Productos por categoría (JPQL)");
            System.out.println("  0. Volver al menú principal");
            System.out.println("──────────────────────────────────────────");

            int opcion = leerEntero("Seleccioná una opción: ");
            switch (opcion) {
                case 1 -> reporteProductosPorCategoria();
                case 0 -> volver = true;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    /**
     * HU-09: Consulta JPQL — Productos por categoría.
     * Lista las categorías activas, solicita al usuario que elija una,
     * y muestra los productos activos de esa categoría.
     */
    private static void reporteProductosPorCategoria() {
        System.out.println("\n--- REPORTE: PRODUCTOS POR CATEGORÍA ---");

        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("✘ No hay categorías activas disponibles.");
            return;
        }

        listarCategorias();
        long catId = leerLong("Seleccioná el ID de la categoría: ");

        Optional<Categoria> optCat = categorias.stream()
                .filter(c -> c.getId().equals(catId))
                .findFirst();

        if (optCat.isEmpty()) {
            System.out.println("✘ Error: no existe una categoría activa con ID " + catId + ".");
            return;
        }

        // Llamada al método JPQL en ProductoRepository
        List<Producto> productos = productoRepo.buscarPorCategoria(catId);

        System.out.println("\nProductos activos en categoría '" + optCat.get().getNombre() + "':");

        if (productos.isEmpty()) {
            System.out.println("  (No hay productos activos en esta categoría)");
            return;
        }

        System.out.println("┌──────┬──────────────────────┬──────────┬───────┐");
        System.out.printf( "│ %-4s │ %-20s │ %-8s │ %-5s │%n", "ID", "Nombre", "Precio", "Stock");
        System.out.println("├──────┼──────────────────────┼──────────┼───────┤");
        for (Producto p : productos) {
            System.out.printf("│ %-4d │ %-20s │ $%-7.2f │ %-5d │%n",
                    p.getId(),
                    truncar(p.getNombre(), 20),
                    p.getPrecio(),
                    p.getStock());
        }
        System.out.println("└──────┴──────────────────────┴──────────┴───────┘");
    }

    // =========================================================
    // MÉTODOS AUXILIARES DE LECTURA Y FORMATO
    // =========================================================

    /** Lee un entero de consola, reintentando si el input es inválido. */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Ingresá un número entero válido.");
            }
        }
    }

    /** Lee un Long de consola, reintentando si el input es inválido. */
    private static long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Ingresá un número válido.");
            }
        }
    }

    /** Lee un Double de consola, reintentando si el input es inválido. */
    private static double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("⚠ Ingresá un número decimal válido (ej: 19.99).");
            }
        }
    }

    /** Lee un texto no vacío de consola, reintentando si está en blanco. */
    private static String leerTextoObligatorio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("⚠ Este campo es obligatorio, no puede estar vacío.");
        }
    }

    /** Trunca un texto al largo máximo indicado para mostrar en tablas. */
    private static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max - 1) + "…" : texto;
    }
}
