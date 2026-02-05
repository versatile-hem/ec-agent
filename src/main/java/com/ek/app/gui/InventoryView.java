package com.ek.app.gui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.ek.app.inventory.domain.InventoryMovementDto;
import com.ek.app.inventory.domain.InventoryService;
import com.ek.app.inventory.domain.InventoryType;
import com.ek.app.inventory.infra.db.SalesChannel;
import com.ek.app.productcatalog.ProductDto;
import com.ek.app.productcatalog.ProductService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.Route;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route(value = "inventory", layout = MainLayout.class)
public class InventoryView extends VerticalLayout {

    private final int PAGE_SIZE = 10;
    private int currentPage = 0;

    @Autowired
    private final InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    private List<ProductDto> prodInv = new ArrayList<>();
    private Grid<ProductDto> grid = new Grid<>(ProductDto.class, false);

    public InventoryView(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        setSizeFull();
        // setPadding(true);
        Button prev = new Button("Previous", e -> changePage(-1));
        Button next = new Button("Next", e -> changePage(1));
        Div pager = new Div(prev, next);
        pager.getStyle().set("display", "flex").set("gap", "10px");
        add(buildHeader(), buildGrid(), pager);
        refresh();
    }

    // ---------------- HEADER ----------------
    private Component buildHeader() {
        TextField search = new TextField();
        search.setPlaceholder("Search SKU / Product");
        Button refresh = new Button("Search", e -> refresh());
        HorizontalLayout header = new HorizontalLayout(search, refresh);
        header.setWidthFull();
        header.setAlignItems(Alignment.END);
        return header;
    }

    // ---------------- GRID ----------------
    private Grid<ProductDto> buildGrid() {
        prodInv = this.productService.listAll();
        NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        inr.setMinimumFractionDigits(2);
        grid.addColumn(ProductDto::getSku).setHeader("SKU").setWidth("120px")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(ProductDto::getProduct_title).setHeader("Product");
        grid.addColumn(ProductDto::getCategory).setHeader("Category").setWidth("200px")
                .setAutoWidth(false)
                .setFlexGrow(0);
        grid.addColumn(ProductDto::getAvailableStock).setHeader("Stock")
                .setWidth("120px")
                .setAutoWidth(false)
                .setFlexGrow(0);
        grid.addColumn(dto -> inr.format(dto.getMrp()))
                .setHeader("MRP")
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("120px")
                .setAutoWidth(false)
                .setFlexGrow(0);

        grid.addComponentColumn(this::actionButtons)
                .setHeader("Actions");
        grid.setSizeFull();
        return grid;
    }

    // ---------------- ACTION BUTTONS ----------------

    private Component actionButtons(ProductDto dto) {
        Button in = new Button("+IN", e -> openInDialog(dto));
        Button out = new Button("-OUT", e -> openOutDialog(dto));
        Button adjust = new Button("±", e -> openAdjustDialog(dto));
        Button damage = new Button("Damage", e -> openDamageDialog(dto));
        Button ret = new Button("Return", e -> openReturnDialog(dto));
        Button vm = new Button("View Movements", e -> openReturnDialog(dto));
        in.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        damage.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(in, out, adjust, damage, ret,vm);
        actions.setSpacing(false);
        actions.setPadding(false);
        return actions;

    }

    // ---------------- DIALOGS ----------------

    private void openInDialog(ProductDto dto) {
        openQtyDialog("Stock IN", dto, InventoryType.IN);
    }

    private void openOutDialog(ProductDto dto) {
        openQtyDialog("Stock OUT", dto, InventoryType.OUT);
    }

    private void openAdjustDialog(ProductDto dto) {
        openQtyDialog("Adjust", dto, InventoryType.ADJUST);
    }

    private void openDamageDialog(ProductDto dto) {
        openQtyDialog("Damage", dto, InventoryType.DAMAGE);
    }

    private void openReturnDialog(ProductDto dto) {
        openQtyDialog("Return", dto, InventoryType.RETURN);
    }

    // ---------------- COMMON QTY DIALOG ----------------
    private void openQtyDialog(String title, ProductDto dto, InventoryType type) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title + " - " + dto.getProduct_title());
        IntegerField qty = new IntegerField("Quantity");
        qty.setMin(1);
        TextField reference = new TextField("Refrence"); // orderid/po
        TextArea remarks = new TextArea("Remarks");
        DateTimePicker movementTime = new DateTimePicker("Movement Time");

        ComboBox<SalesChannel> salesChannel = new ComboBox<>("Sales Channel");
        salesChannel.setItems(SalesChannel.values());
        salesChannel.setItemLabelGenerator(channel -> channel.name().charAt(0) +
                channel.name().substring(1).toLowerCase());
        salesChannel.setPlaceholder("Select channel");
        salesChannel.setClearButtonVisible(true);

        movementTime.setValue(LocalDateTime.now());
        movementTime.setStep(Duration.ofMinutes(1));
        Button save = new Button("Save", e -> {
            InventoryMovementDto stockMovementDto = new InventoryMovementDto();
            stockMovementDto.setMovementTime(movementTime.getValue());
            stockMovementDto.setReference(reference.getValue());
            stockMovementDto.setQuantity(BigDecimal.valueOf(qty.getValue()));
            stockMovementDto.setMovementType(type);
            stockMovementDto.setSalesChannel(salesChannel.getValue());
            inventoryService.updateStock(stockMovementDto, dto.getProductId(), qty.getValue(), type);
            dialog.close();
            refresh();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(new VerticalLayout(salesChannel, qty, reference, movementTime, remarks, save));
        dialog.open();
    }

    // ---------------- REFRESH ----------------
    private void refresh() {
        grid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, prodInv.size());
        grid.setItems(prodInv.subList(from, to));
    }

    private void changePage(int delta) {
        int maxPage = prodInv.size() / PAGE_SIZE;
        currentPage = Math.max(0, Math.min(currentPage + delta, maxPage));
        refresh();
    }

}
