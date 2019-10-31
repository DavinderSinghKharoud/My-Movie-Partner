package com.example.mymoviepartner;


import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Upload_Image extends Fragment {

    //creating variables
    private CircleImageView circleImageView;
    private Button choose_pic;
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    public Upload_Image() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload__image, container, false);

        //Referencing the variables
        circleImageView = (CircleImageView) view.findViewById(R.id.upload_pic);
        choose_pic = (Button) view.findViewById(R.id.choose_photo_button);

        //Getting current user logged in the application
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        //Referencing to the current user details in the database
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        //Getting storage reference
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    userModel user = dataSnapshot.getValue(userModel.class);
                    try{
                        if (user.getImageURL().equals("default")) {
                            circleImageView.setImageResource(R.drawable.ic_launcher_background);
                        } else {
                            Glide.with(getContext()).load(user.getImageURL()).into(circleImageView);
                        }
                    }catch (Exception e){
                        Toast.makeText(getContext(),"No image Url",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        choose_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation
                        (getActivity(), R.anim.fadein);

                choose_pic.startAnimation(animation);

                //Selecting image from the phone
                openImage();
            }
        });

        return view;
    }

    /**
     * Sending implicit intent for uploading the image
     */
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    /**
     * Getting extension of the image
     *
     * @param uri
     * @return String as extension of the image
     */
    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    /**
     * Uploading image to the database
     */
    private void uploadImage() {

        //creating progress dialogue
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        //Changing name of the image
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(fUser.getUid() +
                    ".jpg");

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                      //  HashMap<String, Object> map = new HashMap<>();
                        //map.put("ImageURL", mUri);
                        reference.child("imageURL").setValue(mUri);

                        progressDialog.dismiss();
                //        Toast.makeText(getContext(),"Profile photo updated",Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            //      circleImageView.setImageURI(imageUri);
            //If data is selected
            uploadImage();

        }
    }
}
