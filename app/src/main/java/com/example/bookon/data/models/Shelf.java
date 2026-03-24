package com.example.bookon.data.models;

public class Shelf {

    private String name;
    private String description;

    public Shelf(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}