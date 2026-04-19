package com.ek.app.sales.entity;

import java.math.BigDecimal;

import com.ek.app.productcatalog.infra.db.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "base_amount", precision = 18, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "gst_rate", precision = 6, scale = 2)
    private BigDecimal gstRate;

    @Column(name = "gst_amount", precision = 18, scale = 2)
    private BigDecimal gstAmount;

    @Column(name = "cgst_amount", precision = 18, scale = 2)
    private BigDecimal cgstAmount;

    @Column(name = "sgst_amount", precision = 18, scale = 2)
    private BigDecimal sgstAmount;

    @Column(name = "igst_amount", precision = 18, scale = 2)
    private BigDecimal igstAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal total;
}
