package az.tarkhangurbanli.payment.model.entity;

import az.tarkhangurbanli.payment.model.enums.PaymentStatus;
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
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(length = 36)
    private String paymentId;

    @Column(nullable = false, length = 36)
    private String sagaId;

    @Column(nullable = false, length = 36)
    private String orderId;

    @Column(nullable = false, length = 36)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(length = 500)
    private String failureReason;

    @Column(length = 500)
    private String refundReason;

    @Column(nullable = false)
    private Integer retryCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime refundedAt;

    @Version
    private Long version;

    public Payment(String paymentId,
                   String sagaId,
                   String orderId,
                   String customerId,
                   BigDecimal amount,
                   PaymentStatus status,
                   Integer retryCount) {
        this.paymentId = paymentId;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.retryCount = retryCount;
    }

}
