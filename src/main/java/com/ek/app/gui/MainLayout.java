package com.ek.app.gui;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ek.app.gui.Inventory.InventoryView;
import com.ek.app.gui.Inventory.ProductsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Uses(Tab.class)
public class MainLayout extends AppLayout {

    private VerticalLayout sideMenu = new VerticalLayout();
    private Tabs tabs;

    @Autowired
    AuthenticationContext authContext;

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        Tab inventoryTab = new Tab("Inventory");
        Tab purchaseTab = new Tab("Purchase Management");
        Tab salesTab = new Tab("Sales & Billing");

        tabs = new Tabs(inventoryTab, purchaseTab, salesTab);
        tabs.setWidthFull();
        tabs.getStyle().set("border-bottom", "1px solid #ddd");
        tabs.setSelectedTab(inventoryTab);
        loadInventoryMenu();

        Map<Tab, Runnable> tabActions = new HashMap<>();
        tabActions.put(inventoryTab, this::loadInventoryMenu);
        tabActions.put(purchaseTab, this::loadPurchaseMenu);
        tabActions.put(salesTab, this::loadSalesMenu);

        tabs.addSelectedChangeListener(event ->
                tabActions.get(event.getSelectedTab()).run()
        );

        MenuBar profileMenu = new MenuBar();
        profileMenu.getStyle().set("margin-left", "auto");
        Avatar avatar = new Avatar("Admin");
        MenuItem userItem = profileMenu.addItem(avatar);
        SubMenu subMenu = userItem.getSubMenu();
        subMenu.addItem("Logout", e -> authContext.logout());

        HorizontalLayout header = new HorizontalLayout(tabs, profileMenu);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(tabs);
        header.getStyle()
                .set("background", "#f5f5d4ff")
                .set("padding", "10px");
        header.addClassName("app-header");
        addToNavbar(header);
    }

    private void createDrawer() {
        sideMenu.setWidthFull();
        addToDrawer(sideMenu);
        loadInventoryMenu();
    }

    private void loadInventoryMenu() {
        sideMenu.removeAll();
        RouterLink dashboard = new RouterLink("Dashboard", HomeView.class);
        RouterLink stock = new RouterLink("Stock Movement", InventoryView.class);
        RouterLink products = new RouterLink("Products", ProductsView.class);
        dashboard.addClassName("sidebar-link");
        stock.addClassName("sidebar-link");
        products.addClassName("sidebar-link");
        sideMenu.add(dashboard, stock, products);
        sideMenu.addClassName("sidebar");
    }

    private void loadPurchaseMenu() {
        sideMenu.removeAll();
        sideMenu.add(
                new RouterLink("Purchase Orders", HomeView.class),
                new RouterLink("GRN", HomeView.class),
                new RouterLink("Vendors", HomeView.class)
        );
    }

    private void loadSalesMenu() {
        sideMenu.removeAll();
        sideMenu.add(
                new RouterLink("Create Bill", BillBookView.class)
        );
    }
}
