package org.group40.ui.rep.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.rep.RepView;

public class EditReservationView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);
    private final JTextField tfReservationID = new JTextField();
    private final JTextField tfNewInstanceID = new JTextField();
    private final JLabel msg = new JLabel(" ");

    public EditReservationView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Edit Reservation", "Back",
                () -> ctx.frame.showView(new RepView(ctx))),
                BorderLayout.NORTH);

        JPanel form = formPanel(440);
        addRow(form, "Reservation ID", tfReservationID);
        addRow(form, "New Instance ID", tfNewInstanceID);
        addFullRow(form, msg);

        add(centered(form), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(smallButton("Update Flight", this::handleUpdate));
        buttons.add(smallButton("Cancel Reservation", this::handleCancel));
        add(buttons, BorderLayout.SOUTH);
    }

    private void handleUpdate() {
        String resID = tfReservationID.getText().trim();
        String newID = tfNewInstanceID.getText().trim();
        if (resID.isBlank() || newID.isBlank()) {
            msg.setText("Both fields are required.");
            return;
        }
        try {
            boolean ok = ctx.reservationDAO.updateReservation(resID, newID);
            msg.setText(ok ? "Updated successfully." : "Update failed (no seats or invalid ID).");
        } catch (SQLException ex) {
            msg.setText("Database error: " + ex.getMessage());
        }
    }

    private void handleCancel() {
        String resID = tfReservationID.getText().trim();
        if (resID.isBlank()) {
            msg.setText("Reservation ID required.");
            return;
        }
        try {
            double fee = ctx.reservationDAO.getCancellationFee(resID);
            String prompt = fee > 0
                    ? String.format("Economy cancel fee: $%,.2f. Continue?", fee)
                    : "Cancel reservation? (No fee.)";
            if (JOptionPane.showConfirmDialog(this, prompt) != JOptionPane.YES_OPTION)
                return;
            ctx.reservationDAO.cancelReservation(resID);
            msg.setText("Reservation canceled.");
        } catch (SQLException ex) {
            msg.setText("Error: " + ex.getMessage());
        }
    }
}