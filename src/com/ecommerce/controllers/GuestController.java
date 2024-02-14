package com.ecommerce.controllers;

import java.util.List;

import com.ecommerce.database.ProductDatabase;
import com.ecommerce.models.Product;

public class GuestController {
    private ProductDatabase productDatabase;

    public GuestController(ProductDatabase productDatabase) {
        this.productDatabase = productDatabase;
    }

    // Method to display the product list to the guest
 // Add this method to your AdminController class
    public void displayProductList() {
        List<Product> productList = productDatabase.getAllProducts();
        if (productList.isEmpty()) {
            System.out.println("No products available.");
        } else {
            System.out.println("Product List:");
            for (Product product : productList) {
                displaySingleProductInfo(product);
                System.out.println("--------------");
            }
        }
    }

    // Helper method to display information for a single product
    private void displaySingleProductInfo(Product product) {
        System.out.println("Product ID: " + product.getProductId());
        System.out.println("Product Name: " + product.getProductName());
        System.out.println("Description: " + product. getDescription());
        System.out.println("Available Quantity: " + product.getQuantityAvailable());
        System.out.println("Price: $" + product.getPrice());
    }
}