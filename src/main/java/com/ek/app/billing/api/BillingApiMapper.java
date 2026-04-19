package com.ek.app.billing.api;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ek.app.billing.api.dto.BillItemResponse;
import com.ek.app.billing.api.dto.BillingGenerateRequest;
import com.ek.app.billing.api.dto.InvoiceResponse;
import com.ek.app.billing.domain.BillHeaderDTO;
import com.ek.app.billing.domain.BillItemDTO;

@Component
public class BillingApiMapper {

    public BillHeaderDTO toDomain(BillingGenerateRequest request) {
        BillHeaderDTO dto = new BillHeaderDTO();
        dto.setBillNo(request.getBillNo());
        dto.setCustomerName(request.getCustomerName());
        dto.setCustomerPhone(request.getCustomerPhone());
        dto.setBillDate(request.getBillDate());
        dto.setDiscountAmount(request.getDiscountAmount());
        dto.setPaymentMode(request.getPaymentMode());
        dto.setStatus(request.getStatus());

        List<BillItemDTO> items = request.getItems().stream().map(item -> {
            BillItemDTO billItemDTO = new BillItemDTO();
            billItemDTO.setSku(item.getSku());
            billItemDTO.setQuantity(item.getQuantity());
            billItemDTO.setUnitPrice(item.getUnitPrice());
            return billItemDTO;
        }).toList();

        dto.setItems(items);
        return dto;
    }

    public InvoiceResponse toInvoiceResponse(BillHeaderDTO dto) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(dto.getId());
        response.setBillNo(dto.getBillNo());
        response.setCustomerName(dto.getCustomerName());
        response.setCustomerPhone(dto.getCustomerPhone());
        response.setBillDate(dto.getBillDate());
        response.setSubtotal(dto.getSubtotal());
        response.setTaxAmount(dto.getTaxAmount());
        response.setDiscountAmount(dto.getDiscountAmount());
        response.setTotalAmount(dto.getTotalAmount());
        response.setPaymentMode(dto.getPaymentMode());
        response.setStatus(dto.getStatus());

        List<BillItemResponse> items = dto.getItems() == null
                ? Collections.emptyList()
                : dto.getItems().stream().map(this::toBillItemResponse).toList();
        response.setItems(items);

        return response;
    }

    private BillItemResponse toBillItemResponse(BillItemDTO dto) {
        BillItemResponse item = new BillItemResponse();
        item.setProductId(dto.getProductId());
        item.setProductName(dto.getProductName());
        item.setSku(dto.getSku());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setTaxableValue(dto.getTaxableValue());
        item.setGst(dto.getGst());
        item.setTax(dto.getTax());
        item.setFinalAmount(dto.getFinalAmount());
        return item;
    }
}
