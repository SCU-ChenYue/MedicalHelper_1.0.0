package com.example.medicalhlepers.Yuyue;

public class AppointmentClass {
    private String hosName;
    private String department;
    private String date;
    private String shangwu;
    private String title;
    private String doctorName;

    public AppointmentClass() {}

    public AppointmentClass(String hosName, String department, String date,
                            String shangwu, String title, String doctorName) {
        this.hosName = hosName;
        this.department = department;
        this.date = date;
        this.shangwu = shangwu;
        this.title = title;
        this.doctorName = doctorName;
    }
    public String getHosName() { return  hosName; }
    public String getDepartment() { return department; }
    public String getDate() { return date; }
    public String getShangwu() { return shangwu; }
    public String getTitle() { return title; }
    public String getDoctorName() { return doctorName; }

    public void setHosName(String hosName) {
        this.hosName = hosName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setShangwu(String shangwu) {
        this.shangwu = shangwu;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
