package com.example.medicalhlepers.DiagnoseOnline;

public class Messages {
    private String name;
    private String title;
    private String imageId;
    private String lastMsg;
    private String status;
    private String unreadMsg;

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setStatus(String status) { this.status = status; }

    public void setUnreadMsg(String unreadMsg) { this.unreadMsg = unreadMsg; }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getImageId() {
        return imageId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public String getStatus() { return status; }

    public String getUnreadMsg() { return unreadMsg; }
}
