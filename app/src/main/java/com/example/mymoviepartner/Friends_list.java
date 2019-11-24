package com.example.mymoviepartner;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymoviepartner.Models.FriendsModel;
import com.example.mymoviepartner.Models.MessageModel;
import com.example.mymoviepartner.Models.MessageRooms;
import com.example.mymoviepartner.Notification.Token;
import com.example.mymoviepartner.ViewHolders.Friends_Adapter;
import com.example.mymoviepartner.ViewHolders.allPosts_Adapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Friends_list extends Fragment {
    //creating variables
    private RecyclerView recyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFriendsRef;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;
    private TextView emptyView;
    //Adapter and list
    private Friends_Adapter friends_adapter;
    private ArrayList<FriendsModel> listFriends = new ArrayList<>();
    private ChildEventListener mChildEventListener;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private boolean checker = true;
    //user details


    public Friends_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        emptyView = (TextView) view.findViewById(R.id.empty_view);

        //Referencing Navigation View and checking navigation menu item as home
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_messages);

        //setting title
        getActivity().setTitle("My Movie Partner");


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //getting reference from the database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendsRef = mFirebaseDatabase.getReference("MessageRooms");


        //setting up progress bar
        progressBar = view.findViewById(R.id.progress_bar_friendsList);


        //setting up recycler view
        settingUpRecyclerView(view);

        if (checker == true) {
            progressBar.setVisibility(View.VISIBLE);
            addChildEventListener();
            changeFriends();

            //fetch the friends List
            //     fetchFriendsList();


        }

    }


    /**
     * creating child event listener for the messsage rooms
     */

    private void addChildEventListener() {


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                try {


                    //getting messageRoom
                    MessageRooms messageRooms = dataSnapshot.getValue(MessageRooms.class);

                    if (messageRooms.getUser1().equals(currentUser.getUid())
                            || messageRooms.getUser2().equals(currentUser.getUid())) {

                        if (messageRooms.getUser1().equals(currentUser.getUid())) {

                            gettingOtherUserDetails(messageRooms.getUser2(), dataSnapshot.getKey());


                        } else {

                            gettingOtherUserDetails(messageRooms.getUser1(), dataSnapshot.getKey());

                        }

                    }


                    // friends_adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.INVISIBLE);
                    if (listFriends.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    // changeVisibility();

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                try {
                    String messageRoomID = dataSnapshot.getKey();

                    int index = 0;
                    for (int i = 0; i < listFriends.size(); i++) {
                        FriendsModel friendsModel = listFriends.get(i);
                        String messageRoomIdFromFriends = friendsModel.getRoomID();

                        if (messageRoomID.equals(messageRoomIdFromFriends)) {
                            listFriends.remove(index);
                            friends_adapter.notifyItemRemoved(index);
                            break;
                        } else {
                            index++;
                        }


                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    /**
     * getting last Message to display on the screen
     */
    private void gettingLastMessage(final String messageRoomId, final String OtherUserId, final String otherUserName, final String ImageURl) {

        try {
            //getting Room reference
            DatabaseReference mMessages = mFirebaseDatabase.getReference("Messages");

            Query query = mMessages.orderByChild("messageRoomID").equalTo(messageRoomId).limitToLast(1);

            FriendsModel friend = new FriendsModel(ImageURl, otherUserName, "", messageRoomId, OtherUserId, "offline");
            listFriends.add(friend);

            friends_adapter.notifyItemInserted(listFriends.size() - 1);

            if (listFriends.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.INVISIBLE);
            }


            progressBar.setVisibility(View.INVISIBLE);


            //checking the status(online/offline)
            checkStatus();

            String test = "";
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {

                        String lastMessage = "";

                        //getting message object
                        MessageModel message = dataSnapshot.getValue(MessageModel.class);


                        lastMessage = message.getMessageDesc();
                        String messageRoomIdFromDesc = message.getMessageRoomID();


                        int index = 0;
                        for (int i = 0; i < listFriends.size(); i++) {
                            FriendsModel friendsModel = listFriends.get(i);
                            String messageRoomIdFromFriends = friendsModel.getRoomID();

                            if (messageRoomIdFromDesc.equals(messageRoomIdFromFriends)) {
                                break;
                            } else {
                                index++;
                            }


                        }


                        listFriends.get(index).setLastMessage(lastMessage);

                        friends_adapter.notifyItemChanged(index);

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * getting user details
     *
     * @param userId
     */
    private void gettingOtherUserDetails(final String userId, final String messageRoomID) {

        DatabaseReference mOtherUserRef = mFirebaseDatabase.getReference("Users").child(userId);

        mOtherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    //getting the user details and saving in the user model
                    userModel mUser = dataSnapshot.getValue(userModel.class);

                    //getting user details
                    String otherUserName = mUser.getName();
                    String ImageURl = mUser.getImageURL();


                    gettingLastMessage(messageRoomID, userId, otherUserName, ImageURl);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG);
            }
        });

    }

    /**
     * check the status of the friends
     */
    private void checkStatus() {


        for (int index = 0; index < listFriends.size(); index++) {
            FriendsModel friendsModel = listFriends.get(index);
            String userIdFromList = friendsModel.getUserID();


            DatabaseReference mOtherUserRef = mFirebaseDatabase.getReference("Users").child(userIdFromList).child("user_status");


            final int finalIndex = index;
            final int finalIndex1 = index;

            mOtherUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {


                        String status = dataSnapshot.getValue(String.class);


                        listFriends.get(finalIndex).setStatus(status);

                        friends_adapter.notifyItemChanged(finalIndex);

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    /**
     * setting up recycler view
     */
    private void settingUpRecyclerView(View view) {
        //referencing recycler view
        recyclerView = view.findViewById(R.id.friends_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        friends_adapter = new Friends_Adapter(listFriends, getContext(), isAdded());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(friends_adapter);


        friends_adapter.setOnItemClickListener(new Friends_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int positon) {

                try {
                    FriendsModel friend = listFriends.get(positon);

                    movingMessageFragment(friend.getRoomID(), currentUser.getUid(), friend.getUserID());


                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {


                //create an AlertDialog
                alertDialogBuilder = new AlertDialog.Builder(getContext());


                LayoutInflater inflater2 = LayoutInflater.from(getContext());

                view = inflater2.inflate(R.layout.confirmation_dialog, null);

                TextView textTitle = view.findViewById(R.id.textAlert);
                textTitle.setText("Delete Chat?");

                Button noButton = (Button) view.findViewById(R.id.nobtn);
                Button yesButton = (Button) view.findViewById(R.id.yesbtn);

                alertDialogBuilder.setView(view);
                dialog = alertDialogBuilder.create();
                dialog.show();

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriend(position);
                        dialog.dismiss();
                    }
                });
            }


        });
    }

    /**
     * delete the object from the database
     *
     * @param position
     */
    private void deleteFriend(int position) {
        try {

            FriendsModel friend = listFriends.get(position);

            // listFriends.remove(position);
            //friends_adapter.notifyItemRemoved(position);
            mFriendsRef.child(friend.getRoomID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Toast.makeText(getContext(), "room deleted", Toast.LENGTH_LONG).show();
                }
            });

            changeVisibility();

            //getting Room reference
            DatabaseReference mMessages = mFirebaseDatabase.getReference("Messages");

            Query query = mMessages.orderByChild("messageRoomID").equalTo(friend.getRoomID());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {


                        for (DataSnapshot message : dataSnapshot.getChildren()) {
                            message.getRef().removeValue();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displaying the empty text view
     */
    private void changeVisibility() {
        if (listFriends.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {

            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Getting fragment manager and moving to another fragment with id
     *
     * @param RoomID
     */
    private void movingMessageFragment(String RoomID, String fUserID, String otherUserID) {

        MessageScreen messageScreen = new MessageScreen();

        //creating bundle and adding data
        Bundle bundle = new Bundle();
        bundle.putString("home", "home");
        bundle.putString("RoomID", RoomID);
        bundle.putString("fUserID", fUserID);
        bundle.putString("otherUserID", otherUserID);

        messageScreen.setArguments(bundle);

        // mFriendsRef.removeEventListener(mChildEventListener);
        // listFriends.clear();

        //Adding again the home fragment and replacing it with message fragment
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container,
                        messageScreen).commit();
    }

    private void changeFriends() {
        mFriendsRef.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFriendsRef.removeEventListener(mChildEventListener);
    }


    void checkEmpty() {
        emptyView.setVisibility(friends_adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        //gg
    }

    @Override
    public void onPause() {
        super.onPause();
        checker = false;
    }


}
