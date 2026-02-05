package com.ek.app.gui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {

        RouterLink home = new RouterLink("Home", HomeView.class);
        RouterLink products = new RouterLink("Products", ProductsView.class);
        RouterLink inventory = new RouterLink("Inventory", InventoryView.class);
        RouterLink reports = new RouterLink("Reports", ReportsView.class);
        RouterLink billBook = new RouterLink("Bill Book", BillBookView.class);
        HorizontalLayout nav = new HorizontalLayout(home, products, inventory,
                billBook, reports);
        nav.getStyle().set("gap", "20px");
        HorizontalLayout header = new HorizontalLayout( nav);
        header.setWidthFull();
        header.getStyle()
                .set("background", "#f5f5d4ff")
                .set("padding", "10px");
        header.addClassName("app-header");
        home.addClassName("nav-link");
        products.addClassName("nav-link");
        inventory.addClassName("nav-link");

        // setPrimarySection(Section.DRAWER);
        // addToNavbar(true, createTopbar());
        // addToDrawer(createDrawer());

    }

    private Component createDrawer() {

        H3 brand = new H3("StockFlow");
        brand.addClassName("brand");

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", MainLayout.class,
                VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Inventory", InventoryView.class, VaadinIcon.ARCHIVES.create()));
        nav.addItem(new SideNavItem("Suppliers", "#", VaadinIcon.TRUCK.create()));
        nav.addItem(new SideNavItem("Orders", "#", VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem("Reports", ReportsView.class, VaadinIcon.CHART.create()));
        nav.addItem(new SideNavItem("Settings", "#", VaadinIcon.COG.create()));

        VerticalLayout wrapper = new VerticalLayout(brand, nav);
        wrapper.setPadding(true);
        wrapper.setSpacing(false);
        wrapper.setSizeFull();
        wrapper.addClassName("drawer");

        return new Scroller(wrapper);

    }

    private Component createTopbar() {

        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu");

        TextField search = new TextField();
        search.setPlaceholder("Search products, SKUs…");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setWidth("560px");
        search.addClassName("top-search");

        HorizontalLayout actions = new HorizontalLayout(
                new Icon(VaadinIcon.BELL),
                new Icon(VaadinIcon.QUESTION_CIRCLE),
                new Icon(VaadinIcon.USER));
        // actions.setSpacing("16px");
        actions.addClassName("top-actions");

        HorizontalLayout bar = new HorizontalLayout(toggle, search, actions);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.addClassNames("topbar", LumoUtility.Padding.MEDIUM);

        /**
         * RouterLink home = new RouterLink("Home", HomeView.class);
         * RouterLink products = new RouterLink("Products", ProductsView.class);
         * RouterLink inventory = new RouterLink("Inventory", InventoryView.class);
         * RouterLink reports = new RouterLink("Reports", ReportsView.class);
         * RouterLink billBook = new RouterLink("Bill Book", BillBookView.class);
         * 
         * HorizontalLayout nav = new HorizontalLayout(home, products, inventory,
         * billBook, reports);
         * nav.getStyle().set("gap", "20px");
         * 
         * HorizontalLayout header = new HorizontalLayout(title, nav);
         * header.setWidthFull();
         * header.getStyle()
         * .set("background", "#f5f5d4ff")
         * .set("padding", "10px");
         * 
         * header.addClassName("app-header");
         * home.addClassName("nav-link");
         * products.addClassName("nav-link");
         * inventory.addClassName("nav-link");
         */

        return bar;
    }
}
