package com.tp.jpa.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"categoria"}) // Excluimos la relación del toString
@EqualsAndHashCode(callSuper = true, exclude = {"categoria"})
@SuperBuilder
public class Producto extends Base {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private int stock;

    private String imagen; // Puede ser null si no hay imagen

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true; // Por defecto el producto está disponible

    // LA RELACIÓN CON CATEGORÍA (Muchos a Uno)
    // Muchos productos pertenecen a una Categoría.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
