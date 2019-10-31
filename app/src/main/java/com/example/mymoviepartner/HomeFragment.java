package com.example.mymoviepartner;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mymoviepartner.ViewHolders.Post_ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.ServiceConfigurationError;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //creating variables
    private RecyclerView recyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseRecyclerOptions<PostModel> options;
    private FirebaseRecyclerAdapter<PostModel, Post_ViewHolder> mFirebaseAdapter;
    //creating string, which will contain the posted time
    private String TimePostedOn = "Posted: ";
    private LinearLayoutManager mLayoutManager;
    private Spinner spinner;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //Clearing the back stack of fragments
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //setting it to true, to setup the custom menu to the application
        setHasOptionsMenu(true);

        //spinner starts
        spinner = view.findViewById(R.id.spinner1);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray((R.array.names)));

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        //spinner ends

        //Referencing Navigation View and checking navigation menu item as home
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);

        //referencing recyclerView and setting fixed size
        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        //setting up the layout manager and setting the latest post added at the top
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        //setting layout manager
        recyclerView.setLayoutManager(mLayoutManager);

        //getting reference from the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Posts");

        //Creating query and getting data
        createQuery(mRef);

        mFirebaseAdapter.startListening();
        //setting up the adapter
        recyclerView.setAdapter(mFirebaseAdapter);

        return view;
    }

    /**
     * setting up firebase adapter to fetch and display the data
     *
     * @param mRef
     */
    private void createQuery(DatabaseReference mRef) {

        //setting firebase recyclerOptions
        options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(mRef, PostModel.class).build();

        //Setting adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PostModel, Post_ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Post_ViewHolder post_viewHolder, int i, @NonNull final PostModel postModel) {
                //getting user id
                final String userID = postModel.getUser_id();

                DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                userDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //getting the user details and saving in the user model
                        userModel mUser = dataSnapshot.getValue(userModel.class);


                        //getting time of the post from the current time
                        getPostedTime(postModel);


                        //just to make sure fragment is loaded before the data is passed
                        boolean isAdded = isAdded();

                        try {
                            //sending the values to the post holder, so that they can be setup in the views.
                            post_viewHolder.setDetails(getContext(), postModel.getTitle(),
                                    postModel.getDescription(), postModel.getDate(), postModel.getTime(),
                                    postModel.getLocation(), mUser.getGender(), mUser.getName(),
                                    mUser.getImageURL(), TimePostedOn, isAdded);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @NonNull
            @Override
            public Post_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile_layout, parent, false);


                return new Post_ViewHolder(view);
            }
        };
    }

    /**
     * To get the time difference of the posts from the current time
     *
     * @param postModel
     */
    private void getPostedTime(PostModel postModel) {
        //getting the time, when the post was created
        long TimeOfPost = postModel.getTime_stamp();

        //getting the difference between the current time and the post time in milliseconds
        long msDiff = Calendar.getInstance().getTimeInMillis() - TimeOfPost;

        //getting the difference of days between today and when the post created
        String daysDiff = String.valueOf(TimeUnit.MILLISECONDS.toDays(msDiff));
        //getting the difference of hours between today and when the post created
        String hoursDiff = String.valueOf(TimeUnit.MILLISECONDS.toHours(msDiff));
        //getting the difference of minutes between today and when the post created
        String minutesDiff = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(msDiff));

        //setting up if condition to get the final posted time
        if (!daysDiff.equals("0")) {
            //if the post is not created on the same day
            this.TimePostedOn = "Posted: " + daysDiff + " day ago";
        } else if (!hoursDiff.equals("0")) {
            //if the post is created not in the same hour
            this.TimePostedOn = "Posted: " + hoursDiff + " h ago";
        } else if (hoursDiff.equals("0")) {
            //when the post is created in the same hour, so we add the minute difference
            this.TimePostedOn = "Posted: " + minutesDiff + " min ago";
        }
    }

    /**
     * This method will filter the posts, according to user typed text in the search bar.
     *
     * @param search_text
     * @param type
     */
    private void firebaseSearch(String search_text, String type) {
        String user_text = search_text;

        Query firebaseSearchQuery = mRef.orderByChild(type).startAt(user_text).endAt(user_text + "\uf8ff");

        //setting firebase recyclerOptions
        options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(firebaseSearchQuery, PostModel.class).build();


        //Setting adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PostModel, Post_ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Post_ViewHolder post_viewHolder, int i, @NonNull final PostModel postModel) {
                //getting user id
                final String userID = postModel.getUser_id();

                DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                userDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //getting the user details and saving in the user model
                        userModel mUser = dataSnapshot.getValue(userModel.class);


                        //getting time of the post from the current time
                        getPostedTime(postModel);


                        //just to make sure fragment is loaded before the data is passed
                        boolean isAdded = isAdded();

                        try {
                            //sending the values to the post holder, so that they can be setup in the views.
                            post_viewHolder.setDetails(getContext(), postModel.getTitle(),
                                    postModel.getDescription(), postModel.getDate(), postModel.getTime(),
                                    postModel.getLocation(), mUser.getGender(), mUser.getName(),
                                    mUser.getImageURL(), TimePostedOn, isAdded);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @NonNull
            @Override
            public Post_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile_layout, parent, false);


                return new Post_ViewHolder(view);
            }
        };


        //setting layout manager
        recyclerView.setLayoutManager(mLayoutManager);

        mFirebaseAdapter.startListening();
        //setting up the adapter
        recyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //inflating the search field
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        android.widget.SearchView searchView = (android.widget.SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Integer index = spinner.getSelectedItemPosition();
                if (index == 2) {

                    firebaseSearch(query, "location");
                } else if (index == 3) {
                    firebaseSearch(query, "gender");
                } else {

                    firebaseSearch(query, "title");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Integer index = spinner.getSelectedItemPosition();
                if (index == 2) {

                    firebaseSearch(newText, "location");
                } else if (index == 3) {
                    firebaseSearch(newText, "gender");
                } else {

                    firebaseSearch(newText, "title");
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }

    //private void displayData
    @Override
    public void onStart() {
        super.onStart();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseAdapter != null) {
            //setting layout manager
            mFirebaseAdapter.startListening();
        }
    }
}
