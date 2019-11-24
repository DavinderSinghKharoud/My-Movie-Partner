package com.example.mymoviepartner;


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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    //Creating Attributes
    private TextView register;
    private EditText email_edt, pass_edt;
    private Button login;
    private TextView forgot;

    private ProgressBar progressBar;

    //Creating attribute for firebase
    private FirebaseAuth mAuth;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Referencing the attributes
        register = (TextView) view.findViewById(R.id.goToRegister);
        email_edt = (EditText) view.findViewById(R.id.loginEmail);
        pass_edt = (EditText) view.findViewById(R.id.loginPassword);
        login = (Button) view.findViewById(R.id.loginBtn_id);
        forgot = (TextView) view.findViewById(R.id.forgotPasswordBtn);

        //setting up listeners
        setUpOnTouchAndOnFocusListeners();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Clearing the back stack of fragments
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //If user is already logged in, the main menu will open
        //Replacing Fragment coantainer with main menu fragment
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) { //user is logged in
            // FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            //   fragmentTransaction.replace(R.id.mainActivity_layout, new MainMenu()).commit();

            Intent intent = new Intent(getActivity(), MainMenu.class);
            startActivity(intent);
            getActivity().finish();
        }


        //Setting up register on click button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Replacing fragment container with register fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainActivity_layout, new Register())
                        .addToBackStack(null).commit();
            }
        });


        //Setting up login on click button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                login.startAnimation(animation);

                //Getting the value from email and password editText field
                String emailInfo = email_edt.getText().toString();
                String passInfo = pass_edt.getText().toString();

                //Checking if email field is empty
                if (TextUtils.isEmpty(emailInfo)) {
                    Toast.makeText(getContext(), "Please enter your e-mail", Toast.LENGTH_LONG).show();
                    return;
                }

                //Checking if password field is empty
                if (TextUtils.isEmpty(passInfo)) {
                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //Verifying if user exist in the database
                verifyLoginCredentials(emailInfo, passInfo);

            }
        });

        //Setting up forgot on click button
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Replacing frament container with the forgt password layout
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainActivity_layout, new ForgotPassword())
                        .addToBackStack(null).commit();
            }
        });

        return view;
    }

    /**
     * Method verifying if user exist in the database.
     *
     * @param email    {Taking String as a input}
     * @param password {Taking password as a input}
     */

    private void verifyLoginCredentials(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (mAuth.getCurrentUser().isEmailVerified()){

                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(getActivity(), MainMenu.class);
                                startActivity(intent);
                                getActivity().finish();
                            }else{

                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Plesase, verify your email address", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    public void onResume() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }

    /**
     * setting up onTouchListeners on title,desc and location
     */
    private void setUpOnTouchAndOnFocusListeners() {
        //for email
        email_edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                email_edt.setHint("");
                return false;
            }
        });

        email_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    email_edt.setHint("Enter your email");
                }
            }
        });

        //for password
        pass_edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pass_edt.setHint("");
                return false;
            }
        });

        pass_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    pass_edt.setHint("Enter your password");
                }
            }
        });
    }
}

