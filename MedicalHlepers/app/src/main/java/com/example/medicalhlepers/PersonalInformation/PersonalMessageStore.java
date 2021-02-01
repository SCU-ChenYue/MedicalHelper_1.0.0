package com.example.medicalhlepers.PersonalInformation;

import org.litepal.crud.DataSupport;

public class PersonalMessageStore extends DataSupport {
    private int id; //头像图片id
    private String userName;
    private String userSex;
    private String userbirthDay;
    private String idType;
    private String idNumber;
    private String phoneNumber;
    private String password;

    public int getId() {
        return id;
    }
    public String getUserName() { return userName; }
    public String getUserSex() { return userSex; }
    public String getUserbirthDay() { return userbirthDay; }
    public String getIdType() { return idType; }
    public String getIdNumber() { return idNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public void setUserbirthDay(String userbirthDay) {
        this.userbirthDay = userbirthDay;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) { this.password = password; }
}
