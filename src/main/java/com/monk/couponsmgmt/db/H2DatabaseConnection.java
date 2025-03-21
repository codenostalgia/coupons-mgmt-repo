package com.monk.couponsmgmt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
    }
}
