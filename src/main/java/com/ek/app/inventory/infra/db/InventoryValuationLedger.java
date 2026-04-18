package com.ek.app.inventory.infra.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InventoryValuationLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private com.ek.app.productcatalog.infra.db.Product product;

    private String referenceType;
    // PURCHASE, SALE, RETURN, ADJUSTMENT

    private Long referenceId;

    @Column(precision = 19, scale = 4)
    private BigDecimal quantityIn;

    @Column(precision = 19, scale = 4)
    private BigDecimal quantityOut;

    @Column(precision = 19, scale = 4)
    private BigDecimal unitCostExclGst;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalValueChange;

    @Column(precision = 19, scale = 4)
    private BigDecimal runningQuantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal runningValue;

    private LocalDateTime createdAt;
}
