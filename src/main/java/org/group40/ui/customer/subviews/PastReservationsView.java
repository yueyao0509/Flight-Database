package org.group40.ui.customer.subviews;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.CustomerView;

public class PastReservationsView extends BaseView {

    private static final Color BG = new Color(200, 245, 220);

    private final DefaultTableModel model = new DefaultTableModel();
    private JTable table;

    public PastReservationsView(AppContext ctx) {
        super(ctx, BG);
        this.add(topBar("My Past Reservations", "Back",
                () -> ctx.frame.showView(new CustomerView(ctx))), "North");

        this.add(buildTable(), "Center");

        loadPastReservations();
    }

    private JScrollPane buildTable() {
        model.setColumnIdentifiers(new Object[] {
                "Reservation ID",
                "Instance ID",
                "Flight Num",
                "Airline",
                "From",
                "To",
                "Departure",
                "Arrival",
                "Seat",
                "Price"
        });

        table = new JTable(model);
        return new JScrollPane(table);
    }

    private void loadPastReservations() {
        try {
            model.setRowCount(0);

            String userID = ctx.currentUser;

            List<Map<String, Object>> list = ctx.reservationDAO.getPastReservations(userID);

            for (Map<String, Object> r : list) {
                model.addRow(new Object[] {
                        r.get("reservationID"),
                        r.get("instanceID"),
                        r.get("flightNum"),
                        r.get("airlineID"),
                        r.get("depAirport"),
                        r.get("arrAirport"),
                        r.get("departureTime"),
                        r.get("arrivalTime"),
                        r.get("seatNum"),
                        r.get("price")
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have no past reservations.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load past reservations: " + ex.getMessage());
        }
    }
}
