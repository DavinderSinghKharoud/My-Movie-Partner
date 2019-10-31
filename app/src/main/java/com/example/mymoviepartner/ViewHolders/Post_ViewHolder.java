package com.example.mymoviepartner.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymoviepartner.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_ViewHolder extends RecyclerView.ViewHolder {
    //Creating variables
    private View mView;
    private TextView mTitle, mDesc, mDate, mTime, mLocation, mGender, mName, mPostedTime;
    private CircleImageView circleImageView;

    public Post_ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

    }


    public void setDetails(Context context, String title, String description, String date,
                           String time, String location, String gender, String name, String imageURl, String postedON, boolean isAdded) {
        //Views
        mTitle = mView.findViewById(R.id.textView2_title);
        mDesc = mView.findViewById(R.id.textView3_description);
        mDate = mView.findViewById(R.id.textView4_date);
        mTime = mView.findViewById(R.id.textView5_time);
        mLocation = mView.findViewById(R.id.textView6_location);
        mGender = mView.findViewById(R.id.textView7_gender);
        mName = mView.findViewById(R.id.textView1_name);
        circleImageView = mView.findViewById(R.id.profile_picture_viewPost);
        mPostedTime = mView.findViewById(R.id.textView8_postedOn);

        //Set data to views
        mTitle.setText(title);
        mDesc.setText(description);
        mDate.setText("On: " + date);
        mTime.setText("At: " + time);
        mLocation.setText("Location: " + location);
        mGender.setText("Gender: " + gender);
        mName.setText(name);
        mPostedTime.setText(postedON);

        if (isAdded)
            if (imageURl.equals("default")) {
                circleImageView.setImageResource(R.drawable.ic_launcher_background);
            } else {
                Glide.with(context).load(imageURl).into(circleImageView);
            }

    }
}
