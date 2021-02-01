package com.example.medicalhlepers.AIdiagnose;

public class HospitalResult {
    private String hospitalName;
    private String hospitalLevel;
    private String hospitalType;
    private String hospitalAddre;
    private String hospitalPhone;

    public HospitalResult() {}
    public HospitalResult(String hospitalName, String hospitalLevel, String hospitalType,
                          String hospitalAddre) {
        this.hospitalName = hospitalName;
        this.hospitalLevel = hospitalLevel;
        this.hospitalType = hospitalType;
        this.hospitalAddre = hospitalAddre;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getHospitalLevel() {
        return hospitalLevel;
    }

    public String getHospitalType() {
        return hospitalType;
    }

    public String getHospitalAddre() {
        return hospitalAddre;
    }

    public String getHospitalPhone() {
        return hospitalPhone;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setHospitalLevel(String hospitalLevel) {
        this.hospitalLevel = hospitalLevel;
    }

    public void setHospitalType(String hospitalType) {
        this.hospitalType = hospitalType;
    }

    public void setHospitalAddre(String hospitalAddre) {
        this.hospitalAddre = hospitalAddre;
    }

    public void setHospitalPhone(String hospitalPhone) {
        this.hospitalPhone = hospitalPhone;
    }
}
