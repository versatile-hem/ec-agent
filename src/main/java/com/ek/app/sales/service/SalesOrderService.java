package com.ek.app.sales.service;

import java.util.List;

import com.ek.app.sales.dto.CreateSalesOrderRequest;
import com.ek.app.sales.dto.SalesOrderResponse;

public interface SalesOrderService {

    SalesOrderResponse create(CreateSalesOrderRequest request);

    List<SalesOrderResponse> list(String createdBy);

    SalesOrderResponse getById(Long id);
}
