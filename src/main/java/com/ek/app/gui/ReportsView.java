package com.ek.app.gui;

import org.springframework.beans.factory.annotation.Autowired;

import com.ek.app.reports.ReportDefinition;
import com.ek.app.reports.ReportRow;
import com.ek.app.reports.ReportService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;

@Route(value = "reports", layout = MainLayout.class)
public class ReportsView extends SplitLayout {

    private final Grid<ReportDefinition> reportsGrid = new Grid<>(ReportDefinition.class, false);
    private final Grid<ReportRow> resultGrid = new Grid<>(ReportRow.class, false);

    public ReportsView(ReportService reportService) {
        this.reportService = reportService;

        setSizeFull();
        setSplitterPosition(20); // 20% left
        addToPrimary(createReportsPanel());
        addToSecondary(createOutputPanel());
    }

    @Autowired
    private ReportService reportService; 

    private Component createReportsPanel() {
        reportsGrid.addColumn(ReportDefinition::getName)
                .setHeader("Reports")
                .setAutoWidth(true);
        reportsGrid.setItems(reportService.getAllReports());
        reportsGrid.asSingleSelect()
                .addValueChangeListener(e -> loadReport(e.getValue()));

        reportsGrid.addThemeVariants(GridVariant.LUMO_COMPACT);

        return new VerticalLayout(new H4("Reports"), reportsGrid);
    }

    private Object loadReport(ReportDefinition value) {
        return null;
    }

    private Component createOutputPanel() {

        Button excel = new Button("Excel", VaadinIcon.FILE_TABLE.create());
        Button pdf = new Button("PDF", VaadinIcon.FILE_PRESENTATION.create());

        excel.addClickListener(e -> downloadExcel());
        pdf.addClickListener(e -> downloadExcel());

        HorizontalLayout toolbar = new HorizontalLayout(excel, pdf);

        resultGrid.setSizeFull();
        resultGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_COMPACT);

                /*
        Pagination pagination = new Pagination(0, this::pageChanged);
 */
        VerticalLayout layout = new VerticalLayout(toolbar, resultGrid);
        layout.setSizeFull();
        layout.expand(resultGrid);

        return layout;
    }

    private Object downloadExcel() {
        return null;
        // TODO Auto-generated method stub
      //  throw new UnsupportedOperationException("Unimplemented method 'downloadExcel'");
    }
}
