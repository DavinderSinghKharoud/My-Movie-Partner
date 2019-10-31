package com.example.mymoviepartner;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword extends Fragment {
    private Button sendPassword;
    private EditText enter_email;
    private Button goback;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    public ForgotPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) view.findViewById(R.id.forgot_progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        //getting reference
        sendPassword = (Button) view.findViewById(R.id.passwordBtn_forgotPassword);
        enter_email = (EditText) view.findViewById(R.id.email_forgotPassword);
        goback = (Button)view. findViewById(R.id.forgot_goback);

        sendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                sendPassword.startAnimation(animation);

                String emailInfo = enter_email.getText().toString();
                if(TextUtils.isEmpty(emailInfo)){
                    Toast.makeText(getContext(),
                            "Please enter your email ID",Toast.LENGTH_SHORT)
                            .show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.sendPasswordResetEmail(enter_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(),"Link is sent to your email",Toast.LENGTH_LONG).show();
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                goback.startAnimation(animation);
                //Replacing fragment with the login as a first page
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainActivity_layout,new Login())
                        .commit();
            }
        });
        return view;
    }
    public void onResume() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }
}
