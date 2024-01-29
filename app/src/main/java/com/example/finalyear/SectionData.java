package com.example.finalyear;

import java.util.List;

public class SectionData {
    private String title;
    private List<Product> products;

    // Constructors
    public SectionData() {
        // Default constructor required for Firebase
    }

    public SectionData(String title, List<Product> products) {
        this.title = title;
        this.products = products;
    }

    // Add getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
