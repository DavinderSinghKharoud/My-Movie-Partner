package com.example.mymoviepartner;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mymoviepartner.Models.MessageModel;
import com.example.mymoviepartner.ViewHolders.View_Messages_Adapter;
import com.example.mymoviepartner.ViewHolders.allPosts_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageScreen extends Fragment {

    //creating variables
    private RecyclerView recyclerView;
    private String MessageRoomID;
    private String CurrentUserID;
    private String OtherUserID;
    private FirebaseDatabase fDatabase;
    private DatabaseReference mRefUser;
    private DatabaseReference mRefMessages;
    private String OtherUserName;
    private EditText messageTyped;
    private ImageButton send_button;
    //Firebase variables

    private View_Messages_Adapter view_messages_adapter;
    private List<MessageModel> messageModelList;


    public MessageScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_screen, container, false);

        //getting reference
        messageTyped = view.findViewById(R.id.text_send);
        send_button = view.findViewById(R.id.btn_send);

        //setting RoomID,CurrentUserID, and Other UserID;
        getData();

        //getting database instance
        fDatabase = FirebaseDatabase.getInstance();

        //getting user reference from the database
        mRefUser = fDatabase.getReference("Users").child(OtherUserID);
        mRefMessages = fDatabase.getReference("Messages");


        //referencing recyclerView and setting fixed size
        recyclerView = view.findViewById(R.id.message_recyclerView);
        recyclerView.setHasFixedSize(true);
        //setting layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //setting the other user name in the title
        settingActionBarTitle();

        readMessages(MessageRoomID);

        //clicking send button
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send Message
                sendMessage();

                //empty the messageDescription
                messageTyped.setText("");

            }
        });

        return view;
    }

    /**
     * getting and sending data to the viewholder
     */
    private void readMessages(final String RoomID) {
        messageModelList = new ArrayList<>();

        mRefMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageModelList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message=snapshot.getValue(MessageModel.class);

                    if(message.getMessageRoomID().equals(RoomID)){
                        messageModelList.add(message);
                    }

                    view_messages_adapter=new View_Messages_Adapter(getContext(),messageModelList);
                    recyclerView.setAdapter(view_messages_adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * getting data from the previous fragment
     */
    private void getData() {
        //getting data from the other fragment
        MessageRoomID = getArguments().getString("RoomID");
        CurrentUserID = getArguments().getString("fUserID");
        OtherUserID = getArguments().getString("otherUserID");
    }

    /**
     * getting other user name and setting up in the title bar
     */
    private void settingActionBarTitle() {
        mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel user = dataSnapshot.getValue(userModel.class);

                //gettin userName of otherUser
                OtherUserName = user.getName();
                //setting his name in the title
                getActivity().setTitle(OtherUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * fetching the data from the editText and saving in the database
     */
    private void sendMessage() {
        //getting typed text
        String messDesc = messageTyped.getText().toString();

        if (TextUtils.isEmpty(messDesc)) {
            return;
        }

        //generating message ID
        String messageID = mRefMessages.push().getKey();

        //creating message object
        MessageModel message = new MessageModel(CurrentUserID, OtherUserID, MessageRoomID, messDesc);

        mRefMessages.child(messageID).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {



            }
        });


    }

}
