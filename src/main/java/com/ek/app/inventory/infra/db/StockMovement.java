package com.ek.app.inventory.infra.db;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_movement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType type; // IN or OUT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementReference reference; // Manual, Order, Return, Adjustment

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private String createdBy; // userId

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "movement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockMovementItem> items;

    public enum StockMovementType {
        IN, OUT
    }

    public enum StockMovementReference {
        MANUAL, ORDER, RETURN, ADJUSTMENT, SUPPLIER, DAMAGE, TRANSFER, MEESHO, FLIPKART
    }
}
