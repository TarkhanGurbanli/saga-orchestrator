package az.tarkhangurbanli.orders.model.entity;

import az.tarkhangurbanli.orders.model.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @Column(length = 36)
    private String orderId;

    @Column(nullable = false, length = 36)
    private String sagaId;

    @Column(nullable = false, length = 36)
    private String customerId;

    @Column(nullable = false, length = 36)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(length = 500)
    private String cancellationReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Order(String orderId,
                 String sagaId,
                 String customerId,
                 String productId,
                 Integer quantity,
                 BigDecimal amount,
                 OrderStatus orderStatus) {
        this.orderId = orderId;
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.status = orderStatus;
    }

}
