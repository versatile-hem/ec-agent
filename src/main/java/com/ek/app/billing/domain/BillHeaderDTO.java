package com.ek.app.billing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Bill Header Data")
public class BillHeaderDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "INV-1001")
    private String billNo;

    @Schema(example = "Rahul Sharma")
    private String customerName;

    private Long customerId;

    private String customerGstin;

    private String customerState;

    @Schema(example = "9876543210")
    private String customerPhone;

    @Schema(example = "2026-04-17T14:30:00")
    private LocalDateTime billDate;

    @Schema(example = "500")
    private BigDecimal subtotal;

    @Schema(example = "54")
    private BigDecimal taxAmount;

    @Schema(example = "20")
    private BigDecimal discountAmount;

    @Schema(example = "534")
    private BigDecimal totalAmount;

    @Schema(example = "UPI")
    private String paymentMode;

    @Schema(example = "PAID")
    private String status;

    private List<BillItemDTO> items;
}