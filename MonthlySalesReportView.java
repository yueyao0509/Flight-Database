package org.group40.ui.admin.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.admin.AdminView;

public class MonthlySalesReportView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);

    public MonthlySalesReportView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Monthly Sales Report", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        add(scrollableList(list), BorderLayout.CENTER);

        try {
            List<String> rows = ctx.reservationDAO.getMonthlySales();
            if (rows.isEmpty())
                model.addElement("No sales data available.");
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error loading sales data.");
            ex.printStackTrace();
        }
    }
}