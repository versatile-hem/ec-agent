package com.ek.app.customer.service.dto;

import java.time.Instant;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class CustomerResponse {

    private Long id;
    private String cpId;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String gstin;
    private String pan;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String area;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Instant createdAt;
    private Instant updatedAt;
}
