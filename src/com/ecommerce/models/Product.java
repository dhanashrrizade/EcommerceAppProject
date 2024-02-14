package com.ecommerce.models;

public class Product {
    private int productId;
    private String productName;
    private String description;
    private int quantityAvailable;
    private double price;

    public Product(int productId, String productName, String description, int quantityAvailable, double price) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.quantityAvailable = quantityAvailable;
        this.price = price;
    }
    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;}

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public double getPrice() {
        return price;
    }
    public void displayProductInfo() {
        System.out.println("Product ID: " + productId);
        System.out.println("Product Name: " + productName);
        System.out.println("Product Description: " + description);
        System.out.println("Quantity Available: " + quantityAvailable);
        System.out.println("Price: $" + price);
    }

    // Other methods...
}

