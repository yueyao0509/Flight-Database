package org.group40.ui.admin.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.db.ReservationDAO.RevenueSummary;
import org.group40.ui.BaseView;
import org.group40.ui.admin.AdminView;

public class RevenueSummaryView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JComboBox<String> groupBy = new JComboBox<>(
            new String[] { "By Flight", "By Airline", "By Customer" });

    public RevenueSummaryView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Revenue Summary", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);

        JList<String> list = new JList<>(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(buildHeader(), BorderLayout.NORTH);
        center.add(scrollableList(list), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        groupBy.setFont(MAIN_FONT);
        groupBy.addActionListener(e -> reload());
        reload();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel totals = new JLabel(buildTotalsText());
        totals.setFont(MAIN_FONT);
        totals.setHorizontalAlignment(JLabel.CENTER);
        header.add(totals, BorderLayout.NORTH);

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        selector.setOpaque(false);
        JLabel lbl = new JLabel("Group by:");
        lbl.setFont(MAIN_FONT);
        selector.add(lbl);
        selector.add(groupBy);
        header.add(selector, BorderLayout.SOUTH);
        return header;
    }

    private String buildTotalsText() {
        try {
            RevenueSummary s = ctx.reservationDAO.getRevenueSummary();
            return String.format("Total: $%,.2f  |  Fees: $%,.2f  |  %d reservations",
                    s.totalRevenue(), s.totalBookingFees(), s.numReservations());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error loading totals.";
        }
    }

    private void reload() {
        model.clear();
        try {
            List<String> rows = switch (groupBy.getSelectedIndex()) {
                case 1 -> ctx.reservationDAO.getRevenueByAirline();
                case 2 -> ctx.reservationDAO.getRevenueByCustomer();
                default -> ctx.reservationDAO.getRevenueByFlight();
            };
            if (rows.isEmpty())
                model.addElement("No revenue data.");
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error loading revenue data.");
            ex.printStackTrace();
        }
    }
}