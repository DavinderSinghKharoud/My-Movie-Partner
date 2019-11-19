package com.example.mymoviepartner;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.jar.Attributes;


/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment {

    //Creating Attributes
    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword, mConfirmPassword, mName;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private Button mSignUp;
    private TextView signInHere;
    DatabaseReference myRef;


    public Register() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Refering the attributes
        mName = (EditText) view.findViewById(R.id.registerName);
        mEmail = (EditText) view.findViewById(R.id.registerEmailId);
        mPassword = (EditText) view.findViewById(R.id.registerPassword);
        mConfirmPassword = (EditText) view.findViewById(R.id.registerConfirmPassword);
        mSignUp = (Button) view.findViewById(R.id.registerSignUp);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.gender);


        //setting up listeners
        setUpOnTouchAndOnFocusListeners();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Getting Firebase Reference
        myRef = FirebaseDatabase.getInstance().getReference("Users");

        //user is logged in
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {

            Intent intent = new Intent(getActivity(), MainMenu.class);
            startActivity(intent);
        }


        //Setting up Sign up click button
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                mSignUp.startAnimation(animation);


                //Getting values of email, password, and confirm password from editText fields
                String Name = mName.getText().toString();
                String Gender = "";
                String userEmail = mEmail.getText().toString();
                String userPassword = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();

                //Checking Radio Button
                int selectedGender = mRadioGroup.getCheckedRadioButtonId();
                mRadioButton = (RadioButton) view.findViewById(selectedGender);
                Gender = mRadioButton.getText().toString();

                //Creating the new user in the database
                createUser(userEmail, userPassword, confirmPassword, Name, Gender);
            }
        });

        signInHere = (TextView) view.findViewById(R.id.signInHere_textView);

        signInHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainActivity_layout, new Login())
                        .commit();
            }
        });

        return view;
    }


    /**
     * Creating new user in the database
     *
     * @param email       {Taking string as attibute}
     * @param password    {Taking string as attibute}
     * @param confirmPass {Taking string as attibute}
     */
    private void createUser(String email, String password, String confirmPass, final String Name, final String Gender) {

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter your e-mail", Toast.LENGTH_LONG).show();
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(getContext(), "Incorrect e-mail ID", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 7) {
            Toast.makeText(getContext(), "Password must be 7 characters long", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(getContext(), "Confirm password field is empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (!confirmPass.equals(password)) {
            Toast.makeText(getContext(), "Password Fields are not same", Toast.LENGTH_LONG).show();
            return;
        }
        //creating progress dialogue
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        String ImageURL = "default";
                                        userModel mUser = new userModel(Name, Gender, ImageURL,"offline");
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(mUser);


                                        Toast.makeText(getContext(), "Registration successful, please check " +
                                                "your email for verification", Toast.LENGTH_LONG).show();

                                        // Sign in success, update UI with the signed-in user's information
                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.mainActivity_layout, new Login()).commit();
                                    }else{

                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Not successful", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onResume() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }

    /**
     * setting up onTouchListeners on title,desc and location
     */
    private void setUpOnTouchAndOnFocusListeners() {
        //for email
        mName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mName.setHint("");
                return false;
            }
        });

        mName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mName.setHint("Full name");
                }
            }
        });

        //for password
        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPassword.setHint("");
                return false;
            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mPassword.setHint("Create password");
                }
            }
        });

        //for email
        mEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEmail.setHint("");
                return false;
            }
        });

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mEmail.setHint("Enter your e-mail");
                }
            }
        });

        //for confirm password
        mConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mConfirmPassword.setHint("");
                return false;
            }
        });

        mConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mConfirmPassword.setHint("Confirm password");
                }
            }
        });
    }
}
