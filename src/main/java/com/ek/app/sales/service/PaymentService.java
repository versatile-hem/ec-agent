package com.ek.app.sales.service;

import java.util.List;

import com.ek.app.sales.dto.AddPaymentRequest;
import com.ek.app.sales.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse add(AddPaymentRequest request);

    List<PaymentResponse> listByOrder(Long orderId);
}
