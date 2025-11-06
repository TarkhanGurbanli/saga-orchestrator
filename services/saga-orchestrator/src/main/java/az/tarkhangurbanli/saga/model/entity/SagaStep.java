package az.tarkhangurbanli.saga.model.entity;

import az.tarkhangurbanli.saga.model.enums.SagaStepStatus;
import az.tarkhangurbanli.saga.model.enums.SagaStepType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_id", nullable = false)
    private SagaInstance sagaInstance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SagaStepType stepType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SagaStepStatus status;

    @Column(nullable = false)
    private Integer stepOrder;

    @Column(length = 36)
    private String commandId;

    @Column(length = 36)
    private String eventId;

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
}
