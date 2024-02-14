// File: com.ecommerce.database.ProductDatabase
package com.ecommerce.database;

import com.ecommerce.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabase {
    private List<Product> products;

    public ProductDatabase() {
        this.products = new ArrayList<>();
        // Initialize with some sample data or leave it empty
        initializeSampleData();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Other database-related methods can be added here, like fetching user data, etc.

    private void initializeSampleData() {
        // Check if products are already added
        if (products.isEmpty()) {
            // Add some sample products
            addProduct(new Product(101, "Apple MacBook 2020", "8 GB RAM, 256 SSD", 5, 85000));
            addProduct(new Product(102, "One Plus Mobile", "16 GB RAM, 128 GB Storage", 3, 37500));
            addProduct(new Product(103, "Samsung Galaxy Watch", "Smartwatch with Fitness Tracking", 2, 15000));
            addProduct(new Product(104, "Sony Noise Cancelling Headphones", "Over Ear Wireless Bluetooth Headphones", 8, 3000));
            // Add more products as needed
        }
    }
}

