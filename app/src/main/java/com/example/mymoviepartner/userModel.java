package com.example.mymoviepartner;

public class userModel {

    private String Name;
    private String Gender;
    private String ImageURL;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public userModel(String name, String gender, String imageURL,String status) {
        Name = name;
        Gender = gender;
        ImageURL = imageURL;
        this.status=status;
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
