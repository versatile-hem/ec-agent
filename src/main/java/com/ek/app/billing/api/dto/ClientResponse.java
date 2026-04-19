package com.ek.app.billing.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponse {

    private String customerName;
    private String customerPhone;
}
