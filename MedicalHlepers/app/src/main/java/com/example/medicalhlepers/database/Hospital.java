package com.example.medicalhlepers.database;

public class Hospital {
    private String name;
    private String hosPhoto1;
    private String hosWeb;
    private String hosLevel;
    private String hosType;
    private String hosAddre;
    private String hosPhone;
    private String hosPhoto2;

    public Hospital() {}
    public Hospital(String name, String hosPhoto1, String hosWeb, String hosLevel, String hosType,
                    String hosAddre, String hosPhone, String hosPhoto2) {
        this.name = name;
        this.hosPhoto1 = hosPhoto1;
        this.hosWeb = hosWeb;
        this.hosLevel = hosLevel;
        this.hosType = hosType;
        this.hosAddre = hosAddre;
        this.hosPhone = hosPhone;
        this.hosPhoto2 = hosPhoto2;
    }

    public String getName() {
        return name;
    }
    public String getImageId() {
        return hosPhoto1;
    }
    public String getHosWeb() {
        return hosWeb;
    }
    public String getHosLevel() {
        return hosLevel;
    }
    public String getHosType() {
        return hosType;
    }
    public String getHosPhone() {
        return hosPhone;
    }
    public String getHosAddre() {
        return hosAddre;
    }
    public String getHosPhoto() {
        return hosPhoto2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHosPhoto1(String hosPhoto1) {
        this.hosPhoto1 = hosPhoto1;
    }

    public void setHosWeb(String hosWeb) {
        this.hosWeb = hosWeb;
    }

    public void setHosLevel(String hosLevel) {
        this.hosLevel = hosLevel;
    }

    public void setHosType(String hosType) {
        this.hosType = hosType;
    }

    public void setHosPhone(String hosPhone) {
        this.hosPhone = hosPhone;
    }

    public void setHosAddre(String hosAddre) {
        this.hosAddre = hosAddre;
    }

    public void setHosPhoto2(String hosPhoto2) {
        this.hosPhoto2 = hosPhoto2;
    }
}
