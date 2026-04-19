package com.ek.app.customer.service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerCreateRequest {

    private String cpId;

    @NotBlank(message = "name is required")
    private String name;

    private String contactName;

    @NotBlank(message = "phone is required")
    private String phone;

    private String email;

    @Pattern(regexp = "^$|^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GSTIN format")
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
}
