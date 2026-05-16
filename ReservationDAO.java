package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationDAO {
    private final Connection con;

    public ReservationDAO(Connection con) {
        this.con = con;
    }

    public String createReservation(String userID, String instanceID, String seatClass) throws SQLException {
        if (!hasAvailableSeats(instanceID))
            return "FULL";

        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            double fare = getBaseFare(instanceID);
            double bookingFee = 20.00;

            String sql = "INSERT INTO reservation (userID, instanceID, totalFare, bookingFee) VALUES (?, ?, ?, ?)";
            String reservationID = null;
            try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, userID);
                ps.setString(2, instanceID);
                ps.setDouble(3, fare + bookingFee);
                ps.setDouble(4, bookingFee);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        reservationID = String.valueOf(keys.getInt(1));
                }
            }

            if (reservationID == null)
                throw new SQLException("Reservation ID was not generated.");
            createTicketForReservation(reservationID, instanceID, seatClass, fare + bookingFee);
            decreaseSeat(instanceID);
            con.commit();
            return reservationID;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    public String createReservation(String userID, String instanceID) throws SQLException {
        return createReservation(userID, instanceID, "economy");
    }

    public boolean updateReservation(String reservationID, String newInstanceID) throws SQLException {
        if (!hasAvailableSeats(newInstanceID))
            return false;
        String oldInstanceID = getInstanceForReservation(reservationID);
        if (oldInstanceID == null)
            return false;

        String sql = "UPDATE reservation SET instanceID = ? WHERE reservationID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newInstanceID);
            ps.setString(2, reservationID);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = con.prepareStatement("UPDATE ticket SET instanceID = ? WHERE reservationID = ?")) {
            ps.setString(1, newInstanceID);
            ps.setString(2, reservationID);
            ps.executeUpdate();
        }

        increaseSeat(oldInstanceID);
        decreaseSeat(newInstanceID);
        return true;
    }

    private static final double ECONOMY_CANCEL_FEE = 75.00;

    /**
     * Returns the fee that would be charged to cancel this reservation. 0 = free.
     */
    public double getCancellationFee(String reservationID) throws SQLException {
        String sql = "SELECT class FROM ticket WHERE reservationID = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && "economy".equalsIgnoreCase(rs.getString("class"))) {
                    return ECONOMY_CANCEL_FEE;
                }
            }
        }
        return 0.0;
    }

    public boolean cancelReservation(String reservationID) throws SQLException {
        String instanceID = getInstanceForReservation(reservationID);
        if (instanceID == null)
            return false;

        double fee = getCancellationFee(reservationID);

        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            if (fee > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE reservation SET cancelFee = ? WHERE reservationID = ?")) {
                    ps.setDouble(1, fee);
                    ps.setString(2, reservationID);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM ticket WHERE reservationID = ?")) {
                ps.setString(1, reservationID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM reservation WHERE reservationID = ?")) {
                ps.setString(1, reservationID);
                ps.executeUpdate();
            }
            increaseSeat(instanceID);
            con.commit();
            return true;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    public List<Map<String, Object>> getUpcomingReservations(String userID) throws SQLException {
        String sql = reservationSelectSql()
                + " WHERE r.userID = ? AND TIMESTAMP(fi.flightDate, f.depTIME) > NOW() ORDER BY departureTime ASC";
        return reservationList(userID, sql);
    }

    public List<Map<String, Object>> getPastReservations(String userID) throws SQLException {
        String sql = reservationSelectSql()
                + " WHERE r.userID = ? AND TIMESTAMP(fi.flightDate, f.depTIME) < NOW() ORDER BY departureTime DESC";
        return reservationList(userID, sql);
    }

    private String reservationSelectSql() {
        return "SELECT r.reservationID, r.instanceID, f.flightNum, f.airlineID, " +
                "f.depAirport, f.arrAirport, " +
                "TIMESTAMP(fi.flightDate, f.depTIME) AS departureTime, " +
                "CASE WHEN f.arrTIME < f.depTIME THEN TIMESTAMP(DATE_ADD(fi.flightDate, INTERVAL 1 DAY), f.arrTIME) ELSE TIMESTAMP(fi.flightDate, f.arrTIME) END AS arrivalTime, "
                +
                "t.ticketID, t.seatNum, t.class, COALESCE(t.price, r.totalFare) AS price " +
                "FROM reservation r " +
                "JOIN flight_instance fi ON r.instanceID = fi.instanceID " +
                "JOIN flight f ON fi.flightID = f.flightID " +
                "LEFT JOIN ticket t ON r.reservationID = t.reservationID ";
    }

    private List<Map<String, Object>> reservationList(String userID, String sql) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("reservationID", rs.getInt("reservationID"));
                    row.put("instanceID", rs.getInt("instanceID"));
                    row.put("flightNum", rs.getInt("flightNum"));
                    row.put("airlineID", rs.getString("airlineID"));
                    row.put("depAirport", rs.getString("depAirport"));
                    row.put("arrAirport", rs.getString("arrAirport"));
                    row.put("departureTime", rs.getTimestamp("departureTime"));
                    row.put("arrivalTime", rs.getTimestamp("arrivalTime"));
                    row.put("ticketID", rs.getObject("ticketID"));
                    row.put("seatNum", rs.getString("seatNum"));
                    row.put("class", rs.getString("class"));
                    row.put("price", rs.getDouble("price"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    private boolean hasAvailableSeats(String instanceID) throws SQLException {
        String sql = "SELECT seatsAvail FROM flight_instance WHERE instanceID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("seatsAvail") > 0;
            }
        }
    }

    private double getBaseFare(String instanceID) throws SQLException {
        String sql = "SELECT baseFare FROM flight_instance WHERE instanceID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("baseFare");
            }
        }
        throw new SQLException("Flight instance not found: " + instanceID);
    }

    private void createTicketForReservation(String reservationID, String instanceID, String seatClass, double price)
            throws SQLException {
        String sql = "INSERT INTO ticket (reservationID, instanceID, seatNum, class, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ps.setString(2, instanceID);
            ps.setString(3, generateSeat(instanceID));
            ps.setString(4, seatClass == null || seatClass.isBlank() ? "economy" : seatClass);
            ps.setDouble(5, price);
            ps.executeUpdate();
        }
    }

    private void decreaseSeat(String instanceID) throws SQLException {
        String sql = "UPDATE flight_instance SET seatsAvail = seatsAvail - 1 WHERE instanceID = ? AND seatsAvail > 0";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            ps.executeUpdate();
        }
    }

    private void increaseSeat(String instanceID) throws SQLException {
        String sql = "UPDATE flight_instance SET seatsAvail = seatsAvail + 1 WHERE instanceID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            ps.executeUpdate();
        }
    }

    private String getInstanceForReservation(String reservationID) throws SQLException {
        String sql = "SELECT instanceID FROM reservation WHERE reservationID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("instanceID");
            }
        }
        return null;
    }

    public List<String> getReservationsByCustomer(String userID) throws SQLException {
        String sql = """
                    SELECT r.reservationID, f.airlineID, f.flightNum,
                           f.depAirport, f.arrAirport, fi.flightDate, r.totalFare
                    FROM reservation r
                    JOIN flight_instance fi ON r.instanceID = fi.instanceID
                    JOIN flight f ON fi.flightID = f.flightID
                    WHERE r.userID = ?
                    ORDER BY fi.flightDate DESC
                """;
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(String.format(
                            "Res #%d | %s%d | %s→%s | %s | $%.2f",
                            rs.getInt("reservationID"),
                            rs.getString("airlineID"), rs.getInt("flightNum"),
                            rs.getString("depAirport"), rs.getString("arrAirport"),
                            rs.getDate("flightDate"), rs.getDouble("totalFare")));
                }
            }
        }
        return rows;
    }

    public List<String> getReservationsByFlight(String flightID) throws SQLException {
        String sql = """
                    SELECT r.reservationID, u.username, fi.flightDate,
                           r.totalFare, t.class, t.seatNum
                    FROM reservation r
                    JOIN user_account u ON r.userID = u.userID
                    JOIN flight_instance fi ON r.instanceID = fi.instanceID
                    LEFT JOIN ticket t ON r.reservationID = t.reservationID
                    WHERE fi.flightID = ?
                    ORDER BY fi.flightDate DESC
                """;
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, flightID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(String.format(
                            "Res #%d | %s | %s | Class: %s | Seat: %s | $%.2f",
                            rs.getInt("reservationID"), rs.getString("username"),
                            rs.getDate("flightDate"), rs.getString("class"),
                            rs.getString("seatNum"), rs.getDouble("totalFare")));
                }
            }
        }
        return rows;
    }

    public List<String> getMonthlySales() throws SQLException {
        String sql = """
                    SELECT DATE_FORMAT(r.purchaseTime, '%Y-%m') AS month,
                           SUM(r.totalFare) AS revenue,
                           COUNT(*) AS numReservations
                    FROM reservation r
                    GROUP BY month
                    ORDER BY month DESC
                """;
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(String.format("%s | Revenue: $%,.2f | %d reservations",
                        rs.getString("month"), rs.getDouble("revenue"),
                        rs.getInt("numReservations")));
            }
        }
        return rows;
    }

    public record RevenueSummary(double totalRevenue, double totalBookingFees,
            int numReservations, int numTickets) {
    }

    public RevenueSummary getRevenueSummary() throws SQLException {
        String sql = """
                    SELECT COALESCE(SUM(r.totalFare), 0)   AS totalRevenue,
                           COALESCE(SUM(r.bookingFee), 0)  AS totalBookingFees,
                           COUNT(DISTINCT r.reservationID) AS numReservations,
                           COUNT(DISTINCT t.ticketID)      AS numTickets
                    FROM reservation r
                    LEFT JOIN ticket t ON r.reservationID = t.reservationID
                """;
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new RevenueSummary(
                        rs.getDouble("totalRevenue"),
                        rs.getDouble("totalBookingFees"),
                        rs.getInt("numReservations"),
                        rs.getInt("numTickets"));
            }
        }
        return new RevenueSummary(0, 0, 0, 0);
    }

    public String createMultiLegReservation(String userID, List<String> instanceIDs,
            String seatClass) throws SQLException {
        if (instanceIDs == null || instanceIDs.isEmpty())
            throw new SQLException("No flight legs provided.");
        for (String id : instanceIDs) {
            if (!hasAvailableSeats(id))
                return "FULL:" + id;
        }

        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            double fareTotal = 0;
            for (String id : instanceIDs)
                fareTotal += getBaseFare(id);
            double bookingFee = 20.00;
            double total = fareTotal + bookingFee;

            String reservationID;
            String sql = "INSERT INTO reservation (userID, instanceID, totalFare, bookingFee) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, userID);
                ps.setString(2, instanceIDs.get(0));
                ps.setDouble(3, total);
                ps.setDouble(4, bookingFee);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next())
                        throw new SQLException("Reservation ID was not generated.");
                    reservationID = String.valueOf(keys.getInt(1));
                }
            }

            for (String id : instanceIDs) {
                createTicketForReservation(reservationID, id, seatClass, getBaseFare(id));
                decreaseSeat(id);
            }

            con.commit();
            return reservationID;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    public List<String> getRevenueByFlight() throws SQLException {
        String sql = """
                    SELECT f.flightID, f.airlineID, f.flightNum,
                           f.depAirport, f.arrAirport,
                           COUNT(r.reservationID) AS numRes,
                           COALESCE(SUM(r.totalFare), 0) AS revenue
                    FROM flight f
                    JOIN flight_instance fi ON f.flightID = fi.flightID
                    JOIN reservation r ON fi.instanceID = r.instanceID
                    GROUP BY f.flightID, f.airlineID, f.flightNum, f.depAirport, f.arrAirport
                    ORDER BY revenue DESC
                """;
        return revenueRows(sql,
                rs -> String.format("%s%d (%s→%s) | %d res | $%,.2f",
                        rs.getString("airlineID"), rs.getInt("flightNum"),
                        rs.getString("depAirport"), rs.getString("arrAirport"),
                        rs.getInt("numRes"), rs.getDouble("revenue")));
    }

    public List<String> getRevenueByAirline() throws SQLException {
        String sql = """
                    SELECT a.airlineID, a.name,
                           COUNT(r.reservationID) AS numRes,
                           COALESCE(SUM(r.totalFare), 0) AS revenue
                    FROM airline a
                    JOIN flight f ON a.airlineID = f.airlineID
                    JOIN flight_instance fi ON f.flightID = fi.flightID
                    JOIN reservation r ON fi.instanceID = r.instanceID
                    GROUP BY a.airlineID, a.name
                    ORDER BY revenue DESC
                """;
        return revenueRows(sql,
                rs -> String.format("%s (%s) | %d res | $%,.2f",
                        rs.getString("airlineID"), rs.getString("name"),
                        rs.getInt("numRes"), rs.getDouble("revenue")));
    }

    public List<String> getRevenueByCustomer() throws SQLException {
        String sql = """
                    SELECT u.userID, u.username,
                           COUNT(r.reservationID) AS numRes,
                           COALESCE(SUM(r.totalFare), 0) AS revenue
                    FROM user_account u
                    JOIN reservation r ON u.userID = r.userID
                    WHERE u.role = 'customer'
                    GROUP BY u.userID, u.username
                    ORDER BY revenue DESC
                """;
        return revenueRows(sql,
                rs -> String.format("%s (ID %d) | %d res | $%,.2f",
                        rs.getString("username"), rs.getInt("userID"),
                        rs.getInt("numRes"), rs.getDouble("revenue")));
    }

    @FunctionalInterface
    private interface RowFormatter {
        String format(ResultSet rs) throws SQLException;
    }

    private List<String> revenueRows(String sql, RowFormatter fmt) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                rows.add(fmt.format(rs));
        }
        return rows;
    }

    private String generateSeat(String instanceID) throws SQLException {
        String sql = "SELECT seatsAvail FROM flight_instance WHERE instanceID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, instanceID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int remaining = rs.getInt("seatsAvail");
                    int seatNum = 30 - remaining;
                    char letter = (char) ('A' + (seatNum % 6));
                    return (seatNum / 6 + 1) + String.valueOf(letter);
                }
            }
        }
        return "1A";
    }
}
