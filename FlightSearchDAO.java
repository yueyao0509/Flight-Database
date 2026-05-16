package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightSearchDAO {
    private final Connection con;

    public FlightSearchDAO(Connection con) {
        this.con = con;
    }

    public List<Map<String, Object>> searchFlights(
            String fromAirport,
            String toAirport,
            String departDate,
            boolean flexible,
            String sortBy,
            Double maxPrice,
            Integer maxStops,
            String airline,
            String earliestTakeoff,
            String latestLanding) throws SQLException {

        StringBuilder sql = new StringBuilder(
                """
                        SELECT
                            fi.instanceID,
                            f.flightID,
                            f.flightNum,
                            f.airlineID,
                            f.depAirport,
                            f.arrAirport,
                            TIMESTAMP(fi.flightDate, f.depTIME) AS departureTime,
                            CASE
                                WHEN f.arrTIME < f.depTIME THEN TIMESTAMP(DATE_ADD(fi.flightDate, INTERVAL 1 DAY), f.arrTIME)
                                ELSE TIMESTAMP(fi.flightDate, f.arrTIME)
                            END AS arrivalTime,
                            fi.seatsAvail,
                            fi.baseFare,
                            TIMESTAMPDIFF(
                                MINUTE,
                                TIMESTAMP(fi.flightDate, f.depTIME),
                                CASE
                                    WHEN f.arrTIME < f.depTIME THEN TIMESTAMP(DATE_ADD(fi.flightDate, INTERVAL 1 DAY), f.arrTIME)
                                    ELSE TIMESTAMP(fi.flightDate, f.arrTIME)
                                END
                            ) AS durationMinutes,
                            0 AS stops
                        FROM flight_instance fi
                        JOIN flight f ON fi.flightID = f.flightID
                        WHERE fi.flightDate >= CURDATE()
                        """);

        if (fromAirport != null && !fromAirport.isBlank())
            sql.append(" AND f.depAirport = ?");
        if (toAirport != null && !toAirport.isBlank())
            sql.append(" AND f.arrAirport = ?");
        if (departDate != null && !departDate.isBlank()) {
            if (flexible)
                sql.append(" AND fi.flightDate BETWEEN DATE_SUB(?, INTERVAL 3 DAY) AND DATE_ADD(?, INTERVAL 3 DAY)");
            else
                sql.append(" AND fi.flightDate = ?");
        }
        if (maxPrice != null)
            sql.append(" AND fi.baseFare <= ?");
        if (maxStops != null)
            sql.append(" AND 0 <= ?");
        if (airline != null && !airline.isBlank())
            sql.append(" AND f.airlineID = ?");
        if (earliestTakeoff != null && !earliestTakeoff.isBlank())
            sql.append(" AND f.depTIME >= ?");
        if (latestLanding != null && !latestLanding.isBlank())
            sql.append(" AND f.arrTIME <= ?");

        sql.append(safeOrderBy(sortBy));

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            if (fromAirport != null && !fromAirport.isBlank())
                ps.setString(i++, fromAirport);
            if (toAirport != null && !toAirport.isBlank())
                ps.setString(i++, toAirport);
            if (departDate != null && !departDate.isBlank()) {
                if (flexible) {
                    ps.setString(i++, departDate);
                    ps.setString(i++, departDate);
                } else {
                    ps.setString(i++, departDate);
                }
            }
            if (maxPrice != null)
                ps.setDouble(i++, maxPrice);
            if (maxStops != null)
                ps.setInt(i++, maxStops);
            if (airline != null && !airline.isBlank())
                ps.setString(i++, airline);
            if (earliestTakeoff != null && !earliestTakeoff.isBlank())
                ps.setString(i++, earliestTakeoff);
            if (latestLanding != null && !latestLanding.isBlank())
                ps.setString(i++, latestLanding);

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("instanceID", rs.getInt("instanceID"));
                    row.put("flightID", rs.getString("flightID"));
                    row.put("flightNum", rs.getInt("flightNum"));
                    row.put("airlineID", rs.getString("airlineID"));
                    row.put("depAirport", rs.getString("depAirport"));
                    row.put("arrAirport", rs.getString("arrAirport"));
                    row.put("departureTime", rs.getTimestamp("departureTime"));
                    row.put("arrivalTime", rs.getTimestamp("arrivalTime"));
                    row.put("seatsAvail", rs.getInt("seatsAvail"));
                    row.put("baseFare", rs.getDouble("baseFare"));
                    row.put("durationMinutes", rs.getInt("durationMinutes"));
                    row.put("stops", rs.getInt("stops"));
                    results.add(row);
                }
                return results;
            }
        }
    }

    public List<Map<String, Object>> searchFlights(
            String fromAirport,
            String toAirport,
            String departDate,
            boolean flexible,
            String sortBy,
            Double maxPrice,
            String airline) throws SQLException {
        return searchFlights(fromAirport, toAirport, departDate, flexible, sortBy, maxPrice, null, airline, null, null);
    }

    private String safeOrderBy(String sortBy) {
        if (sortBy == null)
            return " ORDER BY departureTime ASC";
        return switch (sortBy) {
            case "price" -> " ORDER BY fi.baseFare ASC";
            case "takeoff" -> " ORDER BY departureTime ASC";
            case "landing" -> " ORDER BY arrivalTime ASC";
            case "duration" -> " ORDER BY durationMinutes ASC";
            default -> " ORDER BY departureTime ASC";
        };
    }

    public List<String> getFlightsByAirport(String airportID) throws SQLException {
        String sql = """
                    SELECT f.airlineID, f.flightNum, f.depAirport, f.arrAirport,
                           f.depTIME, f.arrTIME, f.dayofweek
                    FROM flight f
                    WHERE f.depAirport = ? OR f.arrAirport = ?
                    ORDER BY f.depAirport, f.depTIME
                """;
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, airportID);
            ps.setString(2, airportID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(String.format("%s%d | %s→%s | %s @ %s (arrive %s)",
                            rs.getString("airlineID"), rs.getInt("flightNum"),
                            rs.getString("depAirport"), rs.getString("arrAirport"),
                            rs.getString("dayofweek"),
                            rs.getString("depTIME"), rs.getString("arrTIME")));
                }
            }
        }
        return rows;
    }

    public List<String> getMostActiveFlights() throws SQLException {
        String sql = """
                    SELECT f.airlineID, f.flightNum, f.depAirport, f.arrAirport,
                           COUNT(r.reservationID) AS numReservations
                    FROM flight f
                    JOIN flight_instance fi ON f.flightID = fi.flightID
                    JOIN reservation r ON fi.instanceID = r.instanceID
                    GROUP BY f.flightID, f.airlineID, f.flightNum, f.depAirport, f.arrAirport
                    ORDER BY numReservations DESC
                    LIMIT 20
                """;
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(String.format("%s%d (%s→%s) | %d reservations",
                        rs.getString("airlineID"), rs.getInt("flightNum"),
                        rs.getString("depAirport"), rs.getString("arrAirport"),
                        rs.getInt("numReservations")));
            }
        }
        return rows;
    }

    public record RoundTripResult(
            List<Map<String, Object>> outbound,
            List<Map<String, Object>> returning) {
    }

    public RoundTripResult searchRoundTrip(
            String fromAirport, String toAirport,
            String departDate, String returnDate,
            boolean flexible, String sortBy,
            Double maxPrice, String airline) throws SQLException {

        List<Map<String, Object>> out = searchFlights(
                fromAirport, toAirport, departDate, flexible, sortBy, maxPrice, airline);
        List<Map<String, Object>> back = searchFlights(
                toAirport, fromAirport, returnDate, flexible, sortBy, maxPrice, airline);
        return new RoundTripResult(out, back);
    }
}