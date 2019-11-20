package com.example.mymoviepartner.Models;

public class FriendsModel {

    private String ImageUrl;
    private String userName;
    private String lastMessage;
    private String RoomID;
    private String userID;
    private String user_status;

    public FriendsModel() {
    }

    public FriendsModel(String imageUrl, String userName, String lastMessage, String roomID, String userID,String status) {
        ImageUrl = imageUrl;
        this.userName = userName;
        this.lastMessage = lastMessage;
        RoomID = roomID;
        this.userID = userID;
        this.user_status=status;
    }

    public String getStatus() {
        return user_status;
    }

    public void setStatus(String status) {
        this.user_status = status;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public FriendsModel(String imageUrl, String userName, String lastMessage) {
        ImageUrl = imageUrl;
        this.userName = userName;
        this.lastMessage = lastMessage;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
