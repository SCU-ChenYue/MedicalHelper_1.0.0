package com.example.medicalhlepers.AIdiagnose;

public class SymptomDao {
    private String symptom;
    private String disease;
    private boolean flag = false;

    public SymptomDao(String symptom, boolean flag) {
        this.symptom = symptom;
        this.flag = flag;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public SymptomDao() {}

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
