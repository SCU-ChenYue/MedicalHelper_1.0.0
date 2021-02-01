package com.example.medicalhlepers.AIdiagnose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Symptom implements Serializable {
    private String diseaseName;
    private List<String> symptoms = new ArrayList<>();

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }
}
