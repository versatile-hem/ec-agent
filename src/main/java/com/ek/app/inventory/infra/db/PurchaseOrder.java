package com.ek.app.inventory.infra.db;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class PurchaseOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String poNumber;

    @ManyToOne
    private Supplier supplier;

    private LocalDate poDate;

    private String status; // DRAFT, APPROVED, RECEIVED

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmountExclGst;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalGstAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmountInclGst;
}
