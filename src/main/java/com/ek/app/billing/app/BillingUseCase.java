package com.ek.app.billing.app;

import java.time.LocalDate;
import java.util.List;

import com.ek.app.billing.domain.BillHeaderDTO;


public interface BillingUseCase {

    Long createBill(BillHeaderDTO dto);

    Long createBillFromProductNames(BillHeaderDTO dto);

    List<BillHeaderDTO> listBills(LocalDate minusMonths, LocalDate now);

    byte[] generateBill(BillHeaderDTO billHeaderDTO);

}
