package com.tp.jpa.model;

import com.tp.jpa.model.enums.Estado;
import com.tp.jpa.model.enums.FormaPago;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Excluimos las relaciones para evitar bucles infinitos (StackOverflowError)
@ToString(callSuper = true, exclude = {"usuario", "detalles"})
@EqualsAndHashCode(callSuper = true, exclude = {"usuario", "detalles"})
@SuperBuilder
public class Pedido extends Base implements Calculable {

    @Builder.Default
    private LocalDate fecha = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Builder.Default
    private Double total = 0.0;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    // Clave foránea hacia Usuario (lado propietario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Composición: Si borro el pedido, se borran sus detalles
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    @Builder.Default
    private Set<DetallePedido> detalles = new HashSet<>();

    @Override
    public void calcularTotal() {
        this.total = this.detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }

    public void addDetallePedido(int cantidad, Producto producto) {
        DetallePedido nuevoDetalle = new DetallePedido(cantidad, producto);
        this.detalles.add(nuevoDetalle);
        calcularTotal();
    }
}
