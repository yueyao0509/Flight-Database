package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QADAO {
    private final Connection con;

    public QADAO(Connection con) {
        this.con = con;
    }

    public String askQuestion(String userID, String text) throws SQLException {
        String sql = "INSERT INTO qna (userID, questionText, askedAt) VALUES (?, ?, NOW())";
        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userID);
            ps.setString(2, text);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    return String.valueOf(keys.getInt(1));
            }
        }
        return null;
    }

    public List<Map<String, Object>> getAllQuestions() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM qna ORDER BY askedAt DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(mapQuestion(rs));
        }
        return list;
    }

    public List<Map<String, Object>> getUnansweredQuestions() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM qna WHERE answerText IS NULL ORDER BY askedAt ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(mapQuestion(rs));
        }
        return list;
    }

    public void answerQuestion(String questionID, String answer) throws SQLException {
        String sql = "UPDATE qna SET answerText = ?, answeredAt = NOW() WHERE questionID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, answer);
            ps.setString(2, questionID);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> searchQuestions(String keyword) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM qna WHERE questionText LIKE ? OR answerText LIKE ? ORDER BY askedAt DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapQuestion(rs));
            }
        }
        return list;
    }

    private Map<String, Object> mapQuestion(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        row.put("questionID", rs.getInt("questionID"));
        row.put("userID", rs.getInt("userID"));
        row.put("questionText", rs.getString("questionText"));
        row.put("answerText", rs.getString("answerText"));
        row.put("askedAt", rs.getTimestamp("askedAt"));
        row.put("answeredAt", rs.getTimestamp("answeredAt"));
        return row;
    }
}
