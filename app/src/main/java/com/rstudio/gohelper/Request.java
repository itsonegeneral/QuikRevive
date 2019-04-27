package com.rstudio.gohelper;

public class Request {
    private String senderId;
    private String receiverId;
    private String time;
    private String senderName;
    private String senderMessage;
    private float positionX;
    private float positionY;

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public Request(){
        //Empty Constructor
    }

    public Request(String senderId, String receiverId, String time, String senderName, String senderMessage) {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
        this.senderName = senderName;
        this.senderMessage = senderMessage;
    }

    public String getSenderId() {

        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }
}
