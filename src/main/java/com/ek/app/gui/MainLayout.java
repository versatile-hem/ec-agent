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
    // Optional styling
    tabs.getStyle()
            .set("border-bottom", "1px solid #ddd");
    // Default selected tab
    tabs.setSelectedTab(inventoryTab);
    loadInventoryMenu();

     
    Map<Tab, Runnable> tabActions = new HashMap<>();
    tabActions.put(inventoryTab, this::loadInventoryMenu);
    tabActions.put(purchaseTab, this::loadPurchaseMenu);
    tabActions.put(salesTab, this::loadSalesMenu);

    tabs.addSelectedChangeListener(event ->
            tabActions.get(event.getSelectedTab()).run()
    );


      // 👤 Profile Menu
    MenuBar profileMenu = new MenuBar();
   profileMenu.getStyle().set("margin-left", "auto");
    Avatar avatar = new Avatar("Admin");
    MenuItem userItem = profileMenu.addItem(avatar);
    SubMenu subMenu = userItem.getSubMenu();
    subMenu.addItem("Logout", e -> {
        authContext.logout(); 
    });   


  // Header Layout
    HorizontalLayout header = new HorizontalLayout(tabs, profileMenu);
    header.setWidthFull();
    header.expand(tabs); // push profile to right
    addToNavbar(header);

        /**



          RouterLink home = new RouterLink("Home", HomeView.class);
        RouterLink products = new RouterLink("Products", ProductsView.class);
        RouterLink inventory = new RouterLink("Inventory Management", InventoryView.class);
        RouterLink purchaseMgt = new RouterLink("🛒 Purchase Management", ReportsView.class);
        RouterLink billBook = new RouterLink("🧾Sales & Bill Book", BillBookView.class);
        HorizontalLayout nav = new HorizontalLayout(home, products, inventory,
                billBook, purchaseMgt);

        home.addClassName("nav-link");
        products.addClassName("nav-link");
        inventory.addClassName("nav-link");
        billBook.addClassName("nav-link");
        purchaseMgt.addClassName("nav-link");

        nav.getStyle().set("gap", "20px");

        // RIGHT USER AREA
        Avatar avatar = new Avatar("Admin");

        MenuBar userMenu = new MenuBar();
        userMenu.setOpenOnHover(false);
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuItem = userMenu.addItem(avatar);

        menuItem.getSubMenu().addItem("Profile", e -> {
            UI.getCurrent().navigate("profile");
        });

        menuItem.getSubMenu().addItem("Logout", e -> {
            authContext.logout(); // Vaadin AuthenticationContext
        });

        // RIGHT USER AREA
        HorizontalLayout rightNav = new HorizontalLayout(userMenu);

        // HorizontalLayout rightNav = new HorizontalLayout(avatar);

        HorizontalLayout header = new HorizontalLayout(nav, rightNav);
        header.setWidthFull();
        header.getStyle()
                .set("background", "#f5f5d4ff")
                .set("padding", "10px");
        header.addClassName("app-header");
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(nav);
        addToNavbar(header);

         */
    }

   private void createDrawer() {
        sideMenu.setWidthFull();
        addToDrawer(sideMenu);
        loadInventoryMenu(); // default
    }

     // 🔹 INVENTORY MENU
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

    // 🔹 PURCHASE MENU
    private void loadPurchaseMenu() {
        sideMenu.removeAll();

        sideMenu.add(
                new RouterLink("Purchase Orders", HomeView.class),
                new RouterLink("GRN", HomeView.class),
                new RouterLink("Vendors", HomeView.class)
        );
    }


    // 🔹 SALES MENU
    private void loadSalesMenu() {
        sideMenu.removeAll();

 
        sideMenu.add(
                new RouterLink("Create Bill", BillBookView.class)
              //  new RouterLink("Sales Return", SalesReturnView.class),
               // new RouterLink("Customers", CustomerView.class)
        );
    }
}
