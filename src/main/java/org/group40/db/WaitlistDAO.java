package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WaitlistDAO {
    private final Connection con;

    public WaitlistDAO(Connection con) {
        this.con = con;
    }

    public String addToWaitlist(String userID, String instanceID) throws SQLException {
        String sql = "INSERT IGNORE INTO waitlist (userID, instanceID, timestamp) VALUES (?, ?, NOW())";
        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userID);
            ps.setString(2, instanceID);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    return String.valueOf(keys.getInt(1));
            }
        }
        return "already_waitlisted";
    }

    public String popNextCustomer(String instanceID) throws SQLException {
        String sql = "SELECT waitlistID, userID FROM waitlist WHERE instanceID = ? ORDER BY timestamp ASC LIMIT 1";
        String waitlistID = null;
        String userID = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    waitlistID = rs.getString("waitlistID");
                    userID = rs.getString("userID");
                }
            }
        }

        if (waitlistID == null)
            return null;
        deleteWaitlistEntry(waitlistID);
        return userID;
    }

    public String getWaitlistForInstance(String instanceID) throws SQLException {
        String sql = "SELECT waitlistID, userID, timestamp FROM waitlist WHERE instanceID = ? ORDER BY timestamp ASC";
        StringBuilder sb = new StringBuilder();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("WaitlistID: ").append(rs.getString("waitlistID"))
                            .append(" | Customer: ").append(rs.getString("userID"))
                            .append(" | Time: ").append(rs.getTimestamp("timestamp"))
                            .append("\n");
                }
            }
        }
        if (sb.length() == 0)
            return "No customers on waitlist.";
        return sb.toString();
    }

    private void deleteWaitlistEntry(String waitlistID) throws SQLException {
        String sql = "DELETE FROM waitlist WHERE waitlistID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, waitlistID);
            ps.executeUpdate();
        }
    }

    public List<String> listInstancesWithWaitlist() throws SQLException {
        List<String> entries = new java.util.ArrayList<>();
        String sql = """
                    SELECT fi.instanceID, f.airlineID, f.flightNum,
                           f.depAirport, f.arrAirport, fi.flightDate,
                           COUNT(w.waitlistID) AS waiters
                    FROM waitlist w
                    JOIN flight_instance fi ON w.instanceID = fi.instanceID
                    JOIN flight f ON fi.flightID = f.flightID
                    GROUP BY fi.instanceID, f.airlineID, f.flightNum,
                             f.depAirport, f.arrAirport, fi.flightDate
                    ORDER BY waiters DESC
                """;
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                entries.add(String.format("%d — %s%d %s→%s on %s (%d waiting)",
                        rs.getInt("instanceID"),
                        rs.getString("airlineID"), rs.getInt("flightNum"),
                        rs.getString("depAirport"), rs.getString("arrAirport"),
                        rs.getDate("flightDate"),
                        rs.getInt("waiters")));
            }
        }
        return entries;
    }
}
