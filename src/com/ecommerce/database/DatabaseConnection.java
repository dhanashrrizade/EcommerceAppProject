package com.ecommerce.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading MySQL JDBC driver");
        }
    }

    public static void setDatabaseCredentials(String url, String user, String password) {
        URL = "jdbc:mysql://localhost:3306/e_commerce?useSSL=false";
        USER = "root";
        PASSWORD = "root";
    }

    

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new RuntimeException("Database credentials not set. Please set credentials using setDatabaseCredentials method.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Optional: You can provide a method to initialize credentials interactively
    public static void initializeDatabaseCredentials() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter database URL:");
        String dbUrl = scanner.nextLine();

        System.out.println("Enter database username:");
        String dbUser = scanner.nextLine();

        System.out.println("Enter database password:");
        String dbPassword = scanner.nextLine();

        setDatabaseCredentials(dbUrl, dbUser, dbPassword);
    }
    
}

