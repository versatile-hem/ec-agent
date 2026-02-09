package com.ek.app.gui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.Theme;

//@Theme(value = "nextra")
public class MainLayout extends AppLayout {

    @Autowired
    AuthenticationContext authContext;

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

        //HorizontalLayout rightNav = new HorizontalLayout(avatar);

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
