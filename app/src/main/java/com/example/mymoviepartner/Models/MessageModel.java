package com.example.mymoviepartner.Models;

public class MessageModel {

    private String senderID;
    private String receiverID;
    private String messageRoomID;
    private String messageDesc;

    public MessageModel() {
    }

    public String getMessageDesc() {
        return messageDesc;
    }

    public void setMessageDesc(String messageDesc) {
        this.messageDesc = messageDesc;
    }

    public MessageModel(String senderID, String receiverID, String messageRoomID, String messageDesc) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageRoomID = messageRoomID;
        this.messageDesc = messageDesc;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessageRoomID() {
        return messageRoomID;
    }

    public void setMessageRoomID(String messageRoomID) {
        this.messageRoomID = messageRoomID;
    }
}
