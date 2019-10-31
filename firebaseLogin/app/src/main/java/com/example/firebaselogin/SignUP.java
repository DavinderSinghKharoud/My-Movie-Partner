package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUP extends AppCompatActivity {

    private EditText email, password, address;
    private Button signUP, Login;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = (EditText) findViewById(R.id.signUpEmail);
        password = (EditText) findViewById(R.id.signUpPassword);
        address = (EditText) findViewById(R.id.Address);

        signUP = (Button) findViewById(R.id.SignUP);
        Login = (Button) findViewById(R.id.logInPage);

        databaseReference = firebaseDatabase.getInstance().getReference("User");
        firebaseAuth=FirebaseAuth.getInstance();

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mEmail=email.getText().toString();
                final String mPass=password.getText().toString();
                final String mAddress=address.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(mEmail, mPass)
                        .addOnCompleteListener(SignUP.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUP.this,"auth",Toast.LENGTH_SHORT).show();
                                    User user=new User(mEmail,mPass,mAddress);

                                    FirebaseDatabase.getInstance().getReference("User")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(SignUP.this,"all details saved",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {

                                }

                                // ...
                            }
                        });
            }
        });

    }
}