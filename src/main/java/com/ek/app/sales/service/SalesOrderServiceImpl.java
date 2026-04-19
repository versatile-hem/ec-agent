package com.ek.app.sales.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.customer.entity.Customer;
import com.ek.app.customer.repository.CustomerRepository;
import com.ek.app.productcatalog.infra.db.Product;
import com.ek.app.productcatalog.infra.db.ProductRepository;
import com.ek.app.sales.dto.CreateSalesOrderRequest;
import com.ek.app.sales.dto.SalesOrderItemResponse;
import com.ek.app.sales.dto.SalesOrderResponse;
import com.ek.app.sales.entity.PaymentStatus;
import com.ek.app.sales.entity.SalesOrder;
import com.ek.app.sales.entity.SalesOrderItem;
import com.ek.app.sales.entity.SalesOrderStatus;
import com.ek.app.sales.repository.SalesOrderRepository;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AuthContextService authContextService;

    public SalesOrderServiceImpl(
            SalesOrderRepository salesOrderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            AuthContextService authContextService) {
        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.authContextService = authContextService;
    }

    @Override
    @Transactional
    public SalesOrderResponse create(CreateSalesOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        SalesOrder order = new SalesOrder();
        order.setOrderNumber("SO-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        order.setCreatedBy(authContextService.username());
        order.setOrderDate(LocalDate.now());
        order.setStatus(SalesOrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            item.setTotal(itemRequest.getPrice().multiply(itemRequest.getQuantity()));
            order.getItems().add(item);

            total = total.add(item.getTotal());
        }

        order.setTotalAmount(total);
        return toResponse(salesOrderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderResponse> list(String createdBy) {
        if (authContextService.isAdmin()) {
            if (createdBy != null && !createdBy.isBlank()) {
                return salesOrderRepository.findByCreatedByOrderByCreatedAtDesc(createdBy).stream().map(this::toResponse).toList();
            }
            return salesOrderRepository.findAll().stream().map(this::toResponse).toList();
        }

        String effectiveCreatedBy = authContextService.username();
        return salesOrderRepository.findByCreatedByOrderByCreatedAtDesc(effectiveCreatedBy).stream().map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderResponse getById(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!authContextService.isAdmin() && !order.getCreatedBy().equalsIgnoreCase(authContextService.username())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this order");
        }

        return toResponse(order);
    }

    private SalesOrderResponse toResponse(SalesOrder order) {
        return SalesOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .createdBy(order.getCreatedBy())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> SalesOrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .total(item.getTotal())
                        .build()).toList())
                .build();
    }
}
