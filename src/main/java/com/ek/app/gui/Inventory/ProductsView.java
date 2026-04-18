package com.ek.app.gui.Inventory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.ek.app.gui.MainLayout;
import com.ek.app.productcatalog.domain.ProductDto;
import com.ek.app.productcatalog.domain.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RolesAllowed("ADMIN")
@Route(value = "products", layout = MainLayout.class)
public class ProductsView extends VerticalLayout {

    private final int PAGE_SIZE = 10;
    private int currentPage = 0;

    @Autowired
    private ProductService productService;

    private List<ProductDto> skuList = new ArrayList<>();
    private Grid<ProductDto> grid = new Grid<>(ProductDto.class,false);

    public ProductsView(ProductService productService) {
        this.productService = productService;
        skuList = this.productService.listAll();
        log.info("items extracted , {}", skuList.size());
        add(new H1("Products Page (Demo)"));
        Button addProduct = new Button("+ Product", e -> addProduct());
        add(new Div(addProduct));
        getStyle().set("background", "#fffff0");
        Button prev = new Button("Previous", e -> changePage(-1));
        Button next = new Button("Next", e -> changePage(1));
        Div pager = new Div(prev, next);
        pager.getStyle().set("display", "flex").set("gap", "10px");
        add(grid, pager);
        loadPage();
    }

    private void loadPage() {
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, skuList.size());
        NumberFormat inrFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        grid.addColumn(ProductDto::getSku).setHeader("SKU");
        grid.addColumn(ProductDto::getProduct_title).setHeader("Product Title");
        grid.addColumn(ProductDto::getBarcode).setHeader("Barcode");
        grid.addColumn(ProductDto::getCategory).setHeader("Category");
        grid.addColumn(ProductDto::getHsn).setHeader("HSN");
        grid.addColumn(ProductDto::getTax_code).setHeader("Tax Code");
        grid.addColumn(ProductDto::getWeightGrams).setHeader("Weight (g)");
        //grid.addColumn(ProductDto::getMrp).setHeader("MRP");
        grid.addColumn(dto -> inrFormat.format(dto.getMrp())).setHeader("MRP");
        grid.setItems(skuList.subList(from, to));
    }

    private void changePage(int delta) {
        int maxPage = skuList.size() / PAGE_SIZE;
        currentPage = Math.max(0, Math.min(currentPage + delta, maxPage));
        loadPage();
    }

    private void addProduct() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add Product");
        dialog.addClassName("product-dialog");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("640px");
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);

        // ==== Fields ====
        TextField productTitle = new TextField("Product Title");
        productTitle.setRequiredIndicatorVisible(true);
        productTitle.setClearButtonVisible(true);
        productTitle.addClassName("field-product-title");

        TextField sku = new TextField("SKU");
        sku.setRequiredIndicatorVisible(true);
        sku.setClearButtonVisible(true);
        sku.addClassName("field-sku");

        // Category as ComboBox; change to your data source
        ComboBox<String> category = new ComboBox<>("Category");
        category.setItems("Beverages", "Snacks", "Personal Care", "Household", "Other");
        category.setAllowCustomValue(true); // allow manual entry if not in list
        category.addCustomValueSetListener(e -> category.setValue(e.getDetail()));
        category.setClearButtonVisible(true);
        category.addClassName("field-category");

        NumberField mrp = new NumberField("MRP");
        mrp.setMin(0);
        mrp.setStep(0.01);
        mrp.setClearButtonVisible(true);
        mrp.setPrefixComponent(new Span("₹")); // change to $ or your currency
        mrp.addClassName("field-mrp");

        TextField hsn = new TextField("HSN");
        hsn.setPattern("\\d{4,8}"); // common HSN length range; adjust as needed
        // hsn.setPreventInvalidInput(true);
        hsn.setClearButtonVisible(true);
        hsn.addClassName("field-hsn");

        TextField taxCode = new TextField("Tax Code");
        taxCode.setClearButtonVisible(true);
        taxCode.addClassName("field-tax-code");

        // ==== Layout ====
        FormLayout form = new FormLayout();
        form.addClassName("product-form");
        form.add(productTitle, sku, category, mrp, hsn, taxCode);

        // 2 columns on desktop; full width on mobile handled via CSS
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2));

        // ==== Actions ====
        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClassName("btn-cancel");

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClassName("btn-save");

        // (Optional) Binder for validation & mapping
        Binder<ProductDto> binder = new Binder<>(ProductDto.class);

        // Bindings (adapt getters/setters to your Product model)
        binder.forField(productTitle)
                .asRequired("Product title is required")
                .bind(ProductDto::getProduct_title, ProductDto::setProduct_title); // assuming name == product_title

        binder.forField(sku)
                .asRequired("SKU is required")
                .bind(ProductDto::getSku, ProductDto::setSku);

                /*
        binder.forField(category)
                .withValidator(val -> val != null && !val.isBlank(), "Category is required")
                .bind(ProductDto::getCategory, ProductDto::setCategory); // ensure these exist

        binder.forField(mrp)
                .withValidator(v -> v != null && v >= 0, "MRP must be ≥ 0")
                .bind(ProductDto::getMrp, ProductDto::setMrp); // ensure Double/BigDecimal mapping

        binder.forField(hsn)
                .withValidator(v -> v == null || v.matches("\\d{4,8}"), "HSN must be 4–8 digits")
                .bind(ProductDto::getHsn, ProductDto::setHsn);

        binder.forField(taxCode)
                .bind(ProductDto::getTaxCode, ProductDto::setTaxCode);
                 */

        save.addClickListener(e -> {
            ProductDto product = new ProductDto();
 
            if (binder.writeBeanIfValid(product)) {
                 product.setWeightGrams(null);

                // Persist and refresh grid
                grid.setItems(this.productService.addProduct(product));
                dialog.close();
            } else {
                // Optionally: show a notification or shake animation
                Notification.show("Please fix the errors before saving.", 3000, Notification.Position.MIDDLE);
            }
        });

        HorizontalLayout footer = new HorizontalLayout(cancel, save);
        footer.addClassName("dialog-footer");
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
     
        dialog.add(form);
        dialog.getFooter().add(footer);
        dialog.addOpenedChangeListener(ev -> {
            if (ev.isOpened())
                productTitle.focus();
        });
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        dialog.open();

        
    }
}