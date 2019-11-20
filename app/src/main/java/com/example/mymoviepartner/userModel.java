package com.example.mymoviepartner;

public class userModel {

    private String Name;
    private String Gender;
    private String ImageURL;
    private String user_status;


    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public userModel(String name, String gender, String imageURL,String user_status) {
        Name = name;
        Gender = gender;
        ImageURL = imageURL;
        this.user_status=user_status;
    }

    public userModel() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
