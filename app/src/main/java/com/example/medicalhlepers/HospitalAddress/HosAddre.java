package com.example.medicalhlepers.HospitalAddress;
import org.litepal.crud.DataSupport;

public class HosAddre extends DataSupport {
    private int id;
    private String hosName;
    private double longitude;   //经度
    private double latitude;    //纬度

    public int getId() {
        return id;
    }

    public String getHosName() {
        return hosName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHosName(String hosName) {
        this.hosName = hosName;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
