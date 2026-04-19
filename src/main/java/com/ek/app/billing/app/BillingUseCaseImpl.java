package com.ek.app.billing.app;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import com.ek.app.billing.domain.BillHeaderDTO;
import com.ek.app.billing.domain.BillItemDTO;
import com.ek.app.billing.infra.db.BillHeader;
import com.ek.app.billing.infra.db.BillHeaderRepository;
import com.ek.app.billing.infra.db.BillItem;
import com.ek.app.productcatalog.infra.db.Product;
import com.ek.app.productcatalog.infra.db.ProductRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillingUseCaseImpl implements BillingUseCase {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final Pattern TAX_RATE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    @Autowired
    private BillHeaderRepository billRepo;

    @Autowired
    private ProductRepository productRepo;

    @Transactional
    @Override
    public Long createBill(BillHeaderDTO dto) {
        BillHeader bill = new BillHeader();
        BeanUtils.copyProperties(dto, bill);

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bill items are required");
        }

        // Items
        List<BillItem> billItems = new ArrayList<>();
        for (BillItemDTO item : dto.getItems()) {
            if (item.getProductId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required for each item");
            }
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found: " + item.getProductId()));
            BillItem bi = new BillItem();
            bi.setBill(bill);
            bi.setProduct(product);
            BeanUtils.copyProperties(item, bi);
            billItems.add(bi);
        }
        bill.setItems(billItems);
        bill = billRepo.save(bill);
        return bill.getId();
    }

    @Transactional
    @Override
    public Long createBillFromProductNames(BillHeaderDTO dto) {
        BillHeader bill = new BillHeader();
        BeanUtils.copyProperties(dto, bill);

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bill items are required");
        }

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (BillItemDTO item : dto.getItems()) {
            if (item.getSku() == null || item.getSku().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sku is required for each item");
            }
            if (item.getUnitPrice() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitPrice is required for each item");
            }

            Product product = productRepo.findBySkuIgnoreCase(item.getSku().trim())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found for sku: " + item.getSku()));

            BigDecimal quantity = item.getQuantity() == null ? BigDecimal.ONE : item.getQuantity();
            BigDecimal unitPrice = item.getUnitPrice();
            BigDecimal gst = resolveGstRateFromTaxCode(product);
 
            BigDecimal taxableValue = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tax = taxableValue.multiply(gst).divide(HUNDRED, 2, RoundingMode.HALF_UP);
            BigDecimal finalAmount = taxableValue.add(tax).setScale(2, RoundingMode.HALF_UP);

            item.setProductId(product.getProductId());
            item.setProductName(product.getName());
            item.setSku(product.getSku());
            item.setBarcode(product.getBarcode());
            item.setCategory(product.getCategory());
            item.setHsn(product.getHsn());
            item.setTax_code(product.getTax_code());
            item.setWeightGrams(product.getWeightGrams());
            item.setMrp(product.getMrp());
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setLineTotal(taxableValue);
            item.setTaxableValue(taxableValue);
            item.setTax(tax);
            item.setFinalAmount(finalAmount);
            item.setGst(gst);

            BillItem bi = new BillItem();
            bi.setBill(bill);
            bi.setProduct(product);
            BeanUtils.copyProperties(item, bi);
            billItems.add(bi);

            subtotal = subtotal.add(taxableValue);
            taxAmount = taxAmount.add(tax);
        }

        bill.setItems(billItems);

        BigDecimal discount = dto.getDiscountAmount() == null ? BigDecimal.ZERO : dto.getDiscountAmount();
        BigDecimal computedTotal = subtotal.add(taxAmount).subtract(discount).setScale(2, RoundingMode.HALF_UP);
        bill.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        bill.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
        bill.setDiscountAmount(discount.setScale(2, RoundingMode.HALF_UP));
        bill.setTotalAmount(computedTotal);

        dto.setSubtotal(bill.getSubtotal());
        dto.setTaxAmount(bill.getTaxAmount());
        dto.setDiscountAmount(bill.getDiscountAmount());
        dto.setTotalAmount(bill.getTotalAmount());

        bill = billRepo.save(bill);
        return bill.getId();
    }

    @Override
    @Transactional
    public List<BillHeaderDTO> listBills(LocalDate minusMonths, LocalDate now) {
        return billRepo.findAll()
                .stream()
                .filter(e -> e.getBillDate() != null)
                .filter(e -> {
                    LocalDate billDate = e.getBillDate().toLocalDate();
                    return (minusMonths == null || !billDate.isBefore(minusMonths))
                            && (now == null || !billDate.isAfter(now));
                })
                .map(this::toBillHeaderDto)
                .toList();
    }

    @Override
    @Transactional
    public BillHeaderDTO getBillById(Long id) {
        BillHeader header = billRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found: " + id));
        return toBillHeaderDto(header);
    }

    @Override
    @Transactional
    public BillHeaderDTO updateBill(Long id, String paymentMode, String status) {
        BillHeader header = billRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found: " + id));

        if (paymentMode != null && !paymentMode.isBlank()) {
            header.setPaymentMode(paymentMode.trim());
        }
        if (status != null && !status.isBlank()) {
            header.setStatus(status.trim());
        }

        BillHeader saved = billRepo.save(header);
        return toBillHeaderDto(saved);
    }

    @Override
    @Transactional
    public void deleteBill(Long id) {
        if (!billRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found: " + id);
        }
        billRepo.deleteById(id);
    }


    @Override
     public byte[] generateBill(BillHeaderDTO billHeaderDTO) {
        try {
            Template template = freemarkerConfig.getTemplate("bill.ftl");
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("bill", billHeaderDTO);

            String renderedHtml;
            try (StringWriter writer = new StringWriter()) {
                template.process(dataModel, writer);
                renderedHtml = writer.toString();
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(renderedHtml, null);
                builder.toStream(outputStream);
                builder.run();
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating bill", e);
        }
    }

    public BillingUseCaseImpl(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

     private final Configuration freemarkerConfig;

    private BillHeaderDTO toBillHeaderDto(BillHeader header) {
        log.info("entity : {}", header);
        BillHeaderDTO dto = new BillHeaderDTO();
        dto.setId(header.getId());
        dto.setBillNo(header.getBillNo());
        dto.setCustomerName(header.getCustomerName());
        dto.setCustomerPhone(header.getCustomerPhone());
        dto.setBillDate(header.getBillDate());
        dto.setSubtotal(header.getSubtotal());
        dto.setTaxAmount(header.getTaxAmount());
        dto.setDiscountAmount(header.getDiscountAmount());
        dto.setTotalAmount(header.getTotalAmount());
        dto.setPaymentMode(header.getPaymentMode());
        dto.setStatus(header.getStatus());

        if (header.getItems() != null) {
            dto.setItems(header.getItems().stream().map(this::toBillItemDto).filter(Objects::nonNull).toList());
        }

        return dto;
    }

    private BillItemDTO toBillItemDto(BillItem item) {
        if (item == null) {
            return null;
        }

        BillItemDTO dto = new BillItemDTO();
        dto.setProductId(item.getProduct() != null ? item.getProduct().getProductId() : null);
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal());
        dto.setSku(item.getSku());
        dto.setBarcode(item.getBarcode());
        dto.setCategory(item.getCategory());
        dto.setHsn(item.getHsn());
        dto.setTax_code(item.getTax_code());
        dto.setGst(item.getGst());
        dto.setWeightGrams(item.getWeightGrams());
        dto.setMrp(item.getMrp());
        dto.setTaxableValue(item.getTaxableValue());
        dto.setTax(item.getTax());
        dto.setFinalAmount(item.getFinalAmount());
        return dto;
    }

    private BigDecimal resolveGstRateFromTaxCode(Product product) {
        String taxCode = product.getTax_code();
        if (taxCode == null || taxCode.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "tax_code is required for product sku: " + product.getSku());
        }

        Matcher matcher = TAX_RATE_PATTERN.matcher(taxCode);
        if (!matcher.find()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid tax_code for product sku: " + product.getSku() + " (" + taxCode + ")");
        }

        BigDecimal gstRate = new BigDecimal(matcher.group(1));
        if (gstRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid negative GST in tax_code for sku: " + product.getSku());
        }

        return gstRate;
    }
}
