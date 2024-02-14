package com.ecommerce.models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Product> items;
    private double totalBill;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.totalBill = 0.0;
    }

    public List<Product> getItems() {
        return items;
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity should be greater than zero.");
            return;
        }

        if (quantity > product.getQuantityAvailable()) {
            System.out.println("Not enough quantity available.");
            return;
        }

        // Check if the product is already in the cart
        for (Product cartProduct : items) {
            if (cartProduct.getProductId() == product.getProductId()) {
                // Update quantity and total bill for existing product in the cart
                int newQuantity = cartProduct.getQuantityAvailable() + quantity;
                if (newQuantity > product.getQuantityAvailable()) {
                    System.out.println("Not enough quantity available.");
                    return;
                }

                cartProduct.setQuantityAvailable(newQuantity);
                totalBill += product.getPrice() * quantity;
                product.setQuantityAvailable(product.getQuantityAvailable() - quantity);

                // Remove the product from the cart if the quantity becomes zero
                if (cartProduct.getQuantityAvailable() == 0) {
                    items.remove(cartProduct);
                }

                return;
            }
        }

        // If the product is not in the cart, add it as a new item
        items.add(product);
        totalBill += product.getPrice() * quantity;
        product.setQuantityAvailable(product.getQuantityAvailable() - quantity);
    }

    public double getTotalBill() {
        return totalBill;
    }
}

