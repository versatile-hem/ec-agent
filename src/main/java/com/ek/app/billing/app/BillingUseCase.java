package com.ek.app.billing.app;

import java.time.LocalDate;
import java.util.List;

import com.ek.app.billing.domain.BillHeaderDTO;


public interface BillingUseCase {

    Long createBill(BillHeaderDTO dto);

    Long createBillFromProductNames(BillHeaderDTO dto);

    List<BillHeaderDTO> listBills(LocalDate minusMonths, LocalDate now);

    BillHeaderDTO getBillById(Long id);

    BillHeaderDTO updateBill(Long id, String paymentMode, String status);

    void deleteBill(Long id);

    byte[] generateBill(BillHeaderDTO billHeaderDTO);

}
