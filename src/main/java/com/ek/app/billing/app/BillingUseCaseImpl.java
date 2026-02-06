package com.ek.app.billing.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ek.app.billing.domain.BillHeaderDTO;
import com.ek.app.billing.domain.BillItemDTO;
import com.ek.app.billing.infra.db.BillHeader;
import com.ek.app.billing.infra.db.BillHeaderRepository;
import com.ek.app.billing.infra.db.BillItem;
import com.ek.app.productcatalog.db.Product;
import com.ek.app.productcatalog.db.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillingUseCaseImpl implements BillingUseCase {

    @Autowired
    private BillHeaderRepository billRepo;

    @Autowired
    private ProductRepository productRepo;

    @Transactional
    @Override
    public void createBill(BillHeaderDTO dto) {
        BillHeader bill = new BillHeader();
        BeanUtils.copyProperties(dto, bill);
        // Items
        List<BillItem> billItems = new ArrayList<>();
        for (BillItemDTO item : dto.getItems()) {
            Product product = productRepo.findById(item.getProductId()).orElseThrow();
            BillItem bi = new BillItem();
            bi.setBill(bill);
            bi.setProduct(product);
            BeanUtils.copyProperties(dto, bi);
            billItems.add(bi);
        }
        bill.setItems(billItems);
        bill = billRepo.save(bill);
    }

    @Override
    @Transactional
    public List<BillHeaderDTO> listBills(LocalDate minusMonths, LocalDate now) {
        return billRepo.findAll()
                .stream()
                .map(e -> {
                    log.info("entity : {}", e);
                    BillHeaderDTO dto = new BillHeaderDTO();
                    dto.setId(e.getId());
                    dto.setBillNo(e.getBillNo());
                    dto.setCustomerName(e.getCustomerName());
                    dto.setCustomerPhone(e.getCustomerPhone());
                    dto.setBillDate(e.getBillDate());
                    dto.setSubtotal(e.getSubtotal());
                    dto.setTaxAmount(e.getTaxAmount());
                    dto.setDiscountAmount(e.getDiscountAmount());
                    dto.setTotalAmount(e.getTotalAmount());
                    dto.setPaymentMode(e.getPaymentMode());
                    dto.setStatus(e.getStatus());
                    // Items mapping (if present)
                    if (e.getItems() != null) {
                        dto.setItems(
                                e.getItems().stream()
                                        .map(i -> {
                                            BillItemDTO item = new BillItemDTO();
                                            // item.setId(i.getId());
                                            item.setProductName(i.getProductName());
                                            item.setHsn(i.getHsn());
                                            item.setQuantity(i.getQuantity());
                                            item.setUnitPrice(i.getUnitPrice());
                                            item.setTaxableValue(i.getTaxableValue());
                                            item.setGst(i.getGst());
                                            item.setTax(i.getTax());
                                            item.setFinalAmount(i.getFinalAmount());
                                            return item;
                                        })
                                        .toList());
                    }

                    return dto;

                })
                .toList();
    }

}
