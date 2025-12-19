package com.smartLib.managementSystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static final String DB_HOST =
            System.getenv().getOrDefault("DB_HOST", "localhost");

    private static final String DB_PORT =
            System.getenv().getOrDefault("DB_PORT", "5432");

    private static final String DB_NAME =
            System.getenv().getOrDefault("DB_NAME", "smartlib");

    private static final String DB_USER =
            System.getenv().getOrDefault("DB_USER", "java");

    private static final String DB_PASS =
            System.getenv().getOrDefault("DB_PASS", "123");

    private static final String DB_URL =
            "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Database connection failed to " + DB_URL, e
            );
        }
    }
}
