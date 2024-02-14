package com.ecommerce.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ecommerce.database.DatabaseConnection;
import com.ecommerce.database.ProductDatabase;
import com.ecommerce.database.UserDatabase;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.models.ShoppingCart;
import java.sql.Statement;

public class AdminController {
    private ProductDatabase productDatabase;
    private UserDatabase userDatabase;
    private List<User> registeredUsers;
    private ShoppingCart shoppingCart;
    public AdminController(ProductDatabase productDatabase, UserDatabase userDatabase) {
        this.productDatabase = productDatabase;
        this.userDatabase = userDatabase;
        this.registeredUsers = userDatabase.getUsers();
        this.shoppingCart = new ShoppingCart();  // Initialize the shopping cart
    }


    public void handleUserRegistration(Scanner scanner) {
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

        // Create a new User object and add it to the registered users list
        User newUser = new User(firstName, lastName, username, email, password);
        registeredUsers.add(newUser);

        System.out.println("User registration handled successfully!");
    }
    private User authenticateUser(String username, String password) {
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE Username = ? AND Password = ?")) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // User found, create and return the User object
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");

                return new User(firstName, lastName, username, email, password, email, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exceptions appropriately
        }

        return null;
    }

    public void handleProductDetails(Scanner scanner) {
        System.out.println("Enter the product ID:");
        int productId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the product name:");
        String productName = scanner.nextLine();

        System.out.println("Enter the product description:");
        String productDescription = scanner.nextLine();

        System.out.println("Enter the available quantity:");
        int availableQuantity = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the price:");
        double price = scanner.nextDouble();
        scanner.nextLine();

        // Insert product details into the database
        insertProductDetails(productId, productName, productDescription, availableQuantity, price);

        System.out.println("Product details handled successfully!");
    }

    private void insertProductDetails(int productId, String productName, String productDescription, int availableQuantity, double price) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Products (ProductId, ProductName, Description, AvailableQuantity, Price) VALUES (?, ?, ?, ?, ?)")) {

            statement.setInt(1, productId);
            statement.setString(2, productName);
            statement.setString(3, productDescription);
            statement.setInt(4, availableQuantity);
            statement.setDouble(5, price);

