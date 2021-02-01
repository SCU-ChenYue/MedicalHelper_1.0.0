package com.example.medicalhlepers.drugQuery;

public class Drug {
    public Drug(){

    }
    public Drug(String name,int num,Double price){
        this.name=name;
        this.num=num;
        this.price=price;
    }
    private String name;
    private int num;
    private Double price;

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public Double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
