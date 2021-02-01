package com.example.medicalhlepers.Yuyue;

public class Appointment {
    private String date;    //日期
    private String flag;    //星期几
    private String hospital;    //医院
    private String apartment;   //部门
    private String shangwu;

    public Appointment(String date, String flag, String hospital,
                       String apartment, String shangwu) {
        this.date = date;
        this.flag = flag;
        this.hospital = hospital;
        this.apartment = apartment;
        this.shangwu = shangwu;
    }
    public String getDate() { return date; }
    public String getFlag() { return flag; }
    public String getHospital() { return hospital; }
    public String getApartment() { return apartment; }
    public String getShangwu() { return shangwu; }
}
