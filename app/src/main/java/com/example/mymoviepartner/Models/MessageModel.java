package com.example.mymoviepartner.Models;

import com.google.firebase.database.PropertyName;

public class MessageModel {

    private String senderID;
    private String receiverID;
    private String messageRoomID;
    private String messageDesc;
    private String time_stamp;
    private boolean isSeen;


    @PropertyName("isSeen")
    public boolean isSeen() {
        return isSeen;
    }


    @PropertyName("isSeen")
    public void setSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public MessageModel() {
    }

    public String getMessageDesc() {
        return messageDesc;
    }


    public MessageModel(String senderID, String receiverID, String messageRoomID, String messageDesc, String time_stamp, boolean isSeen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageRoomID = messageRoomID;
        this.messageDesc = messageDesc;
        this.time_stamp = time_stamp;
        this.isSeen=isSeen;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String  time_stamp) {
        this.time_stamp = time_stamp;
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
