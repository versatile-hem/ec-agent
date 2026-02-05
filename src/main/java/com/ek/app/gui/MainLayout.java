package com.ek.app.gui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {

        RouterLink home = new RouterLink("Home", HomeView.class);
        RouterLink products = new RouterLink("Products", ProductsView.class);
        RouterLink inventory = new RouterLink("Inventory", InventoryView.class);
        RouterLink reports = new RouterLink("Reports", ReportsView.class);
        RouterLink billBook = new RouterLink("Bill Book", BillBookView.class);
        HorizontalLayout nav = new HorizontalLayout(home, products, inventory,
                billBook, reports);


        home.addClassName("nav-link");
        products.addClassName("nav-link");
        inventory.addClassName("nav-link");
        nav.getStyle().set("gap", "20px");



        // RIGHT USER AREA
        Avatar avatar = new Avatar("Admin");
       // Button logout = new Button("Logout");
        HorizontalLayout rightNav = new HorizontalLayout(avatar);



        HorizontalLayout header = new HorizontalLayout(nav, rightNav);
        header.setWidthFull();
        header.getStyle()
                .set("background", "#f5f5d4ff")
                .set("padding", "10px");
        header.addClassName("app-header");
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(nav); 
        addToNavbar(header);
        

    }

}
