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

public class ReservationsByFlightView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);
    private final JComboBox<String> flightPicker = new JComboBox<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public ReservationsByFlightView(AppContext ctx) {
        super(ctx, BG);
        flightPicker.setEditable(true);

        add(topBar("Reservations by Flight", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);

        loadFlights();

        JList<String> list = new JList<>(model);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(dropdownBar("Flight:", flightPicker, "Search", this::handleSearch),
                BorderLayout.NORTH);
        center.add(scrollableList(list), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void loadFlights() {
        try {
            List<String> flights = ctx.catalogDAO.listFlightIDs();
            for (String f : flights)
                flightPicker.addItem(f);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleSearch() {
        model.clear();
        Object selected = flightPicker.getSelectedItem();
        if (selected == null) {
            model.addElement("No flight selected.");
            return;
        }
        String flightID = selected.toString().split(" ")[0].trim();
        try {
            List<String> rows = ctx.reservationDAO.getReservationsByFlight(flightID);
            if (rows.isEmpty())
                model.addElement("No reservations found for " + flightID);
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error loading reservations.");
            ex.printStackTrace();
        }
    }
}