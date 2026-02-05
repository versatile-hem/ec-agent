package com.ek.app.gui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    public HomeView() {
        getStyle().set("background", "#fffff0");
        add(new H1("Welcome to Home"));
        
    }
}
