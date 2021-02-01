package com.example.medicalhlepers.PriceQuery;

public class Examination {
    private String examName;
    private String examPrice;
    private String otherInfor;

    public Examination(String examName, String examPrice, String otherInfor) {
        this.examName = examName;
        this.examPrice = examPrice;
        this.otherInfor = otherInfor;
    }

    public String getExamName() {
        return examName;
    }

    public String getExamPrice() {
        return examPrice;
    }
    public String getOtherInfor() { return  otherInfor; }
}
