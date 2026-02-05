package com.ek.app.productcatalog.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Table(name = "product",
       indexes = {
           @Index(name = "idx_product_sku", columnList = "sku", unique = true),
           @Index(name = "idx_product_active", columnList = "is_active")
       })
public class Product extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", updatable = false, nullable = false)
    private Long productId;

    @Column(name = "sku", length = 64, nullable = false, unique = true)
    private String sku;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "product_title", length = 1255, nullable = false)
    private String product_title;
 

    @Column(name = "barcode", length = 64)
    private String barcode;

    @Column(name = "category", length = 64)
    private String category;

    @Column(name = "hsn", length = 64)
    private String hsn;

    @Column(name = "tax_code", length = 64)
    private String tax_code;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "weight_grams", precision = 12, scale = 3)
    private java.math.BigDecimal weightGrams;

    @Column(name = "mrp", precision = 12, scale = 3)
    private java.math.BigDecimal mrp;

    @Column(name = "volume_cc", precision = 12, scale = 3)
    private java.math.BigDecimal volumeCc;
}
