package com.example.medicalhlepers.AIdiagnose;

public class DiagnoseResult {
    private String result;
    private double percent;
    public DiagnoseResult(String result, double percent) {
        this.result = result;
        this.percent = percent;
    }

    public String getResult() {
        return result;
    }

    public double getPercent() {
        return percent;
    }
}
