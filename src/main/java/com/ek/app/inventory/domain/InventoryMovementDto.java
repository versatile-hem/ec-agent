package com.ek.app.inventory.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ek.app.inventory.infra.db.SalesChannel;

import lombok.Data;

@Data
public class InventoryMovementDto {

    private Long id;

    private Long productId;

    private InventoryType movementType;

    private BigDecimal quantity;

    private String reference; // orderId / adjustment reason
    private String location; // orderId / adjustment reason

    private LocalDateTime movementTime;

    private LocalDateTime createdAt = LocalDateTime.now();

    private BigDecimal onHandAfter;

    private BigDecimal reservedAfter;

    private SalesChannel salesChannel;

}
