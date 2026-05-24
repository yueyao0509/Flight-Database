package org.group40.ui.customer.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.CustomerView;

public class CancelTicketView extends BaseView {
    private static final Color BG = new Color(200, 245, 220);
    private final DefaultTableModel model = new DefaultTableModel();
    private JTable table;

    public CancelTicketView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Cancel Ticket", "Back", () -> ctx.frame.showView(new CustomerView(ctx))), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        loadCancelableReservations();
    }

    private JPanel buildTablePanel() {
        model.setColumnIdentifiers(new Object[] {
                "Reservation ID", "Instance ID", "Flight Num", "Airline", "From", "To",
                "Departure", "Arrival", "Ticket ID", "Seat", "Class", "Price"
        });
        table = new JTable(model);

        JButton cancelBtn = new JButton("Cancel Selected Reservation");
        cancelBtn.addActionListener(e -> cancelSelectedReservation());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        wrapper.add(cancelBtn, BorderLayout.SOUTH);
        return wrapper;
    }

    private void loadCancelableReservations() {
        try {
            model.setRowCount(0);
            List<Map<String, Object>> list = ctx.reservationDAO.getUpcomingReservations(ctx.currentUser);
            for (Map<String, Object> r : list) {
                model.addRow(new Object[] {
                        r.get("reservationID"), r.get("instanceID"), r.get("flightNum"),
                        r.get("airlineID"), r.get("depAirport"), r.get("arrAirport"),
                        r.get("departureTime"), r.get("arrivalTime"), r.get("ticketID"),
                        r.get("seatNum"), r.get("class"), r.get("price")
                });
            }
            if (list.isEmpty())
                JOptionPane.showMessageDialog(this, "You have no upcoming reservations to cancel.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load reservations: " + ex.getMessage());
        }
    }

    private void cancelSelectedReservation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            return;
        }

        String reservationID = model.getValueAt(row, 0).toString();
        String instanceID = model.getValueAt(row, 1).toString();

        try {
            double fee = ctx.reservationDAO.getCancellationFee(reservationID);
            String prompt = fee > 0
                    ? String.format("Cancelling this economy ticket will charge a $%,.2f fee. Continue?", fee)
                    : "Cancel this reservation? (No fee for business/first class.)";

            int confirm = JOptionPane.showConfirmDialog(this, prompt, "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            ctx.reservationDAO.cancelReservation(reservationID);

            String nextUser = ctx.waitlistDAO.popNextCustomer(instanceID);
            if (nextUser != null) {
                ctx.alertDAO.addAlert(nextUser,
                        "A seat is available for your waitlisted flight (Instance " + instanceID
                                + "). Please reserve it now.");
            }

            JOptionPane.showMessageDialog(this,
                    fee > 0
                            ? String.format("Reservation canceled. $%,.2f fee charged.", fee)
                            : "Reservation canceled.");
            loadCancelableReservations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cancellation failed: " + ex.getMessage());
        }
    }
}
