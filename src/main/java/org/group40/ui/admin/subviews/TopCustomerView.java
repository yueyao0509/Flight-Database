package org.group40.ui.admin.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.db.UserDAO.TopCustomer;
import org.group40.ui.BaseView;
import org.group40.ui.admin.AdminView;

public class TopCustomerView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);

    public TopCustomerView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Top Customer", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);
        add(centered(buildContent()), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        try {
            TopCustomer top = ctx.userDAO.getTopCustomer();
            if (top == null)
                return messagePanel("No customer purchases yet.");

            JPanel form = formPanel(420);
            addRow(form, "Customer", new JLabel(top.username()));
            addRow(form, "User ID", new JLabel(String.valueOf(top.userID())));
            addRow(form, "Total Spent", new JLabel(String.format("$%,.2f", top.totalSpent())));
            addRow(form, "Reservations", new JLabel(String.valueOf(top.numReservations())));
            return form;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return messagePanel("Error loading top customer.");
        }
    }

    private JPanel messagePanel(String text) {
        JLabel msg = new JLabel(text);
        msg.setFont(MAIN_FONT);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(msg);
        return p;
    }
}