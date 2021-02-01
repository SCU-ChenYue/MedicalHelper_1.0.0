package com.example.medicalhlepers.Doctor;

import org.litepal.crud.DataSupport;

public class Doctor extends DataSupport {
    private String hospital;    //医院
    private String department;  //科室
    private String name;    //名字
    private String title;   //职称
    private String introduction;    //介绍
    private String status;  //在线状态
    private String imageUrl;    //头像

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public void setStatus(String status) { this.status = status; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getHospital() { return hospital; }
    public String getDepartment() { return department; }
    public String getName() {
        return name;
    }
    public String getTitle() {
        return title;
    }
    public String getIntroduction() {
        return introduction;
    }
    public String getStatus() { return status; }
    public String getImageUrl() {
        return imageUrl;
    }
}
