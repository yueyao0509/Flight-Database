package org.group40.ui.admin.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.admin.AdminView;

public class ManageUsersView extends BaseView {
    private static final Color BG = new Color(200, 220, 255);
    private DefaultListModel<String> listModel;
    private JList<String> userList;

    public ManageUsersView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Manage Users", "Back",
                () -> ctx.frame.showView(new AdminView(ctx))),
                BorderLayout.NORTH);

        // list UI
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        add(scrollableList(userList), BorderLayout.CENTER);

        // right click context menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("Add User");
        JMenuItem deleteItem = new JMenuItem("Delete User");
        JMenuItem editItem = new JMenuItem("Edit User");

        popupMenu.add(addItem);
        popupMenu.add(deleteItem);
        popupMenu.add(editItem);

        addItem.addActionListener(e -> handleAddUser());
        deleteItem.addActionListener(e -> handleDeleteUser());
        editItem.addActionListener(e -> handleEditUser());

        // mouse listener for right clicks
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = userList.locationToIndex(e.getPoint());
                    if (row != -1) {
                        userList.setSelectedIndex(row);
                        popupMenu.show(userList, e.getX(), e.getY());
                    }
                }
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        listModel.clear();
        try {
            List<String> users = ctx.userDAO.getUsersList();
            if (users.isEmpty()) {
                listModel.addElement("No users found.");
            } else {
                for (String u : users) {
                    listModel.addElement(u);
                }
            }
        } catch (SQLException ex) {
            listModel.addElement("Error loading users.");
            ex.printStackTrace();
        }
    }

    private void handleAddUser() {
        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.isBlank())
            return;

        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null || password.isBlank())
            return;

        String firstName = JOptionPane.showInputDialog(this, "First name:");
        if (firstName == null)
            return;

        String lastName = JOptionPane.showInputDialog(this, "Last name:");
        if (lastName == null)
            return;

        String email = JOptionPane.showInputDialog(this, "Email:");
        if (email == null)
            return;

        String[] options = { "Admin", "Representative", "Customer" };
        int choice = JOptionPane.showOptionDialog(this, "Select User Role:", "Assign Role",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
        if (choice == JOptionPane.CLOSED_OPTION)
            return;

        String role = switch (choice) {
            case 0 -> "A";
            case 1 -> "R";
            default -> "C";
        };

        try {
            ctx.userDAO.addUser(username, password, firstName, lastName, email, role);
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
        }
    }

    private void handleDeleteUser() {
        String selected = userList.getSelectedValue();
        if (selected == null || selected.equals("No users found.") || selected.startsWith("Error")) {
            return;
        }

        try {
            String userID = selected.split("\\|")[0].replace("ID:", "").trim();

            if (userID.equals("U0")) {
                JOptionPane.showMessageDialog(this, "Cannot delete the default system admin account!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user " + userID + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                ctx.userDAO.deleteUser(userID);
                loadUsers();
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete user: They have active tickets or reservations in the database.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not delete user.");
            ex.printStackTrace();
        }
    }

    private void handleEditUser() {
        String selected = userList.getSelectedValue();
        if (selected == null || selected.equals("No users found.") || selected.startsWith("Error")) {
            return;
        }

        String userID = selected.split("\\|")[0].replace("ID:", "").trim();

        try {
            Map<String, String> current = ctx.userDAO.getUserById(userID);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "User not found.");
                return;
            }

            JTextField tfUsername = new JTextField(unwrap(current.get("username")));
            JTextField tfPassword = new JTextField(); // blank = keep existing
            JTextField tfFirstName = new JTextField(unwrap(current.get("firstName")));
            JTextField tfLastName = new JTextField(unwrap(current.get("lastName")));
            JTextField tfEmail = new JTextField(unwrap(current.get("email")));

            String[] roles = { "admin", "rep", "customer" };
            JComboBox<String> cbRole = new JComboBox<>(roles);
            cbRole.setSelectedItem(current.get("role"));

            JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
            panel.add(new JLabel("Username:"));
            panel.add(tfUsername);
            panel.add(new JLabel("Password:"));
            panel.add(tfPassword);
            panel.add(new JLabel("(blank = unchanged)"));
            panel.add(new JLabel(""));
            panel.add(new JLabel("First name:"));
            panel.add(tfFirstName);
            panel.add(new JLabel("Last name:"));
            panel.add(tfLastName);
            panel.add(new JLabel("Email:"));
            panel.add(tfEmail);
            panel.add(new JLabel("Role:"));
            panel.add(cbRole);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Edit User " + userID, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION)
                return;

            if (tfUsername.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Username cannot be blank.");
                return;
            }

            ctx.userDAO.updateUser(
                    userID,
                    tfUsername.getText().trim(),
                    tfPassword.getText(),
                    tfFirstName.getText().trim(),
                    tfLastName.getText().trim(),
                    tfEmail.getText().trim(),
                    String.valueOf(cbRole.getSelectedItem()));

            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
        }
    }

    private static String unwrap(String s) {
        // Field uses "—" for null in display; treat that as empty when editing
        return s == null || "—".equals(s) ? "" : s;
    }
}