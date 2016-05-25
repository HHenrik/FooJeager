package com.example.henrik.googlemapsexample.restaurantmenu;


public class MenuObject {

    private String title;

    private String ingredients;

    private String price;

    public MenuObject(String title, String ingredients, String price) {
        this.title = title;
        this.ingredients = ingredients;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
