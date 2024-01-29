package com.example.finalyear;

import java.util.List;

public class SkincareProduct {
    private List<String> __parsed_extra;
    private String brand;
    private String ingredients;
    private String product_name;

    public List<String> get__parsed_extra() {
        return __parsed_extra;
    }

    public void set__parsed_extra(List<String> __parsed_extra) {
        this.__parsed_extra = __parsed_extra;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    @Override
    public String toString() {
        return "\n" + __parsed_extra;
    }
}

