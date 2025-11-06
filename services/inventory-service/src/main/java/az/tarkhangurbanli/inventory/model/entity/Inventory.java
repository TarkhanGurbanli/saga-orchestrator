package az.tarkhangurbanli.inventory.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @Column(length = 36)
    private String productId;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Inventory(String productId, String productName, int availableQuantity, int reservedQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

}