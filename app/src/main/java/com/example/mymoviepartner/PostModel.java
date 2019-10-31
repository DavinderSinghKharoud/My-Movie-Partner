package com.example.mymoviepartner;

public class PostModel {
    //creating variables
    private String post_id;
    private String user_id;
    private String title;
    private String Description;
    private String location;
    private String date;
    private String time;
    private long time_stamp;

    public PostModel() {
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }



    public PostModel(String user_id, String title, String description, String location, String date, String time,long timeStamp) {
        this.user_id = user_id;
        this.title = title;
        Description = description;
        this.location = location;
        this.date = date;
        this.time = time;
        this.time_stamp=timeStamp;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
