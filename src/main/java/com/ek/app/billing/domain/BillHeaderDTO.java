package com.ek.app.billing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillHeaderDTO {
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

    private List<BillItemDTO> items = new ArrayList<>();
}
