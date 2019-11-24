package com.example.mymoviepartner;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mymoviepartner.Models.MessageRooms;
import com.example.mymoviepartner.ViewHolders.allPosts_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.security.spec.ECField;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //creating variables
    private RecyclerView recyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseUser currentUser;
    private String RoomID = "default";
    //creating string, which will contain the posted time
    private String TimePostedOn = "Posted: ";
    private LinearLayoutManager mLayoutManager;
    private Spinner spinner;
    private ProgressBar progressBar;
    private ValueEventListener mValueEventListener;

    //Adapters and list
    private allPosts_Adapter allPosts_adapter;
    private ArrayList<PostModel> listPost = new ArrayList<>();
    private ArrayList<PostModel> copyList;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //To check the internet connectivity
        isOnline();

        //setting title
        getActivity().setTitle("My Movie Partner");

        //referencing recycler view
        recyclerView = view.findViewById(R.id.my_recycler_view);

        //setting it to true, to setup the custom menu to the application
        setHasOptionsMenu(true);

        //setting up spinner(Drop down menu)
        setUpSpinner(view);

        //Referencing Navigation View and checking navigation menu item as home
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);

        //getting reference from the database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Posts");


        //setting up progress bar
        progressBar = view.findViewById(R.id.progress_bar_home);
        progressBar.setVisibility(View.VISIBLE);

        //attaching value event listener
        attachValueEventListener();
        //fetching data from the database
        fetchData();

        //setting up recycler view
        setUpRecyclerView();

        allPosts_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (listPost.isEmpty()) {
                    //Toast.makeText(getContext(),"empty",Toast.LENGTH_SHORT).show();

                }
            }
        });
        return view;
    }

    /**
     * creating new value event listener
     */
    private void attachValueEventListener() {

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    //clearing the list
                    listPost.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        PostModel postModel = postSnapshot.getValue(PostModel.class);
                        listPost.add(postModel);
                    }
                    copyList = new ArrayList<>(listPost);
                    allPosts_adapter.notifyDataSetChanged();

                    //dismissing the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * To check the internet connectivity
     */
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(getContext(), "Couldn't refresh feed, No Internet Connection", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * fetching data from the firebase and adding to the list
     */
    private void fetchData() {

        //adding value event listener

        mRef.addValueEventListener(mValueEventListener);


    }

    /**
     * setting up recycler view
     */
    private void setUpRecyclerView() {
        //referencing recyclerView and setting fixed size
        recyclerView.setHasFixedSize(true);

        //setting up the layout manager and setting the latest post added at the top
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);


        allPosts_adapter = new allPosts_Adapter(listPost, getContext(), isAdded());

        //setting layout manager
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(allPosts_adapter);


        allPosts_adapter.setOnItemClickListener(new allPosts_Adapter.onClickMessageListener() {
            @Override
            public void onMessageClick(int position) {


                // progressBar.setVisibility(View.VISIBLE);

                //getting post model
                PostModel postModel = listPost.get(position);
                //getting userID
                String postCreaterID = postModel.getUser_id();

                //you can only message to another users
                if (!postCreaterID.equals(currentUser.getUid())) {
                    createMessageRoom(postCreaterID);

                    // progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getContext(), "You can't message yourself", Toast.LENGTH_LONG).show();

                    // progressBar.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void onItemLongClick(View view, int position) {
                //getting post model
                PostModel postModel = listPost.get(position);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check this post on MyMoviePartner App:\n Title: " + postModel.getTitle()
                        + "\nPost Description: " + postModel.getDescription();
                String shareSubject = "Checkout our MyMoviePartner application from the GitHub: \n https://github.com/DavinderSinghKharoud/My-Movie-Partner";

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }

            @Override
            public void onShareClick(int position) {
                //getting post model
                PostModel postModel = listPost.get(position);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check this post on MyMoviePartner App:\n Title: " + postModel.getTitle()
                        + "\nPost Description: " + postModel.getDescription();
                String shareSubject = "Checkout our MyMoviePartner application from the GitHub: \n https://github.com/DavinderSinghKharoud/My-Movie-Partner";

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });


    }

    /**
     * Creating message room with the current userId and the ID of post creater
     */
    private void createMessageRoom(final String postCreaterID) {


        final DatabaseReference mRoomRef = mFirebaseDatabase.getReference("MessageRooms");

        mRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        //getting messageRoom in the messageRoom object
                        MessageRooms messageRooms = snapshot.getValue(MessageRooms.class);
                        //getting currentUserID
                        String currentUserUid = currentUser.getUid();

                        if ((messageRooms.getUser1().equals(currentUserUid) && messageRooms.getUser2().equals(postCreaterID))
                                || (messageRooms.getUser1().equals(postCreaterID) && messageRooms.getUser2().equals(currentUserUid))) {

                            //geting room id
                            RoomID = snapshot.getKey();
                            //setting again to default, just to getting all the logic
                            RoomID = "default";
                            //moving to another fragment with the messageRoomID
                            movingMessageFragment(snapshot.getKey(), currentUserUid, postCreaterID);
                            return;

                        }


                    }

                    if (RoomID.equals("default")) {
                        //creating room ID
                        final String roomID = mRoomRef.push().getKey();
                        //creating messageRoom
                        MessageRooms creatingMessageRoom = new MessageRooms(currentUser.getUid(), postCreaterID);
                        mRoomRef.child(roomID).setValue(creatingMessageRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                //getting room id
                                RoomID = roomID;
                                //setting again to default, just to getting all the logic
                                RoomID = "default";
                                //moving to another fragment with the messageRoomID
                                movingMessageFragment(roomID, currentUser.getUid(), postCreaterID);
                                return;
                            }
                        });

                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Getting fragment manager and moving to another fragment with id
     *
     * @param RoomID
     */
    private void movingMessageFragment(String RoomID, String fUserID, String otherUserID) {

        try {


            MessageScreen messageScreen = new MessageScreen();

            //creating bundle and adding data
            Bundle bundle = new Bundle();
            bundle.putString("home", "home");
            bundle.putString("RoomID", RoomID);
            bundle.putString("fUserID", fUserID);
            bundle.putString("otherUserID", otherUserID);

            messageScreen.setArguments(bundle);

            //Adding again the home fragment and replacing it with message fragment
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container,
                            messageScreen).commit();
        } catch (Exception e) {

        }
    }


    /**
     * setting up spinner
     *
     * @param view
     */
    private void setUpSpinner(View view) {

        //getting reference
        spinner = view.findViewById(R.id.spinner1);

        //setting up adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray((R.array.names)));

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter
        spinner.setAdapter(arrayAdapter);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        try {
            //inflating the search field
            inflater.inflate(R.menu.search_menu, menu);

            MenuItem myActionMenuItem = menu.findItem(R.id.action_search1);


            android.widget.SearchView searchView = (android.widget.SearchView) myActionMenuItem.getActionView();
            searchView.setQueryHint("Search");
            // searchView.setBackgroundColor(Color.MAGENTA);
            int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText searchEditText = (EditText) searchView.findViewById(searchSrcTextId);
            searchEditText.setTextColor(Color.WHITE);
            searchEditText.setHintTextColor(Color.WHITE);

            int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
            // Getting the 'search_plate' LinearLayout.
            View searchPlate = searchView.findViewById(searchPlateId);
            // Setting background of 'search_plate' to earlier defined drawable.
            //    searchPlate.setBackgroundResource(R.drawable.search_draw);
            searchView.setIconifiedByDefault(true);


            int closeButtonId = getResources().getIdentifier("android:id/search_close_btn", null, null);
            ImageView closeButtonImage = (ImageView) searchView.findViewById(closeButtonId);
            closeButtonImage.setImageResource(R.drawable.ic_close_black_24dp);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    //getting drop down item selected
                    Integer index = spinner.getSelectedItemPosition();


                    if (index == 1) {
                        //By userName
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getUserNameFilter().filter(query);
                    } else if (index == 2) {
                        //By location
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getLocationFilter().filter(query);

                    } else if (index == 3) {
                        //By Gender
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getGenderFilter().filter(query);
                    } else {
                        //By default(title)
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getFilter().filter(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //getting drop down item selected
                    Integer index = spinner.getSelectedItemPosition();


                    if (index == 1) {
                        //By userName
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getUserNameFilter().filter(newText);
                    } else if (index == 2) {
                        //By location
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getLocationFilter().filter(newText);

                    } else if (index == 3) {
                        //By Gender
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getGenderFilter().filter(newText);
                    } else {
                        //By default(title)
                        allPosts_adapter.setmPostListFull(copyList);
                        allPosts_adapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });

            super.onCreateOptionsMenu(menu, inflater);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    //private void displayData


    @Override
    public void onStop() {
        super.onStop();
        mRef.removeEventListener(mValueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        allPosts_adapter.notifyDataSetChanged();
    }


}
