package com.ek.app.sales.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.sales.dto.CreateSalesOrderRequest;
import com.ek.app.sales.dto.SalesOrderResponse;
import com.ek.app.sales.service.SalesOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrderResponse create(@Valid @RequestBody CreateSalesOrderRequest request) {
        return salesOrderService.create(request);
    }

    @GetMapping
    public List<SalesOrderResponse> list(@RequestParam(required = false) String createdBy) {
        return salesOrderService.list(createdBy);
    }

    @GetMapping("/{id}")
    public SalesOrderResponse getById(@PathVariable Long id) {
        return salesOrderService.getById(id);
    }
}
