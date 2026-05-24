package org.group40.ui.rep.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.rep.RepView;

public class ManageFlightsView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    public ManageFlightsView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Manage Flights", "Back",
                () -> ctx.frame.showView(new RepView(ctx))),
                BorderLayout.NORTH);
        add(scrollableList(list), BorderLayout.CENTER);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem add = new JMenuItem("Add Flight");
        JMenuItem del = new JMenuItem("Delete Flight");
        menu.add(add);
        menu.add(del);
        add.addActionListener(e -> handleAdd());
        del.addActionListener(e -> handleDelete());

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = list.locationToIndex(e.getPoint());
                    if (row != -1) {
                        list.setSelectedIndex(row);
                        menu.show(list, e.getX(), e.getY());
                    }
                }
            }
        });
        load();
    }

    private void load() {
        model.clear();
        try {
            List<String> rows = ctx.catalogDAO.listFlights();
            if (rows.isEmpty())
                model.addElement("No flights.");
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error: " + ex.getMessage());
        }
    }

    private void handleAdd() {
        try {
            String id = JOptionPane.showInputDialog(this, "Flight ID:");
            if (id == null || id.isBlank())
                return;

            // Airline dropdown
            List<String> airlines = ctx.catalogDAO.listAirlineIDs();
            if (airlines.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No airlines exist. Add one first.");
                return;
            }
            String airlineSel = (String) JOptionPane.showInputDialog(this,
                    "Airline:", "Add Flight", JOptionPane.QUESTION_MESSAGE,
                    null, airlines.toArray(), airlines.get(0));
            if (airlineSel == null)
                return;
            String airlineID = airlineSel.split(" — ")[0].trim();

            // Aircraft dropdown
            List<String> aircraft = ctx.catalogDAO.listAircraftIDs();
            if (aircraft.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No aircraft exist. Add one first.");
                return;
            }
            String aircraftSel = (String) JOptionPane.showInputDialog(this,
                    "Aircraft:", "Add Flight", JOptionPane.QUESTION_MESSAGE,
                    null, aircraft.toArray(), aircraft.get(0));
            if (aircraftSel == null)
                return;
            String aircraftID = aircraftSel.split(" — ")[0].trim();

            // Departure airport
            List<String> airports = ctx.catalogDAO.listAirportIDs();
            if (airports.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No airports exist. Add one first.");
                return;
            }
            String depSel = (String) JOptionPane.showInputDialog(this,
                    "Departure airport:", "Add Flight", JOptionPane.QUESTION_MESSAGE,
                    null, airports.toArray(), airports.get(0));
            if (depSel == null)
                return;
            String dep = depSel.split(" — ")[0].trim();

            // Arrival airport
            String arrSel = (String) JOptionPane.showInputDialog(this,
                    "Arrival airport:", "Add Flight", JOptionPane.QUESTION_MESSAGE,
                    null, airports.toArray(), airports.get(0));
            if (arrSel == null)
                return;
            String arr = arrSel.split(" — ")[0].trim();

            if (dep.equals(arr)) {
                JOptionPane.showMessageDialog(this, "Departure and arrival must differ.");
                return;
            }

            String numStr = JOptionPane.showInputDialog(this, "Flight number:");
            if (numStr == null)
                return;
            String depTime = JOptionPane.showInputDialog(this, "Dep time HH:MM:SS:");
            if (depTime == null)
                return;
            String arrTime = JOptionPane.showInputDialog(this, "Arr time HH:MM:SS:");
            if (arrTime == null)
                return;

            String[] di = { "Domestic", "International" };
            int diChoice = JOptionPane.showOptionDialog(this, "Type:", "Type",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, di, di[0]);
            if (diChoice == JOptionPane.CLOSED_OPTION)
                return;
            String domOrInt = diChoice == 0 ? "D" : "I";

            String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Daily" };
            String dayOfWeek = (String) JOptionPane.showInputDialog(this,
                    "Day of week:", "Add Flight", JOptionPane.QUESTION_MESSAGE,
                    null, days, days[0]);
            if (dayOfWeek == null)
                return;

            ctx.catalogDAO.addFlight(id.trim(), airlineID, aircraftID,
                    Integer.parseInt(numStr.trim()),
                    dep, arr, depTime, arrTime, domOrInt, dayOfWeek);
            load();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Flight number must be numeric.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleDelete() {
        String selected = list.getSelectedValue();
        if (selected == null || selected.startsWith("No ") || selected.startsWith("Error"))
            return;
        String id = selected.split("\\|")[0].trim();
        if (JOptionPane.showConfirmDialog(this, "Delete flight " + id + "?") != JOptionPane.YES_OPTION)
            return;
        try {
            ctx.catalogDAO.deleteFlight(id);
            load();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete — flight may have instances/reservations.");
        }
    }
}