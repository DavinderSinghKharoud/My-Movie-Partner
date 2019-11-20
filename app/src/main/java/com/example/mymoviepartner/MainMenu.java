package com.example.mymoviepartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymoviepartner.Notification.Token;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenu extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    //Attributes for drawerLayout and actionBarDrawerToggle
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView nav_name, nav_email;
    private CircleImageView nav_profile_pic;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDetails;
    private FirebaseUser currentUser;
    private NavigationView navigationView;
    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referencing Navigation View
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //To find the header, so that we can setup name and email
        View header = navigationView.getHeaderView(0);


        //Referencing text views
        nav_name = (TextView) header.findViewById(R.id.navigation_name);
        nav_email = (TextView) header.findViewById(R.id.navigation_email);
        nav_profile_pic = (CircleImageView) header.findViewById(R.id.navigation_image);

        //Getting user and firebase data instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //set the token for the particular device
        updateToken(FirebaseInstanceId.getInstance().getToken());

        //Getting current user logged IN, to get the email details
        currentUser = mAuth.getCurrentUser();


        //Getting the user details with his specific ID in the realtime database
        userDetails = firebaseDatabase.getReference("Users").child(mAuth.getUid());


        //checking logged in user;
        checkUserLoggedIn();

        //Changing user data in the navigation bar
        changeNavigationBarData();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment(), "home").commit();

            navigationView.setCheckedItem(R.id.nav_home);

        }


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        //Setting up action bar and drawer layout
        navigationView.setNavigationItemSelectedListener(this);


        //Setting up toggle bar
        mToggle = new ActionBarDrawerToggle
                (MainMenu.this, drawerLayout, R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void updateToken(String token) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fUser.getUid()).setValue(token1);
    }

    /**
     * Fetching the data from the database and setting up in textviews
     */
    private void changeNavigationBarData() {

        userDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel mUser = dataSnapshot.getValue(userModel.class);
                //Setting name
                try {
                    nav_name.setText(mUser.getName());

                    //Setting email
                    nav_email.setText(currentUser.getEmail());


                    if (mUser.getImageURL().equals("default")) {
                        nav_profile_pic.setImageResource(R.drawable.home);
                    } else {
                        Glide.with(getApplicationContext()).load(mUser.getImageURL()).into(nav_profile_pic);
                    }
                } catch (Exception e) {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "User data does not exist", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * If user is not logged in, the login screen will open.
     */
    private void checkUserLoggedIn() {
        //If user is not logged in
        if (mAuth.getCurrentUser() == null && mAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), FirstScreen.class));
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //Clearing the back stack of fragments
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                if (!navigationView.getCheckedItem().getTitle().equals("Home")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,
                                    new HomeFragment()).commit();
                }
                break;
            case R.id.nav_create_post:
                //create post object
                CreatePost createPost = new CreatePost();
                //creating bundle and adding data
                Bundle bundle = new Bundle();
                bundle.putString("home", "home");
                createPost.setArguments(bundle);

                //Adding again the home fragment and replacing it with profile fragment
                getSupportFragmentManager().beginTransaction()
                        //.add(new HomeFragment(),"HomeFragment")
                        .addToBackStack("HomeFragment")
                        .replace(R.id.fragment_container,
                                createPost, TAG_FRAGMENT).commit();
                break;
            case R.id.nav_messages:

                //Adding again the home fragment and replacing it with message fragment
                getSupportFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.fragment_container,
                                new Friends_list()).commit();

                break;
            case R.id.nav_profile:
                String checkedItem = (String) navigationView.getCheckedItem().getTitle();
                if (!checkedItem.equals("Profile")) {

                    Profile profile = new Profile();

                    //Adding again the home fragment and replacing it with profile fragment
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container,
                                    profile).commit();
                }

                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Checkout our MyMoviePartner application from the GitHub: \n https://github.com/DavinderSinghKharoud/My-Movie-Partner";
                String shareSubject = "Checkout our MyMoviePartner application from the GitHub";

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
            case R.id.nav_logout:
                //sign Out current user
                mAuth.signOut();
                finish();

                //open the login screen
                startActivity(new Intent(getApplicationContext(), FirstScreen.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        //  final Myfragment fragment = (Myfragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        super.onBackPressed();

    }

    /**
     * changing status by passing string
     * @param status
     */
    private void status(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_status", status);

        userDetails.updateChildren(hashMap);
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(currentUser.getUid());
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        currentUser("none");
    }


}
