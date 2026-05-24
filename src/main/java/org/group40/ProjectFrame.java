package org.group40;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.group40.db.DatabaseConnection;
import org.group40.ui.HomeView;

// main application frame
// responsible only for bootstrapping the app and swapping views in and out
public class ProjectFrame extends JFrame {

    private AppContext ctx;

    public void initialize() throws Exception {
        Connection con = DatabaseConnection.get();
        this.ctx = new AppContext(this, con);

        setSize(500, 300);
        setMinimumSize(new Dimension(300, 200));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        showView(new HomeView(ctx));
    }

    // replaces the current content with the given view and updates the title
    public void showView(JPanel view) {
        getContentPane().removeAll();
        getContentPane().add(view);
        setTitle(titleFor(view));
        revalidate();
        repaint();
    }

    private String titleFor(JPanel view) {
        String name = view.getClass().getSimpleName();
        if (name.endsWith("View"))
            name = name.substring(0, name.length() - 4);

        // split camelCase into words: "ManageUsers" -> "Manage Users"
        return name.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public static void main(String[] args) throws Exception {
        try {
            // fails if DB connection cannot be established
            DatabaseConnection.get();
        } catch (SQLException e) {
            System.out.println("Unable to create a connection to the database");
            e.printStackTrace();
            System.exit(0);
        }
        new ProjectFrame().initialize();
    }
}
