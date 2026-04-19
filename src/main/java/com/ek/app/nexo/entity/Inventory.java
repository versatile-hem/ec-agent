package com.ek.app.nexo.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nexo_inventory")
public class Inventory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "warehouse_id", length = 100)
    private String warehouseId;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @PrePersist
    @PreUpdate
    void touchLastUpdated() {
        this.lastUpdated = Instant.now();
    }
}
