package com.example.medicalhlepers.Forum;


import java.io.Serializable;

public class QuestionBeenAnswerItem implements Serializable {
    private String docName;     //医生名字
    private String docTitle;    //医生职称
    private String docHospital; //医院名字
    private String imageUrl;    //医生头像
    private String department;  //医生科室
    private String docLevel;    //医生教授
    private String docGoodAt;   //医生长处
    private String docDescription;  //医生介绍

    private String question;    //问题
    private String answer;  //回答
    private String description; //问题描述
    private String askName; //问问题的人
    private String askDate; //问问题的时间
    private String askPhone;
    private int questionNum;    //问题的回答数
    private int numId;  //问题的标识
    private int mark;   //回答的标识
    private int like;   //点赞数
    private int commentNum; //评论数
    private int collectNum; //收藏数
    private boolean ifHasliked = false; //是否点了赞
    private boolean ifHasCollected = false; //是否收藏
    private String date;    //回答的时间

    public String getAskDate() {
        return askDate;
    }

    public void setAskDate(String askDate) {
        this.askDate = askDate;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDocLevel() {
        return docLevel;
    }

    public void setDocLevel(String docLevel) {
        this.docLevel = docLevel;
    }

    public String getDocGoodAt() {
        return docGoodAt;
    }

    public void setDocGoodAt(String docGoodAt) {
        this.docGoodAt = docGoodAt;
    }

    public String getAskPhone() {
        return askPhone;
    }

    public void setAskPhone(String askPhone) {
        this.askPhone = askPhone;
    }

    public String getAskName() {
        return askName;
    }

    public void setAskName(String askName) {
        this.askName = askName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isIfHasliked() {
        return ifHasliked;
    }

    public void setIfHasliked(boolean ifHasliked) {
        this.ifHasliked = ifHasliked;
    }

    public boolean isIfHasCollected() {
        return ifHasCollected;
    }

    public void setIfHasCollected(boolean ifHasCollected) {
        this.ifHasCollected = ifHasCollected;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public int getNumId() {
        return numId;
    }

    public void setNumId(int numId) {
        this.numId = numId;
    }

    public String getDocHospital() {
        return docHospital;
    }

    public void setDocHospital(String docHospital) {
        this.docHospital = docHospital;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
