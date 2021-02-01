package com.example.medicalhlepers.drugQuery;

public class Drug_item {
    private String name;
    private String des;
    private String imageUrl;
    private double price;
    private String type;
    private String specs;

    public Drug_item(){}

    public Drug_item(int id, String name, String des, String imageUrl, double price, String type, String specs, String usage, String component) {
        this.name = name;
        this.des = des;
        this.imageUrl = imageUrl;
        this.price = price;
        this.type = type;
        this.specs = specs;
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getSpecs() {
        return specs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }
}
