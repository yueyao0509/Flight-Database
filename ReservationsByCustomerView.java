package org.group40.ui.admin.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.admin.AdminView;

public class ReservationsByCustomerView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);
    private final JTextField tfUserID = new JTextField(8);
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public ReservationsByCustomerView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Reservations by Customer", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);

        JList<String> list = new JList<>(model);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(searchBar("User ID:", tfUserID, "Search", this::handleSearch),
                BorderLayout.NORTH);
        center.add(scrollableList(list), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void handleSearch() {
        model.clear();
        String userID = tfUserID.getText().trim();
        if (userID.isEmpty()) {
            model.addElement("Please enter a user ID.");
            return;
        }
        try {
            List<String> rows = ctx.reservationDAO.getReservationsByCustomer(userID);
            if (rows.isEmpty())
                model.addElement("No reservations found.");
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error loading reservations.");
            ex.printStackTrace();
        }
    }
}