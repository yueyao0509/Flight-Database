package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertDAO {
    private final Connection con;

    public AlertDAO(Connection con) {
        this.con = con;
    }

    public void addAlert(String userID, String message) throws SQLException {
        String sql = "INSERT INTO alerts (userID, message) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setString(2, message);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> getAlerts(String userID) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM alerts WHERE userID = ? ORDER BY createdAt DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("alertID", rs.getInt("alertID"));
                    row.put("message", rs.getString("message"));
                    row.put("createdAt", rs.getTimestamp("createdAt"));
                    list.add(row);
                }
            }
        }
        return list;
    }
}
