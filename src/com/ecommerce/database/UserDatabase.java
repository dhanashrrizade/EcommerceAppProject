package com.ecommerce.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ecommerce.models.User;

public class UserDatabase {
    private List<User> users;

    public UserDatabase() {
        this.users = new ArrayList<>();
    }

    public UserDatabase(List<User> users) {
        this.users = new ArrayList<>(users);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
    }

public void addUser(User user) {
        if (!users.contains(user)) {
            insertUserIntoDatabase(user);
            this.users.add(user);
            System.out.println("User added to the database.");
        }
    }


    private void insertUserIntoDatabase(User user) {
        // SQL query to insert user into the Users table
        String query = "INSERT INTO Users (FirstName, LastName, Username, Email, Password, City, MobileNumber) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getCity());
            statement.setString(7, user.getMobileNumber());

            // Execute the insert query
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add user to the database. Error: " + e.getMessage());
        }
    }

    public void initializeSampleData() {
        // Provide initial user data without interacting with the console
        User sampleUser = new User("John", "Doe", "john_doe", "john@example.com", "pass123", "City1", "1234567890");
        addUser(sampleUser);
    }
}


