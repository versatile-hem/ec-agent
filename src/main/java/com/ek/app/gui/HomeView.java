package com.ek.app.gui;



import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RolesAllowed("ADMIN")
@Route(value = "home", layout = MainLayout.class)
public class HomeView  extends VerticalLayout{

    public HomeView() {
                Board dashboard = new Board(); 

          Div visitors = new Div();
            visitors.add(new H3("Visitors"));
            
            

            add(dashboard);
    }
 

    
}

