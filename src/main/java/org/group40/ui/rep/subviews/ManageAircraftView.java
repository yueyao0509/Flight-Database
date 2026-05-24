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

public class ManageAircraftView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    public ManageAircraftView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Manage Aircraft", "Back",
                () -> ctx.frame.showView(new RepView(ctx))),
                BorderLayout.NORTH);
        add(scrollableList(list), BorderLayout.CENTER);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem add = new JMenuItem("Add Aircraft");
        JMenuItem del = new JMenuItem("Delete Aircraft");
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
            List<String> rows = ctx.catalogDAO.listAircraft();
            if (rows.isEmpty())
                model.addElement("No aircraft.");
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error: " + ex.getMessage());
        }
    }

    private void handleAdd() {
        try {
            String id = JOptionPane.showInputDialog(this, "Aircraft ID:");
            if (id == null || id.isBlank())
                return;
            String airlineID = JOptionPane.showInputDialog(this, "Airline ID (e.g. AA):");
            if (airlineID == null)
                return;
            String modelStr = JOptionPane.showInputDialog(this, "Model:");
            if (modelStr == null)
                return;
            String capStr = JOptionPane.showInputDialog(this, "Capacity:");
            if (capStr == null)
                return;
            String rangeStr = JOptionPane.showInputDialog(this, "Range (km):");
            if (rangeStr == null)
                return;

            ctx.catalogDAO.addAircraft(id.trim(), airlineID.trim().toUpperCase(),
                    modelStr, Integer.parseInt(capStr.trim()), Integer.parseInt(rangeStr.trim()));
            load();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity and range must be numbers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleDelete() {
        String selected = list.getSelectedValue();
        if (selected == null || selected.startsWith("No ") || selected.startsWith("Error"))
            return;
        String id = selected.split("\\|")[0].trim();
        if (JOptionPane.showConfirmDialog(this, "Delete aircraft " + id + "?") != JOptionPane.YES_OPTION)
            return;
        try {
            ctx.catalogDAO.deleteAircraft(id);
            load();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete — aircraft may be referenced by flights.");
        }
    }
}