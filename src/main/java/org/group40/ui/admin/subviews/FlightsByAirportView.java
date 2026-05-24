package org.group40.ui.admin.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.admin.AdminView;

public class FlightsByAirportView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);
    private final JComboBox<String> airportPicker = new JComboBox<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public FlightsByAirportView(AppContext ctx) {
        super(ctx, BG);

        airportPicker.setEditable(true);
        add(topBar("Flights by Airport", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);

        loadAirports();

        JList<String> list = new JList<>(model);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(dropdownBar("Airport:", airportPicker, "Search", this::handleSearch),
                BorderLayout.NORTH);
        center.add(scrollableList(list), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void loadAirports() {
        try {
            List<String> airports = ctx.catalogDAO.listAirportIDs();
            for (String a : airports)
                airportPicker.addItem(a);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleSearch() {
        model.clear();
        Object selected = airportPicker.getSelectedItem();
        if (selected == null) {
            model.addElement("No airport selected.");
            return;
        }
        String airport = selected.toString().split(" — ")[0].trim();
        try {
            List<String> rows = ctx.flightSearchDAO.getFlightsByAirport(airport);
            if (rows.isEmpty())
                model.addElement("No flights found for " + airport);
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error loading flights.");
            ex.printStackTrace();
        }
    }
}