            // Execute the insert query
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();  // Handle exceptions appropriately
            System.out.println("Failed to insert product details. Error: " + e.getMessage());
        }
    }


    public void handleShoppingCart(Scanner scanner, ShoppingCart shoppingCart, int userId) {
        System.out.println("Enter the product id to buy product:");
        int productId = scanner.nextInt();
        scanner.nextLine();

        // Retrieve the product details from the database
        Product product = getProductById(productId);

        // Check if the product exists
        if (product != null) {
            System.out.println("Product found:");
            System.out.println("Product ID: " + product.getProductId());
            System.out.println("Product Name: " + product.getProductName());
            System.out.println("Available Quantity: " + product.getQuantityAvailable());
            System.out.println("Price: $" + product.getPrice());

            System.out.println("Enter the quantity to add to the cart:");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            // Validate the quantity
            if (quantity <= 0) {
                System.out.println("Quantity should be greater than zero.");
                return;
            }

            // Check if there is enough quantity available
            if (quantity > product.getQuantityAvailable()) {
                System.out.println("Not enough quantity available.");
                return;
            }

            // Add the item to the shopping cart
            shoppingCart.addItem(product, quantity);

            // Update the shopping cart data in the database
            updateShoppingCartInDatabase(userId, productId, quantity);

            System.out.println("Product added to the cart!");
        } else {
            System.out.println("Product not found.");
        }
    }
    public void processOrder(Scanner scanner, int userId) {
        // Assuming handleShoppingCart adds items to the shopping cart
        handleShoppingCart(scanner, shoppingCart, userId);

        // Display the items in the shopping cart
        viewCart(shoppingCart);

        // Calculate the total bill
        double totalBill = calculateBill(shoppingCart);
        System.out.println("Total Bill: $" + totalBill);

        // Prompt the user to confirm the purchase
        System.out.println("Do you want to proceed with the purchase? (yes/no)");
        String choice = scanner.nextLine().toLowerCase();

        if ("yes".equals(choice)) {
            // Process the purchase
            if (completePurchase(userId, shoppingCart)) {
                System.out.println("Purchase successful!");
            } else {
                System.out.println("Failed to process the purchase. Please try again.");
            }
        } else {
            System.out.println("Purchase canceled.");
        }

        // Clear the shopping cart after the purchase
        shoppingCart.getItems().clear();
    }

    private boolean completePurchase(int userId, ShoppingCart shoppingCart) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Disable auto-commit to manage transactions
            connection.setAutoCommit(false);

            try {
                // Iterate through the items in the shopping cart
                for (Product product : shoppingCart.getItems()) {
                    int productId = product.getProductId();
                    int quantityPurchased = product.getQuantityAvailable();

                    // Update the product quantity in the database
                    if (updateProductQuantityInDatabase(productId, quantityPurchased)) {
                        // Insert the order details into the database
                        insertOrderDetails(userId, productId, quantityPurchased);
                    } else {
                        // Rollback the transaction if an error occurs
                        connection.rollback();
                        return false;
                    }
                }

                // Commit the transaction if everything is successful
                connection.commit();
                return true;
            } catch (SQLException e) {
                // Rollback the transaction in case of an exception
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                // Enable auto-commit after the transaction is complete
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateProductQuantityInDatabase(int productId, int quantityPurchased) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE Products SET AvailableQuantity = AvailableQuantity - ? WHERE ProductId = ? AND AvailableQuantity >= ?")) {

            statement.setInt(1, quantityPurchased);
            statement.setInt(2, productId);
            statement.setInt(3, quantityPurchased);

            // Execute the update query
            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void insertOrderDetails(int userId, int productId, int quantityPurchased) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Orders (userId, productId, quantity) VALUES (?, ?, ?)")) {

            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantityPurchased);

            // Execute the insert query
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void viewCart(ShoppingCart shoppingCart) {
 {
        List<Product> cartItems = shoppingCart.getItems();

        if (cartItems.isEmpty()) {
            System.out.println("The shopping cart is empty.");
        } else {
            System.out.println("Shopping Cart:");
            for (Product item : cartItems) {
                System.out.println("Product ID: " + item.getProductId());
                System.out.println("Product Name: " + item.getProductName());
                System.out.println("Quantity: " + item.getQuantityAvailable());
                System.out.println("Price: $" + item.getPrice());
                System.out.println("-------------------------");
            }
        }}}

    public double calculateBill(ShoppingCart shoppingCart) {
        List<Product> products = shoppingCart.getItems();
        double totalBill = 0;

        for (Product product : products) {
            totalBill += product.getPrice() * product.getQuantityAvailable();
        }

        return totalBill;
    }


    public void viewRegisteredUsers() {
        // Assuming you have a method to get the list of registered users
        List<User> registeredUsers = getRegisteredUsers();

        // Check if the list is not empty
        if (!registeredUsers.isEmpty()) {
            System.out.println("List of Registered Users:");

            // Iterate through the list and print user details
            for (User user : registeredUsers) {
               
                System.out.println("First Name: " + user.getFirstName());
                System.out.println("Last Name: " + user.getLastName());
                System.out.println("Username: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Password: " + user.getPassword());
                System.out.println("-------------------------");
            }
        } else {
            System.out.println("No registered users found.");
        }
    }


    private List<Product> getProductsFromDatabase() {
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT ProductId, ProductName, Description, AvailableQuantity, Price FROM Products")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("ProductId");
                String productName = resultSet.getString("ProductName");
                String productDescription = resultSet.getString("Description");
                int quantityAvailable = resultSet.getInt("AvailableQuantity");
                double price = resultSet.getDouble("Price");

                // Create Product object and add to the list
                Product product = new Product(productId, productName, productDescription, quantityAvailable, price);
                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle exceptions appropriately
        }

        return productList;
    }

    private Product getProductById(int productId) {
        List<Product> productList = getProductsFromDatabase();

        // Debugging statement to check the product list
        System.out.println("Product List: " + productList);

        for (Product product : productList) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }

    public boolean loginUser(Scanner scanner) {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        // Authenticate user
        User authenticatedUser = authenticateUser(username, password);

        if (authenticatedUser != null) {
            System.out.println("Login successful! Welcome, " + authenticatedUser.getFirstName());
            return true;
        } else {
            System.out.println("Invalid username or password. Login failed.");
            return false;
        } if (authenticatedUser != null) {
            loggedInUserId = authenticatedUser.getUserId(); // Set the logged-in user's ID
            System.out.println("Login successful! Welcome, " + authenticatedUser.getFirstName());
            return true;
        } else {
            System.out.println("Invalid username or password. Login failed.");
            return false;
        }
    }
     public List<User> getRegisteredUsers() {
        return registeredUsers;
    }
    private void updateShoppingCartInDatabase(int userId, int productId, int quantity) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "REPLACE INTO shoppingcart (cartId, userId, productId, quantity) VALUES (NULL, ?, ?, ?)")) {

            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            // Execute the replace query
            statement.executeUpdate();

            System.out.println("Shopping cart updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update shopping cart. Error: " + e.getMessage());
        }
    }private void insertOrder(int userId, int productId, int quantity) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Orders (UserID, ProductID, Quantity, OrderDate, TotalAmount) VALUES (?, ?, ?, ?, ?)")) {

               statement.setInt(1, userId);
               statement.setInt(2, productId);
               statement.setInt(3, quantity);

               // Assuming OrderDate is the current date
               java.sql.Date orderDate = new java.sql.Date(System.currentTimeMillis());
               statement.setDate(4, orderDate);

               // Assuming TotalAmount is the product price multiplied by quantity
               double totalAmount = getProductById(productId).getPrice() * quantity;
               statement.setDouble(5, totalAmount);

               // Execute the insert query
               statement.executeUpdate();

               System.out.println("Order placed successfully!");

           } catch (SQLException e) {
               e.printStackTrace();  // Handle exceptions appropriately
               System.out.println("Failed to place order. Error: " + e.getMessage());
           }
       }private int insertOrder(int userId, double totalAmount) {
           try (Connection connection = DatabaseConnection.getConnection();
                   PreparedStatement statement = connection.prepareStatement(
                           "INSERT INTO Orders (UserID, TotalAmount) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                  statement.setInt(1, userId);
                  statement.setDouble(2, totalAmount);

                  int affectedRows = statement.executeUpdate();

                  if (affectedRows == 0) {
                      throw new SQLException("Creating order failed, no rows affected.");
                  }

                  try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                      if (generatedKeys.next()) {
                          return generatedKeys.getInt(1);
                      } else {
                          throw new SQLException("Creating order failed, no ID obtained.");
                      }
                  }
              } catch (SQLException e) {
                  e.printStackTrace();  // Handle exceptions appropriately
                  return -1; // Return a default or error value
              }
          }

          private void insertOrderDetails(int orderId, List<Product> products) {
              try (Connection connection = DatabaseConnection.getConnection();
                   PreparedStatement statement = connection.prepareStatement(
                           "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, Subtotal) VALUES (?, ?, ?, ?)")) {

                  for (Product product : products) {
                      statement.setInt(1, orderId);
                      statement.setInt(2, product.getProductId());
                      statement.setInt(3, product.getQuantityAvailable());
                      statement.setDouble(4, product.getPrice() * product.getQuantityAvailable());
                      statement.addBatch();
                  }

                  statement.executeBatch();

              } catch (SQLException e) {
                  e.printStackTrace();  // Handle exceptions appropriately
              }
          }



	
    
}
