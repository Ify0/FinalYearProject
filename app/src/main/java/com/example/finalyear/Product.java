package com.example.finalyear;

public class Product {
    private String concern;
    private String type;
    private String name;
    private double price;

    // Constructors
    public Product() {
        // Default constructor required for Firebase
    }

    public Product(String concern, String type, String name, double price) {
        this.concern = concern;
        this.type = type;
        this.name = name;
        this.price = price;
    }

    // Add getters and setters
    public String getConcern() {
        return concern;
    }

    public void setConcern(String concern) {
        this.concern = concern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

