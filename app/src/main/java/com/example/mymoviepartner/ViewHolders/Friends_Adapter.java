package com.example.mymoviepartner.ViewHolders;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymoviepartner.Models.FriendsModel;
import com.example.mymoviepartner.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friends_Adapter extends RecyclerView.Adapter<Friends_Adapter.FriendsViewHolder> {

    //creating variables
    private ArrayList<FriendsModel> mFriendsList;
    private Context mContext;
    private boolean isAdded;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int positon);

        void onItemLongClick(View view, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView user_name;
        private TextView last_message;
        private ImageView img_on;
        private ImageView img_off;
        private RelativeLayout container;

        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            //getting reference
            container = itemView.findViewById(R.id.container_friendList);
            circleImageView = itemView.findViewById(R.id.messageList_image);
            user_name = itemView.findViewById(R.id.messageList_user_name);
            last_message = itemView.findViewById(R.id.message_last_msg);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(view, position);
                        }
                    }

                    return true;
                }
            });
        }
    }

    //creating constructor
    public Friends_Adapter(ArrayList<FriendsModel> mFriendsList, Context mContext, boolean isAdded) {
        this.mFriendsList = mFriendsList;
        this.mContext = mContext;
        this.isAdded = isAdded;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custum_msg_layout, parent, false);
        FriendsViewHolder fvh = new FriendsViewHolder(v, mListener);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {

        try {
        //getting values in the object
        FriendsModel friend = mFriendsList.get(position);

        //setting data in to the views
        holder.user_name.setText(friend.getUserName());
        holder.last_message.setText(friend.getLastMessage());


       // holder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        //getting imageURL
        String imageURl = friend.getImageUrl();

        if (isAdded) {
            if (imageURl.equals("default")) {
                holder.circleImageView.setImageResource(R.drawable.ic_launcher_background);
            } else {
                Glide.with(mContext).load(imageURl).into(holder.circleImageView);
            }
        }
        if (friend.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }


        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }


}
