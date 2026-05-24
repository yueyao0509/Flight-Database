package org.group40.ui.customer.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.CustomerView;

public class JoinWaitlistView extends BaseView {
    private static final Color BG = new Color(200, 245, 220);
    private final JTextField instanceField = new JTextField(10);

    public JoinWaitlistView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Join Waitlist", "Back", () -> ctx.frame.showView(new CustomerView(ctx))), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setOpaque(false);
        panel.add(new JLabel("Flight Instance ID:"));
        panel.add(instanceField);

        JButton joinBtn = new JButton("Join Waitlist");
        joinBtn.addActionListener(e -> joinWaitlist());
        panel.add(new JLabel(""));
        panel.add(joinBtn);
        return panel;
    }

    private void joinWaitlist() {
        String instanceID = instanceField.getText().trim();
        if (instanceID.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter an instance ID.");
            return;
        }
        try {
            ctx.waitlistDAO.addToWaitlist(ctx.currentUser, instanceID);
            JOptionPane.showMessageDialog(this, "Added to waitlist.");
            ctx.frame.showView(new CustomerView(ctx));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to join waitlist: " + ex.getMessage());
        }
    }
}
