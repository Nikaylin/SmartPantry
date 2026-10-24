package com.example.smartpantry;

public class PantryItem {
    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiry; // "yyyy-MM-dd" or "" if none

    public PantryItem(int id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry == null ? "" : expiry;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiry() { return expiry; }
}
