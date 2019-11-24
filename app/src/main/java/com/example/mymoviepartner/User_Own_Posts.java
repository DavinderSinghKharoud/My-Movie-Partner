package com.example.mymoviepartner;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymoviepartner.ViewHolders.My_OwnPost_ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class User_Own_Posts extends Fragment {

    //creating variables
    private RecyclerView recyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseUser cuurentUser;
    private TextView empty_view;
    private FirebaseRecyclerOptions<PostModel> options;
    private FirebaseRecyclerAdapter<PostModel, My_OwnPost_ViewHolder> mFirebaseAdapter;
    private Query query;
    //creating string, which will contain the posted time
    private String TimePostedOn = "Posted: ";
    private LinearLayoutManager mLayoutManager;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;

    public User_Own_Posts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user__own__posts, container, false);

        empty_view = view.findViewById(R.id.empty_view_ownPosts);

        //referencing recyclerView and setting fixed size
        recyclerView = view.findViewById(R.id.recyclerView_own_posts);
        recyclerView.setHasFixedSize(true);


        //setting up the layout manager and setting the latest post added at the top
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        //setting layout manager
        recyclerView.setLayoutManager(mLayoutManager);

        //getting current user
        cuurentUser = FirebaseAuth.getInstance().getCurrentUser();

        //getting reference from the databa`se
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Posts");

        //creating query to sort out the posts created by the current user
        query = mRef.orderByChild("user_id").equalTo(cuurentUser.getUid());

        //setting firebase recyclerOptions with the query created
        options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class).build();

        //Setting adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PostModel, My_OwnPost_ViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final My_OwnPost_ViewHolder post_viewHolder, int i, @NonNull final PostModel postModel) {

                empty_view.setVisibility(View.INVISIBLE);
                //getting user id
                final String userID = postModel.getUser_id();

                //Getting reference of the post clicked with the help of position
                DatabaseReference refCurrenPost = getRef(i);
                String post_id = refCurrenPost.getKey().toString();

                //binding data to the holder
                bindingData(userID, postModel, post_viewHolder, post_id);


            }

            @NonNull
            @Override
            public My_OwnPost_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_own_profile_layout, parent, false);


                return new My_OwnPost_ViewHolder(view);
            }
        };


        mFirebaseAdapter.startListening();
        //setting up the adapter
        recyclerView.setAdapter(mFirebaseAdapter);

        return view;
    }

    /**
     * getting reference of the node
     * fetching the data
     * sending to the adapter to be displayed in the views
     *
     * @param userID
     * @param postModel
     * @param post_viewHolder
     */
    private void bindingData(String userID, final PostModel postModel,
                             final My_OwnPost_ViewHolder post_viewHolder, final String post_id) {

        //getting instance of the user id
        DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        //fetching data whenever the data changes
        userDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getting the user details and saving in the user model
                try {
                    userModel mUser = dataSnapshot.getValue(userModel.class);


                    //getting time of the post from the current time
                    getPostedTime(postModel);


                    //sending the values to the post holder, so that they can be setup in the views.
                    post_viewHolder.setDetails(getContext(), postModel.getTitle(),
                            postModel.getDescription(), postModel.getDate(), postModel.getTime(),
                            postModel.getLocation(), mUser.getGender(), mUser.getName(),
                            mUser.getImageURL(), TimePostedOn, isAdded());

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }


                //when ever the edit button is clicked
                post_viewHolder.setOnClickListener(new My_OwnPost_ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //creating bundle, which will contain data
                        Bundle data = new Bundle();

                        //putting data
                        data.putString("ownPosts", "ownPosts");
                        data.putString("title", postModel.getTitle());
                        data.putString("desc", postModel.getDescription());
                        data.putString("location", postModel.getLocation());
                        data.putString("date", postModel.getDate());
                        data.putString("time", postModel.getTime());
                        data.putString("post_id", post_id);

                        //creating new createPost fragment object
                        CreatePost createPost = new CreatePost();
                        //adding data to the object
                        createPost.setArguments(data);

                        //sending the request to change the current fragment
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        createPost).addToBackStack(null).commit();
                    }

                    //Whenever the delete button is clicked
                    @Override
                    public void onItemDeleteClick(View view, int position) {
                        //create an AlertDialog
                        alertDialogBuilder = new AlertDialog.Builder(getContext());

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
                                mRef.child(post_id).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(),
                                                        "Post Deleted",
                                                        Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        });
                        /*
                        mRef.child(post_id).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(),"deleted",Toast.LENGTH_SHORT).show();
                            }
                        });*/
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        //getting post model

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * getting the time difference between the current time and the time, at which post is created
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
            mFirebaseAdapter.startListening();
        }
    }
}
