package com.ek.app.nexo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nexo_product")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false, unique = true, length = 128)
    private String barcode;

    @Column(name = "hsn_code", length = 64)
    private String hsnCode;

    @Column(nullable = false, length = 32)
    private String unit;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
}
