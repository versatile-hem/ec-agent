package com.ek.app.sales.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSalesOrderRequest {

    @NotNull
    private Long customerId;

    @Valid
    @NotEmpty
    private List<SalesOrderItemRequest> items;
}
