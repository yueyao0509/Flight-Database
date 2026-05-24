package org.group40.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.group40.AppContext;

public class RegisterView extends BaseView {

    private final JTextField tfUser = new JTextField();
    private final JPasswordField tfPass = new JPasswordField();
    private final JTextField tfFirstName = new JTextField();
    private final JTextField tfLastName = new JTextField();
    private final JTextField tfEmail = new JTextField();
    private final JRadioButton rbCustomer = new JRadioButton("Customer");
    private final JRadioButton rbRep = new JRadioButton("Representative");
    private final JLabel msg = new JLabel();

    public RegisterView(AppContext ctx) {
        super(ctx, new Color(230, 140, 140));
        msg.setFont(MAIN_FONT);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(rbCustomer);
        roleGroup.add(rbRep);
        rbCustomer.setFont(MAIN_FONT);
        rbRep.setFont(MAIN_FONT);
        rbCustomer.setOpaque(false);
        rbRep.setOpaque(false);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.setOpaque(false);
        rolePanel.add(rbCustomer);
        rolePanel.add(rbRep);

        add(topBar("", "Back", () -> ctx.frame.showView(new HomeView(ctx))), BorderLayout.NORTH);

        JPanel form = formPanel(440);
        addRow(form, "Username", tfUser);
        addRow(form, "Password", tfPass);
        addRow(form, "First Name", tfFirstName);
        addRow(form, "Last Name", tfLastName);
        addRow(form, "Email", tfEmail);
        addRow(form, "Role", rolePanel);
        addFullRow(form, msg);

        add(centered(form), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setOpaque(false);
        buttons.add(smallButton("Add User", this::handleAdd));
        buttons.add(smallButton("Clear", this::handleClear));
        add(buttons, BorderLayout.SOUTH);
    }

    private void handleAdd() {
        try {
            String username = tfUser.getText();
            if (username.isBlank()) {
                msg.setText("Username cannot be blank");
                return;
            }

            String password = new String(tfPass.getPassword());
            if (password.isBlank()) {
                msg.setText("Password cannot be blank");
                return;
            }

            String firstName = tfFirstName.getText();
            if (firstName.isBlank()) {
                msg.setText("First name cannot be blank");
                return;
            }

            String lastName = tfLastName.getText();
            if (lastName.isBlank()) {
                msg.setText("Last name cannot be blank");
                return;
            }

            String email = tfEmail.getText();
            if (email.isBlank()) {
                msg.setText("Email cannot be blank");
                return;
            }

            if (!rbCustomer.isSelected() && !rbRep.isSelected()) {
                msg.setText("Please select a role");
                return;
            }

            String role = rbCustomer.isSelected() ? "C" : "R";
            ctx.userDAO.addUser(username, password, firstName, lastName, email, role);
            msg.setText("User has been added");
        } catch (SQLException e) {
            msg.setText("Unable to add new user");
            e.printStackTrace();
        }
    }

    private void handleClear() {
        tfUser.setText("");
        tfPass.setText("");
        tfFirstName.setText("");
        tfLastName.setText("");
        tfEmail.setText("");
        msg.setText("");
        rbCustomer.setSelected(false);
        rbRep.setSelected(false);
    }
}