package com.ek.app.billing.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceResponse {

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
    private InvoiceCustomerResponse customer;
    private List<BillItemResponse> items;
}
