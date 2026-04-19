package com.ek.app.sales.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.sales.dto.AddPaymentRequest;
import com.ek.app.sales.dto.PaymentResponse;
import com.ek.app.sales.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse add(@Valid @RequestBody AddPaymentRequest request) {
        return paymentService.add(request);
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> listByOrder(@PathVariable Long orderId) {
        return paymentService.listByOrder(orderId);
    }
}
