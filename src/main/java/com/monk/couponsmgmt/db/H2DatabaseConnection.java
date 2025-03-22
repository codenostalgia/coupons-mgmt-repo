package com.monk.couponsmgmt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseConnection {

    public static Connection getConnection(String databaseURL, String username, String password) throws SQLException {
        return DriverManager.getConnection(databaseURL, username, password);
    }
}
