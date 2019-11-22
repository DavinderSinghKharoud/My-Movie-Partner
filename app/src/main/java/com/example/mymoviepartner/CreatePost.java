package com.example.mymoviepartner;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.CaseMap;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePost extends Fragment {
    //creating variables
    private EditText postTitle, postDesc, postLocation;
    private TextView pageTitle;
    private Button postDate, postTime;
    private ImageButton selectMaps;
    private Button creatPost;
    private FirebaseUser currentUser;
    private DatabaseReference post_reference;
    private String timeFormatted = "";
    private String dateFormatted = "";
    final long Time = 1 * 1000;


    private Bundle data;
    private boolean checkIsEditFragment = false;
    private String specificPostId;

    private ProgressDialog progressDialog;


    public CreatePost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        //referencing the variables
        postTitle = view.findViewById(R.id.title);
        postDesc = view.findViewById(R.id.descriptionbox);
        postLocation = view.findViewById(R.id.post_location);
        postDate = (Button) view.findViewById(R.id.date);
        postTime = (Button) view.findViewById(R.id.time);
        selectMaps = view.findViewById(R.id.mapsPicture);
        creatPost = view.findViewById(R.id.postbtn);
        pageTitle = view.findViewById(R.id.textviewCP);

        progressDialog = new ProgressDialog(getContext());

        //setting up listeners
        setUpOnTouchAndOnFocusListeners();

        //Referencing Navigation View and checking navigation menu item as home
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_create_post);

        //getting bundle of data from user_Own_Posts fragment
        data = getArguments();

        //getting value from the previous fragment
        String fragmentData = data.getString("ownPosts");

        //if the value is not null, it means fragment is called from the own post section
        if (fragmentData != null) {
            //changing to true, for letting know other methods
            checkIsEditFragment = true;

            navigationView.setCheckedItem(R.id.nav_profile);

            //getting value from the previous fragment
            String title = getArguments().getString("title");
            String desc = getArguments().getString("desc");
            String location = getArguments().getString("location");
            String date = getArguments().getString("date");
            String time = getArguments().getString("time");
            specificPostId = getArguments().getString("post_id");

            //Changing title and text of the button
            pageTitle.setText("Edit Post");
            creatPost.setText("Update");

            //setting values for the specific post
            settingDefaultData(title, desc, location, date, time);


        }


        //Getting current user, who logged in
        currentUser = FirebaseAuth.getInstance().
                getCurrentUser();

        //creating node with the name as "posts" in the database
        post_reference = FirebaseDatabase.getInstance().getReference("Posts");

        selectMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMaps.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectMaps.setEnabled(true);
                    }
                }, Time);
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });


        //Setting up Date and time picker
        postDate.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                postDate.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postDate.setEnabled(true);
                    }
                }, Time);

                //Selecting date
                handleDateButton();
            }
        });

        postTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                postTime.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postTime.setEnabled(true);
                    }
                }, Time);
                //Selecting time
                handleTimeButton();
            }
        });


        //clicking create post button
        creatPost.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                creatPost.startAnimation(animation);

                //Getting values from the user
                String userid = currentUser.getUid();
                String post_title = postTitle.getText().toString();
                String post_desc = postDesc.getText().toString();
                String post_location = postLocation.getText().toString();

                if (checkIsEditFragment == true) {
                    updatePost(userid, post_title, post_desc, post_location, specificPostId);
                    return;
                }

                if (TextUtils.isEmpty(post_title)) {
                    Toast.makeText(getContext(), "Please write the title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(post_desc)) {
                    Toast.makeText(getContext(), "Please write the description", Toast.LENGTH_SHORT).show();
                    return;
                }
                //checking if the date button is clicked
                String textDate = postDate.getText().toString();
                if (textDate.equals("")) {
                    Toast.makeText(getContext(), "Please select the available date", Toast.LENGTH_SHORT).show();
                    return;
                }
                //checking if the time button is clicked

                String textTime = postTime.getText().toString();
                if (textTime.equals("")) {
                    Toast.makeText(getContext(), "Please select the available time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(post_location)) {
                    Toast.makeText(getContext(), "Please write the location", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Getting data and time selected
                String post_date = dateFormatted;
                String post_time = timeFormatted;

                //creating progress dialogue
                progressDialog.setMessage("Uploading your post");
                progressDialog.show();

                //calling method to create post in the database
                creat_post_firebase(userid, post_title, post_desc,
                        post_location, post_date, post_time);

                //progressDialog.dismiss();


            }
        });


        return view;
    }

    /**
     * Creating the new post in the database
     *
     * @param userid
     * @param post_title
     * @param post_desc
     * @param post_location
     * @param post_date
     * @param post_time
     */
    private void creat_post_firebase(String userid, String post_title, String post_desc,
                                     String post_location, String post_date, String post_time) {

        try {


            //To get the current time in milliseconds
            long current_time = System.currentTimeMillis();

            //creating post object with all the details
            PostModel new_post = new PostModel(userid, post_title, post_desc, post_location, post_date, post_time, current_time);

            //generating post id
            String post_id = post_reference.push().getKey();

            //adding the post object , inside the new post id
            post_reference.child(post_id).setValue(new_post).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Thread timer = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                progressDialog.dismiss();

                            }

                        }
                    };
                    timer.start();

                    //Clearing the back stack of fragments
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    //redirecting to the home fragment
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,
                                    new HomeFragment()).commit();
                    // Toast.makeText(getContext(), "Post created successfuly",
                    //       Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Thread timer = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                progressDialog.dismiss();

                            }

                        }
                    };
                    timer.start();
                    Toast.makeText(getContext(),
                            "Post unsuccessful", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pop up date picker
     * fetching the selected date
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH);
        final int Date = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar converting = Calendar.getInstance();
                converting.set(Calendar.YEAR, year);
                converting.set(Calendar.MONTH, month);
                converting.set(Calendar.DATE, date);

                java.util.Date convertDate = converting.getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("MMM d, yyyy");

                dateFormatted = format1.format(convertDate);
                postDate.setText(dateFormatted);
            }
        }, Year, Month, Date);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();


    }

    /**
     * pop up time picker
     * fetching the time
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleTimeButton() {

        Calendar calendar = Calendar.getInstance();
        int Hour = calendar.get(Calendar.HOUR);
        int Minute = calendar.get(Calendar.MINUTE);

        boolean is24HourFormat = DateFormat.is24HourFormat(getContext());

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {

                timeFormatted = String.format("%02d:%02d", hour, minutes);

                postTime.setText(timeFormatted);
            }
        }, Hour, Minute, false);

        timePickerDialog.show();

    }


    /**
     * Setting up the values in the text fields
     *
     * @param title
     * @param desc
     * @param location
     * @param date
     * @param time
     */
    private void settingDefaultData(String title, String desc, String location, String date, String time) {
        postTitle.setText(title);
        postDesc.setText(desc);
        postLocation.setText(location);
        postDate.setText(date);
        postTime.setText(time);

    }

    /**
     * creating the post object to save the updated details
     * passing the object to the database, which will update the details
     *
     * @param userid
     * @param post_title
     * @param post_desc
     * @param post_location
     */
    private void updatePost(String userid, String post_title, String post_desc,
                            String post_location, String post_id) {

        //To get the current time in milliseconds
        long current_time = System.currentTimeMillis();

        String post_date = postDate.getText().toString();
        String post_time = postTime.getText().toString();

        //checking all the conditions are followed or not
        if (TextUtils.isEmpty(post_title)) {
            Toast.makeText(getContext(), "Please write the title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(post_desc)) {
            Toast.makeText(getContext(), "Please write the description", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(post_location)) {
            Toast.makeText(getContext(), "Please write the location", Toast.LENGTH_SHORT).show();
            return;
        }

        //creating post object with all the details
        PostModel editPost = new PostModel(userid, post_title, post_desc, post_location, post_date, post_time, current_time);

        post_reference.child(post_id).setValue(editPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Post updated", Toast.LENGTH_SHORT).show();

                //Clearing the back stack of fragments
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                //redirecting to the home fragment
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                new HomeFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Post not updated", Toast.LENGTH_SHORT).show();
            }
        });
        ;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                postLocation.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    /**
     * setting up onTouchListeners on title,desc and location
     */
    private void setUpOnTouchAndOnFocusListeners() {
        //for post Title
        postTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                postTitle.setHint("");
                return false;
            }
        });

        postTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    postTitle.setHint("Title");
                }
            }
        });

        //for post description
        postDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                postDesc.setHint("");
                return false;
            }
        });

        postDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    postDesc.setHint("Write Description");
                }
            }
        });
        //for post Location
        postLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                postLocation.setHint("");
                return false;
            }
        });

        postLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    postLocation.setHint("Location");
                }
            }
        });
    }
}
