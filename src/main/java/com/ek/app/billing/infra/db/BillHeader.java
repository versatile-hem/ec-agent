package com.ek.app.billing.infra.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bill_header")
@Data
public class BillHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String billNo;

    private String customerName;
    
    private String customerPhone;

    private LocalDateTime billDate;

    private BigDecimal subtotal;
    
    private BigDecimal taxAmount;
    
    private BigDecimal discountAmount;
    
    private BigDecimal totalAmount;

    private String paymentMode;
    
    private String status;

    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<BillItem> items;
}
