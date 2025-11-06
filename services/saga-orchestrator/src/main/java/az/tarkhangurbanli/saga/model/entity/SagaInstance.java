package az.tarkhangurbanli.saga.model.entity;

import az.tarkhangurbanli.saga.model.enums.SagaStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaInstance {

    @Id
    @Column(length = 36)
    private String sagaId;

    @Column(nullable = false, length = 36)
    private String customerId;

    @Column(length = 36)
    private String orderId;

    @Column(nullable = false, length = 36)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SagaStatus status;

    @Column(length = 36)
    private String paymentId;

    @Column(length = 36)
    private String reservationId;

    @Column(length = 1000)
    private String failureReason;

    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime completedAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "sagaInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SagaStep> steps = new ArrayList<>();

    public void addStep(SagaStep step) {
        steps.add(step);
        step.setSagaInstance(this);
    }
}
