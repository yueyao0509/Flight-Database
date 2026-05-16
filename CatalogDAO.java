package org.group40.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogDAO {
    private final Connection con;

    public CatalogDAO(Connection con) {
        this.con = con;
    }

    public List<String> listAirports() throws SQLException {
        return queryList("SELECT airportID, name, city, country FROM airport ORDER BY airportID",
                rs -> String.format("%s | %s | %s, %s",
                        rs.getString("airportID"), rs.getString("name"),
                        rs.getString("city"), rs.getString("country")));
    }

    public void addAirport(String id, String name, String city, String country) throws SQLException {
        exec("INSERT INTO airport (airportID, name, city, country) VALUES (?, ?, ?, ?)",
                id, name, city, country);
    }

    public void updateAirport(String id, String name, String city, String country) throws SQLException {
        exec("UPDATE airport SET name = ?, city = ?, country = ? WHERE airportID = ?",
                name, city, country, id);
    }

    public void deleteAirport(String id) throws SQLException {
        exec("DELETE FROM airport WHERE airportID = ?", id);
    }

    public List<String> listAircraft() throws SQLException {
        return queryList("""
                SELECT aircraftID, airlineID, model, capacity, range_km
                FROM aircraft ORDER BY aircraftID
                """,
                rs -> String.format("%s | %s | %s | cap %d | range %d km",
                        rs.getString("aircraftID"), rs.getString("airlineID"),
                        rs.getString("model"), rs.getInt("capacity"), rs.getInt("range_km")));
    }

    public void addAircraft(String id, String airlineID, String model,
            int capacity, int rangeKm) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO aircraft (aircraftID, airlineID, model, capacity, range_km) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, airlineID);
            ps.setString(3, model);
            ps.setInt(4, capacity);
            ps.setInt(5, rangeKm);
            ps.executeUpdate();
        }
    }

    public void deleteAircraft(String id) throws SQLException {
        exec("DELETE FROM aircraft WHERE aircraftID = ?", id);
    }

    public List<String> listFlights() throws SQLException {
        return queryList("""
                SELECT flightID, airlineID, aircraftID, flightNum,
                       depAirport, arrAirport, depTIME, arrTIME, dom_or_int, dayofweek
                FROM flight ORDER BY flightID
                """,
                rs -> String.format("%s | %s%d | %s→%s | %s @ %s→%s | %s",
                        rs.getString("flightID"),
                        rs.getString("airlineID"), rs.getInt("flightNum"),
                        rs.getString("depAirport"), rs.getString("arrAirport"),
                        rs.getString("dayofweek"),
                        rs.getString("depTIME"), rs.getString("arrTIME"),
                        "D".equals(rs.getString("dom_or_int")) ? "Domestic" : "International"));
    }

    public void addFlight(String id, String airlineID, String aircraftID, int flightNum,
            String depAirport, String arrAirport,
            String depTime, String arrTime,
            String domOrInt, String dayOfWeek) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("""
                INSERT INTO flight (flightID, airlineID, aircraftID, flightNum,
                                    depAirport, arrAirport, depTIME, arrTIME, dom_or_int, dayofweek)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
            ps.setString(1, id);
            ps.setString(2, airlineID);
            ps.setString(3, aircraftID);
            ps.setInt(4, flightNum);
            ps.setString(5, depAirport);
            ps.setString(6, arrAirport);
            ps.setString(7, depTime);
            ps.setString(8, arrTime);
            ps.setString(9, domOrInt);
            ps.setString(10, dayOfWeek);
            ps.executeUpdate();
        }
    }

    public void deleteFlight(String id) throws SQLException {
        exec("DELETE FROM flight WHERE flightID = ?", id);
    }

    @FunctionalInterface
    private interface RowFmt {
        String format(ResultSet rs) throws SQLException;
    }

    private List<String> queryList(String sql, RowFmt fmt) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                rows.add(fmt.format(rs));
        }
        return rows;
    }

    private void exec(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++)
                ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
        }
    }

    public List<String> listAirportIDs() throws SQLException {
        List<String> ids = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT airportID, city FROM airport ORDER BY airportID");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("airportID") + " — " + rs.getString("city"));
            }
        }
        return ids;
    }

    public List<String> listFlightIDs() throws SQLException {
        List<String> ids = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("""
                SELECT f.flightID, f.airlineID, f.flightNum, f.depAirport, f.arrAirport
                FROM flight f ORDER BY f.flightID
                """);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(String.format("%s (%s%d %s→%s)",
                        rs.getString("flightID"), rs.getString("airlineID"),
                        rs.getInt("flightNum"), rs.getString("depAirport"),
                        rs.getString("arrAirport")));
            }
        }
        return ids;
    }

    public List<String> listAirlineIDs() throws SQLException {
    List<String> ids = new ArrayList<>();
    try (PreparedStatement ps = con.prepareStatement(
            "SELECT airlineID, name FROM airline ORDER BY airlineID");
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            ids.add(rs.getString("airlineID") + " — " + rs.getString("name"));
        }
    }
    return ids;
}

public List<String> listAircraftIDs() throws SQLException {
    List<String> ids = new ArrayList<>();
    try (PreparedStatement ps = con.prepareStatement(
            "SELECT aircraftID, model FROM aircraft ORDER BY aircraftID");
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            ids.add(rs.getString("aircraftID") + " — " + rs.getString("model"));
        }
    }
    return ids;
}
}