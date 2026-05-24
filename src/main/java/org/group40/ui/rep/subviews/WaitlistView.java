package org.group40.ui.rep.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.rep.RepView;

public class WaitlistView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);
    private final JComboBox<String> instancePicker = new JComboBox<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public WaitlistView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("View Waitlist", "Back",
                () -> ctx.frame.showView(new RepView(ctx))),
                BorderLayout.NORTH);

        loadInstances();

        JList<String> list = new JList<>(model);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(dropdownBar("Flight:", instancePicker, "Show", this::loadWaitlist),
                BorderLayout.NORTH);
        center.add(scrollableList(list), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // auto-load the first one
        if (instancePicker.getItemCount() > 0)
            loadWaitlist();
    }

    private void loadInstances() {
        try {
            List<String> instances = ctx.waitlistDAO.listInstancesWithWaitlist();
            if (instances.isEmpty()) {
                instancePicker.addItem("(no waitlists)");
                instancePicker.setEnabled(false);
            } else {
                for (String i : instances)
                    instancePicker.addItem(i);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadWaitlist() {
        model.clear();
        Object selected = instancePicker.getSelectedItem();
        if (selected == null || selected.toString().startsWith("(no")) {
            model.addElement("No waitlists exist.");
            return;
        }
        String instanceID = selected.toString().split(" — ")[0].trim();
        try {
            String result = ctx.waitlistDAO.getWaitlistForInstance(instanceID);
            if (result.startsWith("No customers")) {
                model.addElement(result);
            } else {
                for (String line : result.split("\n")) {
                    if (!line.isBlank())
                        model.addElement(line);
                }
            }
        } catch (SQLException ex) {
            model.addElement("Error loading waitlist.");
            ex.printStackTrace();
        }
    }
}