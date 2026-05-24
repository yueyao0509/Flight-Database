package org.group40.ui.customer;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;

public class AlertsView extends BaseView {
    private static final Color BG = new Color(200, 245, 220);
    private final DefaultTableModel model = new DefaultTableModel();

    public AlertsView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("My Alerts", "Back", () -> ctx.frame.showView(new CustomerView(ctx))), "North");
        add(buildTable(), "Center");
        loadAlerts();
    }

    private JScrollPane buildTable() {
        model.setColumnIdentifiers(new Object[] { "Alert ID", "Message", "Created At" });
        JTable table = new JTable(model);
        table.setRowHeight(28);
        return new JScrollPane(table);
    }

    private void loadAlerts() {
        try {
            model.setRowCount(0);
            List<Map<String, Object>> alerts = ctx.alertDAO.getAlerts(ctx.currentUser);
            for (Map<String, Object> a : alerts) {
                model.addRow(new Object[] { a.get("alertID"), a.get("message"), a.get("createdAt") });
            }
            if (alerts.isEmpty()) {
                model.addRow(new Object[] { "—", "No alerts yet.", "—" });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load alerts: " + ex.getMessage());
        }
    }
}
