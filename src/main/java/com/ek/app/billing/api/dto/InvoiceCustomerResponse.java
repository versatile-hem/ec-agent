package com.ek.app.billing.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCustomerResponse {

    private String name;
    private String gstin;
    private String state;
}
