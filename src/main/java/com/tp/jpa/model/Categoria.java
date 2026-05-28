package com.tp.jpa.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// MUY IMPORTANTE: Excluir 'productos' para evitar bucles infinitos en consola
@ToString(callSuper = true, exclude = {"productos"})
@EqualsAndHashCode(callSuper = true, exclude = {"productos"})
@SuperBuilder
public class Categoria extends Base {

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // Relación Bidireccional: Una categoría tiene muchos productos
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    public void addProducto(Producto p) {
        this.productos.add(p);
        p.setCategoria(this); // Sincroniza el lado propietario (Producto)
    }
}
