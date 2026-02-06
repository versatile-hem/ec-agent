package com.ek.app.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ek.app.billing.app.BillingUseCase;
import com.ek.app.billing.domain.BillHeaderDTO;
import com.ek.app.billing.domain.BillItemDTO;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
@Route(value = "bill-book", layout = MainLayout.class)
public class BillBookView extends VerticalLayout {

        private final com.vaadin.flow.component.grid.Grid<BillHeaderDTO> grid = new com.vaadin.flow.component.grid.Grid<>(
                        BillHeaderDTO.class, false);
        private final TextField search = new TextField();
        private final Button newBill = new Button("New Bill", VaadinIcon.PLUS.create());

        private BillingUseCase billingUseCase;

        public BillBookView(BillingUseCase billingUseCase) {
                this.billingUseCase = billingUseCase;
                setSizeFull();

                // configureToolbar();
                configureGrid();

                add(createToolbar(), grid);
                expand(grid);

                loadBills(null);

        }

        private Component createToolbar() {

                search.setPlaceholder("Search bill no / customer...");
                search.setClearButtonVisible(true);
                search.setPrefixComponent(VaadinIcon.SEARCH.create());

                search.addValueChangeListener(e -> loadBills(e.getValue()));

                newBill.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                newBill.addClickListener(e -> openCreateBillDialog());

                HorizontalLayout toolbar = new HorizontalLayout(search, newBill);
                toolbar.setWidthFull();
                toolbar.expand(search);

                return toolbar;
        }

        private void configureGrid() {

                grid.addColumn(BillHeaderDTO::getBillNo)
                                .setHeader("Bill No")
                                .setAutoWidth(true);

                grid.addColumn(BillHeaderDTO::getCustomerName)
                                .setHeader("Customer");

                grid.addColumn(dto -> dto.getBillDate().toLocalDate())
                                .setHeader("Date");

                grid.addColumn(BillHeaderDTO::getTotalAmount)
                                .setHeader("Total")
                                .setTextAlign(ColumnTextAlign.END);

                grid.addColumn(BillHeaderDTO::getStatus)
                                .setHeader("Status");

                grid.addComponentColumn(this::createActions)
                                .setHeader("Actions")
                                .setFlexGrow(0)
                                .setWidth("120px");

                grid.setSizeFull();

                grid.addThemeVariants(
                                GridVariant.LUMO_ROW_STRIPES,
                                GridVariant.LUMO_COLUMN_BORDERS,
                                GridVariant.LUMO_COMPACT);
        }

        private Component createActions(BillHeaderDTO dto) {

                Button view = new Button(VaadinIcon.EYE.create(),
                                e -> openBill(dto));

                Button pdf = new Button(VaadinIcon.FILE_PRESENTATION.create(),
                                e -> downloadPdf(dto));

                view.addThemeVariants(ButtonVariant.LUMO_SMALL);
                pdf.addThemeVariants(ButtonVariant.LUMO_SMALL);

                HorizontalLayout layout = new HorizontalLayout(view, pdf);
                layout.setSpacing(false);

                return layout;
        }

        private Object downloadPdf(BillHeaderDTO dto) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'downloadPdf'");
        }

        private Object openBill(BillHeaderDTO dto) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'openBill'");
        }

        private void loadBills(String filter) {

                List<BillHeaderDTO> bills = billingUseCase.listBills(LocalDate.now().minusMonths(1),
                                LocalDate.now());

                if (filter != null && !filter.isBlank()) {
                        bills = bills.stream()
                                        .filter(b -> b.getBillNo().contains(filter)
                                                        || (b.getCustomerName() != null &&
                                                                        b.getCustomerName().toLowerCase()
                                                                                        .contains(filter.toLowerCase())))
                                        .toList();
                }

                grid.setItems(bills);
        }

        private void openCreateBillDialog() {

                Dialog dialog = new Dialog();
                dialog.setWidth("80%");
                dialog.setHeight("600px");

                // Customer section
                TextField customerName = new TextField("Customer Name");
                TextField phone = new TextField("Phone");
                HorizontalLayout customerRow = new HorizontalLayout(customerName, phone);

                // Items grid (temporary DTO)
                com.vaadin.flow.component.grid.Grid<BillItemDTO> itemGrid = new com.vaadin.flow.component.grid.Grid<>(
                                BillItemDTO.class, false);

                // itemGrid.addColumn("").setHeader("Sr No.");
                itemGrid.addColumn(BillItemDTO::getProduct_title).setHeader("Product");
                itemGrid.addColumn(BillItemDTO::getHsn).setHeader("HSN");
                itemGrid.addColumn(BillItemDTO::getQuantity).setHeader("Qty");
                itemGrid.addColumn(BillItemDTO::getUnitPrice).setHeader("Rate");
                itemGrid.addColumn(BillItemDTO::getTaxableValue).setHeader("Taxable Value");
                itemGrid.addColumn(BillItemDTO::getGst).setHeader("GST %");
                itemGrid.addColumn(BillItemDTO::getTax).setHeader("GST Amt.");
                itemGrid.addColumn(BillItemDTO::getFinalAmount).setHeader("Amount");
                itemGrid.setHeight("300px");

                List<BillItemDTO> items = new ArrayList<>();
                itemGrid.setItems(items);

                Button addItem = new Button("Add Item", VaadinIcon.PLUS.create());

                addItem.addClickListener(e -> {
                        BillItemDTO item = new BillItemDTO();
                        // SAMPLE DATA (later replace with product picker)
                        item.setProduct_title("Java Programming Book");
                        item.setHsn("4901");
                        item.setQuantity(BigDecimal.ONE);
                        item.setUnitPrice(new BigDecimal("250"));
                        item.setGst(new BigDecimal("18")); // percent
                        item.setProductId(1l);

                        // Calculations
                        BigDecimal taxable = item.getQuantity().multiply(item.getUnitPrice());
                        BigDecimal gstAmount = taxable.multiply(item.getGst())
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        BigDecimal finalAmount = taxable.add(gstAmount);
                        item.setTaxableValue(taxable);
                        item.setTax(gstAmount);
                        item.setFinalAmount(finalAmount);
                        items.add(item);
                        itemGrid.getDataProvider().refreshAll();
                });

                // Payment
                ComboBox<String> paymentMode = new ComboBox<>("Payment Mode");
                paymentMode.setItems("CASH", "CARD", "UPI");

                TextField total = new TextField("Total");
                total.setReadOnly(true);

                // Buttons
                Button save = new Button("Save", VaadinIcon.CHECK.create());
                Button cancel = new Button("Cancel");

                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                save.addClickListener(e -> {
                        openBillItemDialog();

                });

                cancel.addClickListener(e -> dialog.close());

                HorizontalLayout actions = new HorizontalLayout(save, cancel);

                VerticalLayout layout = new VerticalLayout(
                                new H4("Create Bill"),
                                customerRow,
                                addItem,
                                itemGrid,
                                paymentMode,
                                total,
                                actions);

                dialog.add(layout);
                dialog.open();
        }

        private void openBillItemDialog() {

                Dialog dialog = new Dialog();
                dialog.setWidth("80%");
                dialog.setHeight("600px");

                // Customer section
                TextField customerName = new TextField("Customer Name");
                TextField phone = new TextField("Phone");
                HorizontalLayout customerRow = new HorizontalLayout(customerName, phone);

                // HorizontalLayout actions = new HorizontalLayout(save, cancel);

                VerticalLayout layout = new VerticalLayout(
                                new H4("Add Item Bill"),
                                customerRow

                );

                dialog.add(layout);
                dialog.open();

        }

}
