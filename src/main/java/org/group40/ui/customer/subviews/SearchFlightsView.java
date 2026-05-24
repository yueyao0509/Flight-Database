package org.group40.ui.customer.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.CustomerView;

public class SearchFlightsView extends BaseView {
    private final JTextField fromField = new JTextField(8);
    private final JTextField toField = new JTextField(8);
    private final JTextField dateField = new JTextField(10);
    private final JCheckBox flexibleBox = new JCheckBox("+/- 3 days");

    private final JComboBox<String> sortBox = new JComboBox<>(
            new String[] { "takeoff", "price", "landing", "duration" });
    private final JComboBox<String> classBox = new JComboBox<>(new String[] { "economy", "business", "first" });

    private final JTextField maxPriceField = new JTextField(6);
    private final JTextField maxStopsField = new JTextField(3);
    private final JTextField airlineField = new JTextField(4);
    private final JTextField earliestTakeoffField = new JTextField(6);
    private final JTextField latestLandingField = new JTextField(6);

    private final JCheckBox roundTripBox = new JCheckBox("Round trip");
    private final JTextField returnDateField = new JTextField(10);

    private final DefaultTableModel model = new DefaultTableModel();
    private JTable table;

    public SearchFlightsView(AppContext ctx) {
        super(ctx, new Color(200, 245, 220));
        add(topBar("Search Flights", "Back", () -> ctx.frame.showView(new CustomerView(ctx))), BorderLayout.NORTH);
        add(buildSearchPanel(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);

        // make the table not editable
        table.setDefaultEditor(Object.class, null);

        // load initial flights
        searchFlights();
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setOpaque(false);

        JLabel hint = new JLabel("All filters are optional. * = required for round trip");
        hint.setFont(SMALL_FONT);
        panel.add(hint);
        panel.add(new JLabel(""));

        panel.add(new JLabel("From Airport:*"));
        panel.add(fromField);
        panel.add(new JLabel("To Airport:*"));
        panel.add(toField);
        panel.add(new JLabel("Date yyyy-mm-dd:*"));
        panel.add(dateField);
        panel.add(new JLabel("Round Trip:"));
        panel.add(roundTripBox);
        panel.add(new JLabel("Return Date yyyy-mm-dd:*"));
        panel.add(returnDateField);
        panel.add(new JLabel("Flexible Date:"));
        panel.add(flexibleBox);
        panel.add(new JLabel("Sort By:"));
        panel.add(sortBox);
        panel.add(new JLabel("Max Price:"));
        panel.add(maxPriceField);
        panel.add(new JLabel("Max Stops:"));
        panel.add(maxStopsField);
        panel.add(new JLabel("Airline ID:"));
        panel.add(airlineField);
        panel.add(new JLabel("Earliest Takeoff HH:mm:"));
        panel.add(earliestTakeoffField);
        panel.add(new JLabel("Latest Landing HH:mm:"));
        panel.add(latestLandingField);
        panel.add(new JLabel("Ticket Class:"));
        panel.add(classBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchFlights());
        panel.add(new JLabel(""));
        panel.add(searchBtn);

        return panel;
    }

    private JScrollPane buildTable() {
        model.setColumnIdentifiers(new Object[] {
                "Instance ID", "Flight ID", "Flight Num", "Airline", "From", "To",
                "Departure", "Arrival", "Seats", "Price", "Duration", "Stops"
        });

        table = new JTable(model);
        table.setRowHeight(26);

        JButton reserveBtn = new JButton("Reserve / Buy Selected Flight");
        reserveBtn.addActionListener(e -> reserveSelectedFlight());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        wrapper.add(reserveBtn, BorderLayout.SOUTH);
        return new JScrollPane(wrapper);
    }

    private void searchFlights() {
        try {
            model.setRowCount(0);
            String from = fromField.getText().trim().toUpperCase();
            String to = toField.getText().trim().toUpperCase();
            String date = dateField.getText().trim();
            boolean flexible = flexibleBox.isSelected();
            String sortBy = String.valueOf(sortBox.getSelectedItem());

            Double maxPrice = parseDoubleOrNull(maxPriceField.getText().trim());
            Integer maxStops = parseIntOrNull(maxStopsField.getText().trim());
            String airline = airlineField.getText().trim().toUpperCase();
            String earliestTakeoff = normalizeTime(earliestTakeoffField.getText().trim());
            String latestLanding = normalizeTime(latestLandingField.getText().trim());

            if (roundTripBox.isSelected()) {
                String returnDate = returnDateField.getText().trim();
                if (from.isBlank() || to.isBlank() || date.isBlank() || returnDate.isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Round trip requires From, To, Date, and Return Date.");
                    return;
                }
                var rt = ctx.flightSearchDAO.searchRoundTrip(
                        from, to, date, returnDate, flexible, sortBy, maxPrice, airline);
                addFlightsToModel(rt.outbound(), "OUTBOUND");
                addFlightsToModel(rt.returning(), "RETURN");
                if (rt.outbound().isEmpty() && rt.returning().isEmpty())
                    JOptionPane.showMessageDialog(this, "No flights found.");
            } else {
                List<Map<String, Object>> flights = ctx.flightSearchDAO.searchFlights(
                        from, to, date, flexible, sortBy, maxPrice, maxStops,
                        airline, earliestTakeoff, latestLanding);
                addFlightsToModel(flights, "");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    private void addFlightsToModel(List<Map<String, Object>> flights, String legLabel) {
        for (Map<String, Object> f : flights) {
            String flightDisplay = legLabel.isEmpty()
                    ? String.valueOf(f.get("flightID"))
                    : legLabel + " " + f.get("flightID");
            model.addRow(new Object[] {
                    f.get("instanceID"), flightDisplay, f.get("flightNum"),
                    f.get("airlineID"), f.get("depAirport"), f.get("arrAirport"),
                    f.get("departureTime"), f.get("arrivalTime"), f.get("seatsAvail"),
                    f.get("baseFare"), f.get("durationMinutes"), f.get("stops")
            });
        }
    }

    private void reserveSelectedFlight() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight first.");
            return;
        }

        String instanceID = model.getValueAt(row, 0).toString();
        String seatClass = String.valueOf(classBox.getSelectedItem());

        try {
            String result = ctx.reservationDAO.createReservation(ctx.currentUser, instanceID, seatClass);
            if ("FULL".equals(result)) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Flight is full. Join waitlist?",
                        "Waitlist",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    ctx.waitlistDAO.addToWaitlist(ctx.currentUser, instanceID);
                    JOptionPane.showMessageDialog(this, "Added to waitlist.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reservation created and ticket bought! Reservation ID: " + result);
                searchFlights();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Reservation failed: " + ex.getMessage());
        }
    }

    private Double parseDoubleOrNull(String text) {
        if (text == null || text.isBlank())
            return null;
        return Double.parseDouble(text);
    }

    private Integer parseIntOrNull(String text) {
        if (text == null || text.isBlank())
            return null;
        return Integer.parseInt(text);
    }

    private String normalizeTime(String text) {
        if (text == null || text.isBlank())
            return null;
        if (text.length() == 5)
            return text + ":00";
        return text;
    }
}
