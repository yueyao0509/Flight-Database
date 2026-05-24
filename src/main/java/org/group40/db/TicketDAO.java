package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketDAO {
    private final Connection con;

    public TicketDAO(Connection con) {
        this.con = con;
    }

    public void createTicket(String reservationID, String instanceID, String seatNum, String cls) throws SQLException {
        String sql = "INSERT INTO ticket (reservationID, instanceID, seatNum, class) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ps.setString(2, instanceID);
            ps.setString(3, seatNum);
            ps.setString(4, cls);
            ps.executeUpdate();
        }
    }

    public boolean updateSeat(String ticketID, String newSeatNum) throws SQLException {
        String sql = "UPDATE ticket SET seatNum = ? WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newSeatNum);
            ps.setString(2, ticketID);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePrice(String ticketID, double newPrice) throws SQLException {
        String sql = "UPDATE ticket SET price = ? WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, ticketID);
            return ps.executeUpdate() > 0;
        }
    }

    public String getTicket(String ticketID) throws SQLException {
        String sql = "SELECT ticketID, reservationID, instanceID, seatNum, class, price FROM ticket WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ticketID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "TicketID: " + rs.getString("ticketID")
                            + " | Reservation: " + rs.getString("reservationID")
                            + " | Instance: " + rs.getString("instanceID")
                            + " | Seat: " + rs.getString("seatNum")
                            + " | Class: " + rs.getString("class")
                            + " | Price: $" + rs.getDouble("price");
                }
            }
        }
        return "Ticket not found.";
    }

    public boolean deleteTicket(String ticketID) throws SQLException {
        String sql = "DELETE FROM ticket WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ticketID);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTicketByReservation(String reservationID) throws SQLException {
        String sql = "DELETE FROM ticket WHERE reservationID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            return ps.executeUpdate() > 0;
        }
    }
}
