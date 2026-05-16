package org.group40.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/project-db";
    private static final String USER = "root";
    private static final String PASSWORD = "password123";

    private static Connection con;

    public static Connection get() throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return con;
    }

    public static void close() throws SQLException {
        if (con != null && !con.isClosed())
            con.close();
    }
}