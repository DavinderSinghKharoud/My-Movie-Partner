package com.example.mymoviepartner.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymoviepartner.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class My_OwnPost_ViewHolder extends RecyclerView.ViewHolder {

    //Creating variables
    private View mView;
    private TextView mTitle, mDesc, mDate, mTime, mLocation, mGender, mName, mPostedTime;
    private CircleImageView circleImageView;
    private My_OwnPost_ViewHolder.ClickListener mClickListener;
    private ImageView edit_own_post, delete_own_post;

    public My_OwnPost_ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //getting reference
        edit_own_post = mView.findViewById(R.id.edit_my_post);
        delete_own_post = mView.findViewById(R.id.delete_my_post);

        edit_own_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        delete_own_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemDeleteClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        mClickListener.onItemLongClick(view,position);
                    }
                }

                return true;
            }
        });
    }

    public void setDetails(Context context, String title, String description, String date,
                           String time, String location, String gender,
                           String name, String imageURl, String postedON, boolean isAdded) {
        //Views
        mTitle = mView.findViewById(R.id.my_post_title);
        mDesc = mView.findViewById(R.id.my_post_description);
        mDate = mView.findViewById(R.id.my_post_date);
        mTime = mView.findViewById(R.id.my_post_time);
        mLocation = mView.findViewById(R.id.my_post_location);
        mGender = mView.findViewById(R.id.my_post_gender);
        mName = mView.findViewById(R.id.my_post_name);
        circleImageView = mView.findViewById(R.id.profile_picture_own_Post);
        mPostedTime = mView.findViewById(R.id.my_post_postedOn);
        if (isAdded) {
            //Set data to views
            mTitle.setText(title);
            mDesc.setText(description);
            mDate.setText("On: " + date);
            mTime.setText("At: " + time);
            mLocation.setText("Location: " + location);
            mGender.setText("Gender: " + gender);
            mName.setText(name);
            mPostedTime.setText(postedON);


            if (imageURl.equals("default")) {
                circleImageView.setImageResource(R.drawable.ic_launcher_background);
            } else {
                Glide.with(context).load(imageURl).into(circleImageView);
            }
        }
    }

    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemDeleteClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(My_OwnPost_ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
