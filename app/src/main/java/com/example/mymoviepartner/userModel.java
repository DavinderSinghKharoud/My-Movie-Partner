package com.example.mymoviepartner;

public class userModel {

    private String Name;
    private String Gender;
    private String ImageURL;

    public userModel(String name, String gender) {
        Name = name;
        Gender = gender;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public userModel(String name, String gender, String imageURL) {
        Name = name;
        Gender = gender;
        ImageURL = imageURL;
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
