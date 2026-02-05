package com.ek.app.inventory.infra.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ek.app.inventory.domain.InventoryType;
import com.ek.app.productcatalog.db.Product;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@jakarta.persistence.Table(name = "inventory_movement", uniqueConstraints = @UniqueConstraint(name = "uk_inventory_location", columnNames = {
        "location" })

)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private InventoryType movementType;

    @Column(name = "quantity", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantity;

    @Column(name = "reference")
    private String reference; // orderId / adjustment reason

    @Column(name = "location")
    private String location; // orderId / adjustment reason

    @Column(name = "movement_time", nullable = false)
    private LocalDateTime movementTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Optional: snapshot after movement
    @Column(name = "on_hand_after", precision = 18, scale = 6)
    private BigDecimal onHandAfter;

    @Column(name = "reserved_after", precision = 18, scale = 6)
    private BigDecimal reservedAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_channel")
    private SalesChannel salesChannel;

}
