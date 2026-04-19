package com.ek.app.customer.controller;

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

import com.ek.app.customer.service.CustomerService;
import com.ek.app.customer.service.dto.CustomerCreateRequest;
import com.ek.app.customer.service.dto.CustomerResponse;
import com.ek.app.customer.service.dto.CustomerSummaryResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest request) {
        return customerService.create(request);
    }

    @GetMapping
    public List<CustomerSummaryResponse> listAll() {
        return customerService.listAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @GetMapping("/search")
    public List<CustomerSummaryResponse> search(@RequestParam String query) {
        return customerService.search(query);
    }
}
