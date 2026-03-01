package com.ek.app.gui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;

public class KpiCard extends Div {

public KpiCard(String title, String value, String delta) {
        addClassName("card");
        addClassName("kpi-card");

        H5 h = new H5(title);
        h.addClassName("card-title");

        Div val = new Div();
        val.setText(value);
        val.addClassName("kpi-value");

        Paragraph d = new Paragraph(delta);
        d.addClassName(delta.startsWith("-") ? "kpi-delta down" : "kpi-delta up");

        add(h, val, d);
    }


}
