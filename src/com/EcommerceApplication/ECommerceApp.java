package com.EcommerceApplication;

import java.util.Scanner;
import com.ecommerce.database.UserDatabase;
import com.ecommerce.database.ProductDatabase;
import com.ecommerce.controllers.AdminController;
import com.ecommerce.models.ShoppingCart;
import com.ecommerce.database.DatabaseConnection;
import java.util.InputMismatchException;
import com.ecommerce.models.User;

public class ECommerceApp {
    public static void main(String[] args) {
        // Set database credentials before creating connections
        DatabaseConnection.setDatabaseCredentials("jdbc:mysql://localhost:3306/e_commerce", "root", "root");

        // Instantiate databases and controllers
        UserDatabase userDatabase = new UserDatabase();
        ProductDatabase productDatabase = new ProductDatabase();
        AdminController adminController = new AdminController(productDatabase, userDatabase);

        Scanner scanner = new Scanner(System.in);
        ShoppingCart shoppingCart = new ShoppingCart(); // Create a shopping cart outside the loop

        while (true) {
            try {
            	 System.out.println("Enter your choice:");
                 System.out.println("1. User Registration");
                 System.out.println("2. User Login");
                 System.out.println("3. Product Details");
                 System.out.println("4. Shopping Cart");
                 System.out.println("5. View Cart");
                 System.out.println("6. Calculate Bill");
                 System.out.println("7. View Registered Users");
                 System.out.println("8. Buy Product"); // Added option to buy product
                 System.out.println("9. Exit");

                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline after reading the choice

                    switch (choice) {
                        case 1:
                            System.out.println("Enter the first name:");
                            String firstName = scanner.nextLine();

                            System.out.println("Enter the last name:");
                            String lastName = scanner.nextLine();

                            System.out.println("Enter the username:");
                            String username = scanner.nextLine();

                            System.out.println("Enter the email:");
                            String email = scanner.nextLine();

                            System.out.println("Enter the password:");
                            String password = scanner.nextLine();

                            System.out.println("Enter the city:");
                            String city = scanner.nextLine();

                            System.out.println("Enter the mobile number:");
                            String mobileNumber = scanner.nextLine();

                            // Create and add the user to the database
                            User user = new User(firstName, lastName, username, email, password, city, mobileNumber);
                            userDatabase.addUser(user);

                            System.out.println("User added to the database.");
                            break;
                        case 2:
                            if (adminController.loginUser(scanner)) {
                                System.out.println("Login successful!");
                            } else {
                                System.out.println("Login failed. Invalid credentials.");
                            }
                            break;
                        case 3:
                            adminController.handleProductDetails(scanner);
                            break;
                        case 4:
                            adminController.handleShoppingCart(scanner, shoppingCart, choice);
                            break;
                        case 5:
                            adminController.viewCart(shoppingCart);
                            break;
                        case 6:
                            double bill = adminController.calculateBill(shoppingCart);
                            System.out.println("Total Bill: $" + bill);
                            break;

                        case 7:
                            adminController.viewRegisteredUsers();
                            break;
                        case 8:
                            System.out.println("Exiting the application. Goodbye!");
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a valid option.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid option.");
                    scanner.nextLine(); // Consume the invalid input to prevent an infinite loop
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.nextLine(); // Consume the invalid input to prevent an infinite loop
            }
        }
    }
}
