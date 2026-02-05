package com.ek.app.billing.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItemDTO {

    private Long productId;
    private String productName;

    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    private String sku;

    private String product_title;

    private String barcode;

    private String category;

    private String hsn;

    private String tax_code;

    private BigDecimal gst;

    private java.math.BigDecimal weightGrams;

    private java.math.BigDecimal mrp;

    private Long availableStock;

    private BigDecimal taxableValue ;

    private BigDecimal tax ;

    private BigDecimal finalAmount;

}
