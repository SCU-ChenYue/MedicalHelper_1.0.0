package com.example.medicalhlepers.ChatRoom;

import org.litepal.crud.DataSupport;

public class Msg extends DataSupport {

    public static final int TYPE_SENT = 1;
    public static final int TYPE_RECEIVED = 0;
    public String content;
    private int type;
    private String userName;
    private String docName;
    private String title;
    private String msgRead;

    public Msg() {}

    public Msg(String content, int type, String userName, String docName, String title, String msgRead) {
        this.content = content;
        this.type = type;
        this.userName = userName;
        this.docName = docName;
        this.title = title;
        this.msgRead = msgRead;
    }

    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }
    public String getUserName() { return  userName; }
    public String getDocName() { return docName; }
    public String getTitle() { return title; }
    public String getMsgRead() { return msgRead; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setDocName(String docName) {
        this.docName = docName;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setTitle(String title) { this.title = title; }
    public void setMsgRead(String msgRead) { this.msgRead = msgRead; }
}
