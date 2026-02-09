package com.ek.app.billing.infra.db;

import java.math.BigDecimal;

import com.ek.app.productcatalog.infra.db.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bill_item")
@Data
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private BillHeader bill;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;

    private String productName;

    private String sku;

    private String barcode;

    private String category;

    private String hsn;

    private String tax_code;

    private BigDecimal gst;

    private java.math.BigDecimal weightGrams;

    private java.math.BigDecimal mrp;

    private BigDecimal taxableValue;

    private BigDecimal tax;

    private BigDecimal finalAmount;
}
