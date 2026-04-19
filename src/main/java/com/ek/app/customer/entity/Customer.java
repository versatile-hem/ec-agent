package com.ek.app.customer.entity;

import java.time.Instant;
import java.math.BigDecimal;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "customer", indexes = {
    @Index(name = "idx_customer_cp_id", columnList = "cp_id", unique = true),
        @Index(name = "idx_customer_phone", columnList = "phone"),
        @Index(name = "idx_customer_gstin", columnList = "gstin")
})
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cp_id", length = 20, unique = true)
    private String cpId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String contactName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String gstin;

    @Column(length = 20)
    private String pan;

    @Column(length = 255)
    private String addressLine1;

    @Column(length = 255)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String pincode;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String area;

    @Column(precision = 12, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 12, scale = 8)
    private BigDecimal longitude;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
