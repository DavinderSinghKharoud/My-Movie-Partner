package com.example.mymoviepartner;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Manage_Account extends Fragment {

    //creating variables
    private EditText mName, mEmail, mPassword;//mConfirmPassword;
    private Button mUpdate_information;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference userDetails;
    private FirebaseUser currentUser;
    private RadioGroup mARadioGroup;
    private RadioButton mRadioButton;
    private String Gender;
    private View view;

    public Manage_Account() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage__account, container, false);

        //Referencing variables
        mName = (EditText) view.findViewById(R.id.edit_Name);
        mEmail = (EditText) view.findViewById(R.id.edit_email);
        mPassword = (EditText) view.findViewById(R.id.edit_password);
        //mConfirmPassword=(EditText)view.findViewById(R.id.edit_confirm_password);
        mUpdate_information = view.findViewById(R.id.update_information);
        mARadioGroup = (RadioGroup) view.findViewById(R.id.account_radiogroup);


        //Getting instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Getting current user logged IN, to get the email details
        currentUser = mAuth.getCurrentUser();

        //Getting the table Users
        databaseReference = firebaseDatabase.getReference("Users");

        //Getting the user details with his specific ID in the realtime database
        userDetails = databaseReference.child(mAuth.getUid());

        //Getting user details from the database
        getUserDetails();


        //Updating user details
        mUpdate_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                mUpdate_information.startAnimation(animation);

                updateUserInformation();
                /*Toast.makeText(getContext(),"Information updated",
                        Toast.LENGTH_SHORT).show();*/
            }
        });


        return view;
    }

    /**
     * updating user details
     */
    private void updateUserInformation() {

        if (currentUser == null) {
            return;
        }
        try {
            //Setting up radio button
            int selectedGender = mARadioGroup.getCheckedRadioButtonId();
            mRadioButton = (RadioButton) view.findViewById(selectedGender);

            //Getting values from views
            String Name = mName.getText().toString();
            String password = mPassword.getText().toString();
            //String confirmPassword=mConfirmPassword.getText().toString();
            String email = mEmail.getText().toString();
            //Getting selected gender
            Gender = mRadioButton.getText().toString();

            //Updating details in the database
            try {
                userDetails.child("name").setValue(Name);
                userDetails.child("gender").setValue(Gender);
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Checking whether email is following all the rules or not
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Please enter your e-mail", Toast.LENGTH_LONG).show();
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(getContext(), "Incorrect e-mail ID", Toast.LENGTH_LONG).show();
                return;
            }

            //updating email of current user.
            currentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Toast.makeText(getContext(),"Email updated",Toast.LENGTH_SHORT).show();

                    try {

                        Toast.makeText(getContext(), "Registration successful, please check " +
                                "your email for verification", Toast.LENGTH_LONG).show();

                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                getActivity().finish();

                            }
                        });

                    } catch (Exception e) {

                    }


                }
            });


            //Checking whether password is following all the rules or not
            if (!TextUtils.isEmpty(password)) {


                if (password.length() < 7) {
                    Toast.makeText(getContext(), "Password must be 7 characters long",
                            Toast.LENGTH_LONG).show();
                    return;
                }


                //updating current user password
                currentUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //Toast.makeText(getContext(),"password updated",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            Toast.makeText(getContext(), "Information updated",
                    Toast.LENGTH_SHORT).show();

            //Clearing the back stack of fragments
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            //redirecting to the home fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,
                            new HomeFragment()).commit();

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetching the user details from database, and setting up as default
     */
    private void getUserDetails() {


        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel mUser = dataSnapshot.getValue(userModel.class);
                //Setting name
                mName.setText(mUser.getName());

                //Setting the gender from the profile
                Gender = mUser.getGender();
                if (Gender.equals("Male")) {
                    mARadioGroup.check(R.id.edit_male);
                } else {
                    mARadioGroup.check(R.id.edit_female);
                }

                //Setting email
                mEmail.setText(currentUser.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "User data does not exist", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void onResume() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }

}
