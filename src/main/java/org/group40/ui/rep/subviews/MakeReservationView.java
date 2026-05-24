package org.group40.ui.rep.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.rep.RepView;

public class MakeReservationView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);

    private final JComboBox<String> cbCustomer = new JComboBox<>();
    private final JComboBox<String> cbFrom = new JComboBox<>();
    private final JComboBox<String> cbTo = new JComboBox<>();
    private final JTextField tfDate = new JTextField("2026-05-01");
    private final JTextField tfMaxPrice = new JTextField();
    private final JComboBox<String> cbSort = new JComboBox<>(
            new String[] { "takeoff", "price", "landing", "duration" });
    private final JComboBox<String> cbClass = new JComboBox<>(new String[] { "economy", "business", "first" });

    private final DefaultTableModel model = new DefaultTableModel();
    private JTable table;

    public MakeReservationView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Make Reservation", "Back",
                () -> ctx.frame.showView(new RepView(ctx))), BorderLayout.NORTH);

        loadDropdowns();

        JPanel form = formPanel(560);
        addRow(form, "Customer", cbCustomer);
        addRow(form, "From Airport", cbFrom);
        addRow(form, "To Airport", cbTo);
        addRow(form, "Date (YYYY-MM-DD)", tfDate);
        addRow(form, "Max Price", tfMaxPrice);
        addRow(form, "Sort By", cbSort);
        addRow(form, "Class", cbClass);

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.add(smallButton("Search Flights", this::handleSearch));
        btnRow.add(smallButton("Book Selected", this::handleBook));

        model.setColumnIdentifiers(new Object[] {
                "Instance ID", "Flight", "Airline", "From", "To", "Departure", "Arrival", "Seats", "Fare" });
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(centered(form), BorderLayout.NORTH);
        center.add(btnRow, BorderLayout.CENTER);
        center.add(new JScrollPane(table), BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
    }

    private void loadDropdowns() {
        try {
            for (String c : ctx.userDAO.listCustomers())
                cbCustomer.addItem(c);
            cbFrom.addItem(""); // allow blank = any
            cbTo.addItem("");
            for (String a : ctx.catalogDAO.listAirportIDs()) {
                cbFrom.addItem(a);
                cbTo.addItem(a);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleSearch() {
        try {
            model.setRowCount(0);
            String from = extractCode(cbFrom.getSelectedItem());
            String to = extractCode(cbTo.getSelectedItem());
            String date = tfDate.getText().trim();

            Double maxPrice = tfMaxPrice.getText().isBlank() ? null
                    : Double.parseDouble(tfMaxPrice.getText().trim());

            List<Map<String, Object>> results = ctx.flightSearchDAO.searchFlights(
                    from, to, date, false, String.valueOf(cbSort.getSelectedItem()),
                    maxPrice, null);

            for (Map<String, Object> r : results) {
                model.addRow(new Object[] {
                        r.get("instanceID"), r.get("flightNum"), r.get("airlineID"),
                        r.get("depAirport"), r.get("arrAirport"),
                        r.get("departureTime"), r.get("arrivalTime"),
                        r.get("seatsAvail"), "$" + r.get("baseFare") });
            }
            if (results.isEmpty())
                JOptionPane.showMessageDialog(this, "No flights found.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    private void handleBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }
        if (cbCustomer.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select a customer.");
            return;
        }
        try {
            String customer = cbCustomer.getSelectedItem().toString().split(" — ")[0].trim();
            String userID = ctx.userDAO.getUserIDByUsername(customer);
            if (userID == null) {
                JOptionPane.showMessageDialog(this, "Customer not found.");
                return;
            }
            String instanceID = model.getValueAt(row, 0).toString();
            String seatClass = String.valueOf(cbClass.getSelectedItem());

            String result = ctx.reservationDAO.createReservation(userID, instanceID, seatClass);
            if ("FULL".equals(result)) {
                JOptionPane.showMessageDialog(this, "Flight is full.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Booked! Reservation ID: " + result);
            handleSearch();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage());
        }
    }

    private String extractCode(Object selected) {
        if (selected == null)
            return "";
        String s = selected.toString();
        if (s.isBlank())
            return "";
        return s.split(" — ")[0].trim();
    }
}