package com.example.mymoviepartner.Models;

public class MessageRooms {

    private String messageRoomID;
    private String user1;
    private String user2;

    public MessageRooms() {
    }

    public String getMessageRoomID() {
        return messageRoomID;
    }

    public MessageRooms(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public void setMessageRoomID(String messageRoomID) {
        this.messageRoomID = messageRoomID;
    }

    public MessageRooms(String messageRoomID, String user1, String user2) {
        this.messageRoomID = messageRoomID;
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }
}
