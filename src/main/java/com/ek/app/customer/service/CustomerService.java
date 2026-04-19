package com.ek.app.customer.service;

import java.util.List;

import com.ek.app.customer.service.dto.CustomerCreateRequest;
import com.ek.app.customer.service.dto.CustomerResponse;
import com.ek.app.customer.service.dto.CustomerSummaryResponse;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest request);

    List<CustomerSummaryResponse> listAll();

    CustomerResponse getById(Long id);

    List<CustomerSummaryResponse> search(String query);
}
