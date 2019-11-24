package com.example.mymoviepartner;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private TextView manage_account, profile_pic_page, delete_account, own_posts;
    private FirebaseUser fUser;
    private DatabaseReference userReference;
    private StorageReference photo_ref;
    private DatabaseReference posts_ref;
    private DatabaseReference message_rooms;
    private DatabaseReference mMessages;


    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;

    private ProgressDialog progressDialog;


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Referencing Navigation View and checking navigation menu item as home
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_profile);


        //Referencing variables
        manage_account = view.findViewById(R.id.manage_account_TV);
        profile_pic_page = view.findViewById(R.id.choose_profile_picture);
        delete_account = view.findViewById(R.id.delete_account_button);
        own_posts = view.findViewById(R.id.your_posts);

        //get current user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        photo_ref = FirebaseStorage.getInstance().getReference("uploads").child(fUser.getUid() + ".jpg");
        posts_ref = FirebaseDatabase.getInstance().getReference("Posts");
        message_rooms = FirebaseDatabase.getInstance().getReference("MessageRooms");
        mMessages = FirebaseDatabase.getInstance().getReference("Messages");

        progressDialog = new ProgressDialog(getContext());


        manage_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                manage_account.startAnimation(animation);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                new Manage_Account()).addToBackStack(null).commit();
            }
        });

        profile_pic_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                profile_pic_page.startAnimation(animation);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                new Upload_Image()).addToBackStack(null).commit();
            }
        });


        own_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                own_posts.startAnimation(animation);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                new User_Own_Posts()).addToBackStack(null).commit();
            }
        });
        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                delete_account.startAnimation(animation);

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

                        dialog.dismiss();
                        //creating progress dialogue
                        progressDialog.setMessage("Deleting your account...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        final String userID = fUser.getUid();

                        deleteUserDetails(userID);


                    }
                });
/*
                fUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Account Deleted",
                                Toast.LENGTH_SHORT).show();




                        //removing data from the database
                        userReference.removeValue();
                        //removing image from the database
                        photo_ref.delete();

                        //remove the posts created by the user
                        Query query=posts_ref.orderByChild("user_id").equalTo(fUser.getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                    snapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Intent intent=new Intent(getActivity(),FirstScreen.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                fUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else{
                            Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
            }
        });
        return view;

    }

    private void deleteUserDetails(String userID) {
        //removing data from the database
        userReference.removeValue();
        //removing image from the database
        photo_ref.delete();

        //remove the posts created by the user
        Query query = posts_ref.orderByChild("user_id").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
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


        Query mRooms = message_rooms.orderByChild("user1").equalTo(userID);
        mRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
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

        Query mRooms1 = message_rooms.orderByChild("user2").equalTo(userID);
        mRooms1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
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

        Query messageQuery = mMessages.orderByChild("senderID").equalTo(userID);
        messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query messageQuery1 = mMessages.orderByChild("receiverID").equalTo(userID);
        messageQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Account Deleted",
                        Toast.LENGTH_SHORT).show();


                progressDialog.dismiss();


                // deleteUserDetails(userID);
                Intent intent = new Intent(getActivity(), FirstScreen.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public void onResume() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }
}
