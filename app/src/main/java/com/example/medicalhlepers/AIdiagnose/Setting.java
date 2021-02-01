package com.example.medicalhlepers.AIdiagnose;

import org.litepal.crud.DataSupport;

public class Setting extends DataSupport {
    private String hospitalchooseType;
    private String hospitalchooseLevel;
    private String docchooseLevel;
    private int distance;
    private int distancePercent;    //距离和医院等级的权重比例
    private int levelPercent;

    public Setting() {}
    public Setting(String hospitalchooseType, String hospitalchooseLevel, String docchooseLevel,
                   int distance, int distancePercent, int levelPercent) {
        this.hospitalchooseType  = hospitalchooseType;
        this.hospitalchooseLevel = hospitalchooseLevel;
        this.docchooseLevel = docchooseLevel;
        this.distance = distance;
        this.distancePercent = distancePercent;
        this.levelPercent = levelPercent;
    }

    public String getHospitalchooseType() {
        return hospitalchooseType;
    }

    public void setHospitalchooseType(String hospitalchooseType) {
        this.hospitalchooseType = hospitalchooseType;
    }

    public String getHospitalchooseLevel() {
        return hospitalchooseLevel;
    }

    public void setHospitalchooseLevel(String hospitalchooseLevel) {
        this.hospitalchooseLevel = hospitalchooseLevel;
    }

    public String getDocchooseLevel() {
        return docchooseLevel;
    }

    public void setDocchooseLevel(String docchooseLevel) {
        this.docchooseLevel = docchooseLevel;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistancePercent() {
        return distancePercent;
    }

    public void setDistancePercent(int distancePercent) {
        this.distancePercent = distancePercent;
    }

    public int getLevelPercent() {
        return levelPercent;
    }

    public void setLevelPercent(int levelPercent) {
        this.levelPercent = levelPercent;
    }
}
