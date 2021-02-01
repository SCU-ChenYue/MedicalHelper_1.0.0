package com.example.medicalhlepers.AIdiagnose;

import java.io.Serializable;

public class Hospital_2 implements Serializable {
    private String name;    //医院名称
    private String grade;   //医院等级
    private String category;    //中医/西医
    private double longitude;
    private double latitude;
    private String address;
    private String phone;
    private String departmentCategory;  //中医科/西医科
    private String departmentName;  //科室名字
    private String distance;    //距离
    private double lon; //我的
    private double lat;

    public Hospital_2() {}
    public  Hospital_2(String name, String grade, String category, double longitude,
                       double latitude, String phone, String departmentCategory, String departmentName) {
        this.name = name;
        this.grade = grade;
        this.category = category;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
        this.departmentCategory = departmentCategory;
        this.departmentName = departmentName;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartmentCategory() {
        return departmentCategory;
    }

    public void setDepartmentCategory(String departmentCategory) {
        this.departmentCategory = departmentCategory;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

}
