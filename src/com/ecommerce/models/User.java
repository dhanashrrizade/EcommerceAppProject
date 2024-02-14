package com.ecommerce.models;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String city;
    private String mobileNumber;

    // Constructor
    public User(String firstName, String lastName, String username, String email, String password, String city, String mobileNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.city = city;
        this.mobileNumber = mobileNumber;
    }
    private int userId;  // Assume your User class has a field for the user's ID

    // ... other existing fields and methods ...

    public int getUserId() {
        return userId;
    }
    // Getters

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getMobileNumber() {
        return mobileNumber;
        
    }

    // Other methods or setters as needed
}

