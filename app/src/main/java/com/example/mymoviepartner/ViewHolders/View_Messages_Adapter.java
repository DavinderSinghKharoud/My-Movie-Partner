package com.example.mymoviepartner.ViewHolders;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviepartner.Models.MessageModel;
import com.example.mymoviepartner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class View_Messages_Adapter extends RecyclerView.Adapter<View_Messages_Adapter.messageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<MessageModel> mMessageList;
    private FirebaseUser fUser;

    //constructor
    public View_Messages_Adapter(Context mContext, List<MessageModel> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
    }

    //ViewHolder class
    public static class messageViewHolder extends RecyclerView.ViewHolder {
        //Creating variables
        private View mView;
        private TextView show_messages;
        private TextView show_time;
        private ImageView check_read;


        public messageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            //referencing
            show_messages = mView.findViewById(R.id.show_message);
            show_time = mView.findViewById(R.id.chat_time);
            check_read = mView.findViewById(R.id.check);
        }
    }

    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right, parent, false);
            return new View_Messages_Adapter.messageViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left, parent, false);
            return new View_Messages_Adapter.messageViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {
        //getting the message object
        MessageModel message = mMessageList.get(position);
        //setting text
        holder.show_messages.setText(message.getMessageDesc());
        holder.show_time.setText(String.valueOf(message.getTime_stamp()));

        if (!message.isSeen()) {
            holder.check_read.setImageResource(R.drawable.check_grey);
        }

    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //getting IDS
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String senderID = mMessageList.get(position).getSenderID();
        if (senderID.equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
