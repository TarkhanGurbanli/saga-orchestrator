package az.tarkhangurbanli.inventory.model.entity;

import az.tarkhangurbanli.inventory.model.enums.ReservationStatus;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @Column(length = 36)
    private String reservationId;

    @Column(nullable = false, length = 36)
    private String sagaId;

    @Column(nullable = false, length = 36)
    private String orderId;

    @Column(nullable = false, length = 36)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    @Column(length = 500)
    private String failureReason;

    @Column(length = 500)
    private String releaseReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime releasedAt;

    @Version
    private Long version;

    public Reservation(
            String reservationId,
            String sagaId,
            String orderId,
            String productId,
            Integer quantity,
            ReservationStatus reservationStatus) {
        this.reservationId = reservationId;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = reservationStatus;
    }

    public Reservation(
            String reservationId,
            String sagaId,
            String orderId,
            String productId,
            Integer quantity,
            ReservationStatus reservationStatus,
            String reason) {
        this.reservationId = reservationId;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = reservationStatus;
        this.failureReason = reason;
    }

}