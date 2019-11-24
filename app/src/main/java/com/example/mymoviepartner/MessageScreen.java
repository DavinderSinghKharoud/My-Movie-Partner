package com.example.mymoviepartner;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymoviepartner.Models.MessageModel;
import com.example.mymoviepartner.Notification.APIService;
import com.example.mymoviepartner.Notification.Client;
import com.example.mymoviepartner.Notification.Data;
import com.example.mymoviepartner.Notification.MyResponse;
import com.example.mymoviepartner.Notification.Sender;
import com.example.mymoviepartner.Notification.Token;
import com.example.mymoviepartner.ViewHolders.View_Messages_Adapter;
import com.example.mymoviepartner.ViewHolders.allPosts_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageScreen extends Fragment {

    //creating variables
    private RecyclerView recyclerView;
    private String MessageRoomID;
    private String CurrentUserID;
    private String OtherUserID;
    private TextView user_status;
    private FirebaseDatabase fDatabase;
    private DatabaseReference mRefUser;
    private DatabaseReference mRefMessages;
    private String OtherUserName;
    private EditText messageTyped;
    private ImageButton send_button;
    APIService apiService;
    boolean notify = false;
    private ValueEventListener userDetailsValueEventListener;
    private ValueEventListener messageReadValueEventListener;
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
        user_status = view.findViewById(R.id.user_status_message);

        //setting RoomID,CurrentUserID, and Other UserID;
        getData();

        //getting database instance
        fDatabase = FirebaseDatabase.getInstance();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


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


        setUpUserDetailsValueEventListener();
        //setting the other user name in the title
        settingActionBarTitle();


        readMessages(MessageRoomID);

        //clicking send button
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                //send Message
                sendMessage();

                //empty the messageDescription
                messageTyped.setText("");

            }
        });


        view_messages_adapter = new View_Messages_Adapter(getContext(), messageModelList);
        recyclerView.setAdapter(view_messages_adapter);

        return view;
    }


    /**
     * getting and sending data to the viewholder
     */
    private void readMessages(final String RoomID) {
        messageModelList = new ArrayList<>();

        messageReadValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    messageModelList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MessageModel message = snapshot.getValue(MessageModel.class);


                        if (message.getMessageRoomID().equals(RoomID)) {

                            if (message.getReceiverID().equals(CurrentUserID)) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isSeen", true);
                                snapshot.getRef().updateChildren(hashMap);
                            }

                            messageModelList.add(message);
                        }
                        view_messages_adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(view_messages_adapter);

                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRefMessages.addValueEventListener(messageReadValueEventListener);


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
        mRefUser.addValueEventListener(userDetailsValueEventListener);

    }

    private void setUpUserDetailsValueEventListener() {
        userDetailsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    userModel user = dataSnapshot.getValue(userModel.class);

                    //gettin userName of otherUser
                    OtherUserName = user.getName();
                    //setting his name in the title
                    getActivity().setTitle(OtherUserName);

                    String status = user.getUser_status();

                    if (status.equals("online")) {
                        user_status.setVisibility(View.VISIBLE);
                    } else {
                        user_status.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * fetching the data from the editText and saving in the database
     */
    private void sendMessage() {
        try {


            //getting typed text
            String messDesc = messageTyped.getText().toString();

            if (TextUtils.isEmpty(messDesc)) {
                return;
            }

            //generating message ID
            String messageID = mRefMessages.push().getKey();

            //To get the current Date and time0

            Date date = new Date();
            String timeDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date);


            //creating message object
            MessageModel message = new MessageModel(CurrentUserID, OtherUserID, MessageRoomID, messDesc, timeDate, false);

            mRefMessages.child(messageID).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {


                }
            });


            final String msg = messDesc;


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {


                        userModel user = dataSnapshot.getValue(userModel.class);
                        if (notify)
                            sendNotification(OtherUserID, user.getName(), msg);
                        notify = false;
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void sendNotification(String otherUserID, final String name, final String msg) {
        try {
            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            Query query = tokens.orderByKey().equalTo(otherUserID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(OtherUserID, R.mipmap.ic_launcher, msg, name, CurrentUserID);

                        Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {

                                    if (response.body().success != 1) {

                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

                                    } else {

                                        // Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRefUser.removeEventListener(userDetailsValueEventListener);
        mRefMessages.removeEventListener(messageReadValueEventListener);
    }
}
