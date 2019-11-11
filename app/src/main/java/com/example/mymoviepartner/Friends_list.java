package com.example.mymoviepartner;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private int change = 0;
    private TextView emptyView;
    //Adapter and list
    private Friends_Adapter friends_adapter;
    private ArrayList<FriendsModel> listFriends = new ArrayList<>();
    private ChildEventListener mChildEventListener;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    //user details


    public Friends_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

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

        addChildEventListener();
        changeFriends();


        //fetch the friends List
        //     fetchFriendsList();




        return view;
    }

    private void addChildEventListener() {
        change = 0;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //               Toast.makeText(getContext(),"assigned",Toast.LENGTH_LONG).show();
                //fetchFriendsList();
                /**if (change == 0) {
                 MessageRooms room = dataSnapshot.getValue(MessageRooms.class);
                 FriendsModel friend = new FriendsModel("default", room.getUser1(), "wow", dataSnapshot.getKey(), room.getUser2());
                 listFriends.add(0, friend);
                 friends_adapter.notifyDataSetChanged();
                 }**/

                //clearing the list
                // listFriends.clear();
                progressBar.setVisibility(View.VISIBLE);


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


                friends_adapter.notifyDataSetChanged();

                adapterCheck();
                progressBar.setVisibility(View.INVISIBLE);
                // changeVisibility();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                fetchFriendsList();
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
     * n
     * adding the friends in to the list
     */
    private void fetchFriendsList() {

        mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //clearing the list
                listFriends.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //getting messageRoom
                    MessageRooms messageRooms = snapshot.getValue(MessageRooms.class);


                    if (messageRooms.getUser1().equals(currentUser.getUid())
                            || messageRooms.getUser2().equals(currentUser.getUid())) {

                        if (messageRooms.getUser1().equals(currentUser.getUid())) {

                            gettingOtherUserDetails(messageRooms.getUser2(), snapshot.getKey());

                        } else {

                            gettingOtherUserDetails(messageRooms.getUser1(), snapshot.getKey());

                        }

                    }


                }
                friends_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * getting last Message to display on the screen
     */
    private void gettingLastMessage(final String messageRoomId, final String OtherUserId, final String otherUserName, final String ImageURl) {

        //getting Room reference
        DatabaseReference mMessages = mFirebaseDatabase.getReference("Messages");

        Query query = mMessages.orderByChild("messageRoomID").equalTo(messageRoomId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastMessage = "";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //getting message object
                    MessageModel message = snapshot.getValue(MessageModel.class);


                    lastMessage = message.getMessageDesc();


                }

                FriendsModel friend = new FriendsModel(ImageURl, otherUserName, lastMessage, messageRoomId, OtherUserId);
                listFriends.add(0, friend);

                friends_adapter.notifyDataSetChanged();

                changeVisibility();

                //dismissing the progress bar
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG);
            }
        });


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
                //getting the user details and saving in the user model
                userModel mUser = dataSnapshot.getValue(userModel.class);

                //getting user details
                String otherUserName = mUser.getName();
                String ImageURl = mUser.getImageURL();


                gettingLastMessage(messageRoomID, userId, otherUserName, ImageURl);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG);
            }
        });

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

                FriendsModel friend = listFriends.get(positon);

                movingMessageFragment(friend.getRoomID(), currentUser.getUid(), friend.getUserID());
            }

            @Override
            public void onItemLongClick(View view, final int position) {


                //create an AlertDialog
                alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("It will also delete messages.");
                LayoutInflater inflater2 = LayoutInflater.from(getContext());

                view = inflater2.inflate(R.layout.confirmation_dialog, null);

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

    private void deleteFriend(int position) {
        FriendsModel friend = listFriends.get(position);

        listFriends.remove(position);
        friends_adapter.notifyDataSetChanged();
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
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    message.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

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

        mFriendsRef.removeEventListener(mChildEventListener);
        listFriends.clear();

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


    private void adapterCheck(){
        friends_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                checkEmpty();            }
        });
    }
    void checkEmpty() {
        emptyView.setVisibility(friends_adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
