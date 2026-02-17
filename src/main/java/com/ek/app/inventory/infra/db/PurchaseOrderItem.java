package com.ek.app.inventory.infra.db;

import java.math.BigDecimal;

import com.vaadin.pro.licensechecker.Product;

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
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    private com.ek.app.productcatalog.infra.db.Product  product;

    private BigDecimal quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal unitPriceExclGst;

    @Column(precision = 5, scale = 2)
    private BigDecimal gstRate;

    @Column(precision = 19, scale = 4)
    private BigDecimal gstAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmountInclGst;
}
