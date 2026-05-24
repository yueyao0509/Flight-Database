package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDAO {
    private final Connection con;

    public UserDAO(Connection con) {
        this.con = con;
    }

    public String authenticate(String username, String password) throws SQLException {
        String sql = "SELECT role FROM user_account WHERE username = ? AND password = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("role");
                return null;
            }
        }
    }

    public String getUserIDByUsername(String username) throws SQLException {
        String sql = "SELECT userID FROM user_account WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("userID");
                return null;
            }
        }
    }

    public void addUser(String username, String password,
            String firstName, String lastName, String email,
            String role) throws SQLException {
        String sql = """
                    INSERT INTO user_account (username, password, firstName, lastName, email, role)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setString(5, email);
            ps.setString(6, normalizeRole(role));
            ps.executeUpdate();
        }
    }

    public String getAllUsers() throws SQLException {
        String sql = "SELECT userID, username, password, role FROM user_account ORDER BY userID";
        StringBuilder sb = new StringBuilder();

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sb.append("ID: ").append(rs.getString("userID"))
                        .append(" | Username: ").append(rs.getString("username"))
                        .append(" | Password: ").append(rs.getString("password"))
                        .append(" | Role: ").append(rs.getString("role"))
                        .append("\n");
            }
        }
        if (sb.length() == 0)
            return "No users found.";
        return sb.toString();
    }

    public List<String> getUsersList() throws SQLException {
        String sql = """
                    SELECT userID, username, firstName, lastName, email, role
                    FROM user_account ORDER BY userID
                """;
        List<String> users = new java.util.ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(String.format("ID: %s | %s | %s %s | %s | %s",
                        rs.getString("userID"),
                        rs.getString("username"),
                        nz(rs.getString("firstName")),
                        nz(rs.getString("lastName")),
                        nz(rs.getString("email")),
                        rs.getString("role")));
            }
        }
        return users;
    }

    private String normalizeRole(String role) {
        if (role == null)
            return "customer";
        return switch (role) {
            case "A", "admin" -> "admin";
            case "R", "rep", "representative" -> "rep";
            default -> "customer";
        };
    }

    private static String nz(String s) {
        return s == null ? "—" : s;
    }

    public void deleteUser(String userID) throws SQLException {
        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            // Delete tickets (must precede reservation delete due to FK)
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE t FROM ticket t JOIN reservation r ON t.reservationID = r.reservationID WHERE r.userID = ?")) {
                ps.setString(1, userID);
                ps.executeUpdate();
            }
            for (String table : new String[] { "reservation", "alerts", "waitlist", "qna" }) {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM " + table + " WHERE userID = ?")) {
                    ps.setString(1, userID);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM user_account WHERE userID = ?")) {
                ps.setString(1, userID);
                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    public record TopCustomer(int userID, String username,
            double totalSpent, int numReservations) {
    }

    public TopCustomer getTopCustomer() throws SQLException {
        String sql = """
                    SELECT u.userID,
                           u.username,
                           SUM(r.totalFare + COALESCE(r.bookingFee, 0)) AS totalSpent,
                           COUNT(r.reservationID) AS numReservations
                    FROM user_account u
                    JOIN reservation r ON u.userID = r.userID
                    WHERE u.role = 'customer'
                    GROUP BY u.userID, u.username
                    ORDER BY totalSpent DESC
                    LIMIT 1
                """;
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new TopCustomer(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getDouble("totalSpent"),
                        rs.getInt("numReservations"));
            }
            return null;
        }
    }

    public Map<String, String> getUserById(String userID) throws SQLException {
        String sql = """
                    SELECT username, firstName, lastName, email, role
                    FROM user_account WHERE userID = ?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                Map<String, String> m = new java.util.HashMap<>();
                m.put("username", rs.getString("username"));
                m.put("firstName", rs.getString("firstName"));
                m.put("lastName", rs.getString("lastName"));
                m.put("email", rs.getString("email"));
                m.put("role", rs.getString("role"));
                return m;
            }
        }
    }

    public void updateUser(String userID, String username, String password,
            String firstName, String lastName, String email,
            String role) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                    UPDATE user_account
                    SET username = ?, firstName = ?, lastName = ?, email = ?, role = ?
                """);
        if (password != null && !password.isBlank())
            sql.append(", password = ?");
        sql.append(" WHERE userID = ?");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setString(i++, username);
            ps.setString(i++, firstName);
            ps.setString(i++, lastName);
            ps.setString(i++, email);
            ps.setString(i++, normalizeRole(role));
            if (password != null && !password.isBlank())
                ps.setString(i++, password);
            ps.setString(i++, userID);
            ps.executeUpdate();
        }
    }

    public List<String> listCustomers() throws SQLException {
        List<String> customers = new java.util.ArrayList<>();
        String sql = "SELECT username, firstName, lastName FROM user_account WHERE role = 'customer' ORDER BY username";
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String first = rs.getString("firstName");
                String last = rs.getString("lastName");
                String name = (first == null ? "" : first) + " " + (last == null ? "" : last);
                customers.add(rs.getString("username") + " — " + name.trim());
            }
        }
        return customers;
    }
}
