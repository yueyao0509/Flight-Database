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

public class ManageAirportsView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    public ManageAirportsView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Manage Airports", "Back",
                () -> ctx.frame.showView(new RepView(ctx))),
                BorderLayout.NORTH);
        add(scrollableList(list), BorderLayout.CENTER);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem add = new JMenuItem("Add Airport");
        JMenuItem del = new JMenuItem("Delete Airport");
        JMenuItem upd = new JMenuItem("Edit Airport");
        menu.add(add);
        menu.add(upd);
        menu.add(del);
        add.addActionListener(e -> handleAdd());
        del.addActionListener(e -> handleDelete());
        upd.addActionListener(e -> handleEdit());

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
            List<String> rows = ctx.catalogDAO.listAirports();
            if (rows.isEmpty())
                model.addElement("No airports.");
            else
                rows.forEach(model::addElement);
        } catch (SQLException ex) {
            model.addElement("Error: " + ex.getMessage());
        }
    }

    private void handleAdd() {
        String id = JOptionPane.showInputDialog(this, "Airport ID (3 chars):");
        if (id == null || id.isBlank())
            return;
        String name = JOptionPane.showInputDialog(this, "Name:");
        if (name == null)
            return;
        String city = JOptionPane.showInputDialog(this, "City:");
        if (city == null)
            return;
        String country = JOptionPane.showInputDialog(this, "Country:");
        if (country == null)
            return;
        try {
            ctx.catalogDAO.addAirport(id.trim().toUpperCase(), name, city, country);
            load();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleEdit() {
        String selected = list.getSelectedValue();
        if (selected == null || selected.startsWith("No ") || selected.startsWith("Error"))
            return;
        String id = selected.split("\\|")[0].trim();
        String name = JOptionPane.showInputDialog(this, "New name:");
        if (name == null)
            return;
        String city = JOptionPane.showInputDialog(this, "New city:");
        if (city == null)
            return;
        String country = JOptionPane.showInputDialog(this, "New country:");
        if (country == null)
            return;
        try {
            ctx.catalogDAO.updateAirport(id, name, city, country);
            load();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleDelete() {
        String selected = list.getSelectedValue();
        if (selected == null || selected.startsWith("No ") || selected.startsWith("Error"))
            return;
        String id = selected.split("\\|")[0].trim();
        if (JOptionPane.showConfirmDialog(this, "Delete airport " + id + "?") != JOptionPane.YES_OPTION)
            return;
        try {
            ctx.catalogDAO.deleteAirport(id);
            load();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete — airport may be referenced by flights.");
        }
    }
}