package org.group40.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.group40.AppContext;
import org.group40.ui.admin.AdminView;
import org.group40.ui.customer.CustomerView;
import org.group40.ui.rep.RepView;

public class LoginView extends BaseView {
    private final JTextField tfUser = new JTextField();
    private final JPasswordField tfPass = new JPasswordField();
    private final JLabel msg = new JLabel();

    public LoginView(AppContext ctx) {
        super(ctx, new Color(230, 140, 140));
        msg.setFont(MAIN_FONT);

        setLayout(new BorderLayout());
        add(topBar("", "Back", () -> ctx.frame.showView(new HomeView(ctx))), BorderLayout.NORTH);

        JPanel form = formPanel(360);
        addRow(form, "Username", tfUser);
        addRow(form, "Password", tfPass);
        addFullRow(form, msg);

        add(centered(form), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setOpaque(false);
        buttons.add(smallButton("Login", this::handleLogin));
        buttons.add(smallButton("Clear", this::handleClear));
        add(buttons, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        try {
            String role = ctx.userDAO.authenticate(tfUser.getText(), new String(tfPass.getPassword()));
            if (role == null) {
                msg.setText("Unknown user or incorrect password");
                return;
            }
            ctx.loggedIn = true;
            String userID = ctx.userDAO.getUserIDByUsername(tfUser.getText());
            ctx.currentUser = userID;
            ctx.currentRole = role;
            switch (role) {
                case "admin" -> ctx.frame.showView(new AdminView(ctx));
                case "rep" -> ctx.frame.showView(new RepView(ctx));
                default -> ctx.frame.showView(new CustomerView(ctx));
            }
        } catch (SQLException e) {
            msg.setText("Error communicating with the database");
            e.printStackTrace();
        }
    }

    private void handleClear() {
        tfUser.setText("");
        tfPass.setText("");
        msg.setText("");
    }
}