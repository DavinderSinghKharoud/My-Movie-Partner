package com.example.mymoviepartner.ViewHolders;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymoviepartner.HomeFragment;
import com.example.mymoviepartner.MainMenu;
import com.example.mymoviepartner.PostModel;
import com.example.mymoviepartner.R;
import com.example.mymoviepartner.userModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class allPosts_Adapter extends RecyclerView.Adapter<allPosts_Adapter.postViewHolder> implements Filterable {

    private onClickMessageListener mListener;

    private ArrayList<PostModel> mPostList;
    private ArrayList<PostModel> mPostListFull;
    private String TimePostedOn = "Posted: ";
    private Context context;
    private boolean isAdded;
    //getting database reference
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

    //creating interface for clicking
    public interface onClickMessageListener {
        void onMessageClick(int position);

        void onItemLongClick(View view, int position);

        void onShareClick(int position);
    }

    public void setOnItemClickListener(onClickMessageListener listener) {
        mListener = listener;
    }

    public static class postViewHolder extends RecyclerView.ViewHolder {

        //Creating variables
        private View mView;
        private TextView mTitle, mDesc, mDate, mTime, mLocation, mGender, mName, mPostedTime;
        private CircleImageView circleImageView;
        private ImageView shareButton;
        private Button mMessage;
        private RelativeLayout container;
        public boolean isClickable;


        public postViewHolder(@NonNull View itemView, final onClickMessageListener listener) {
            super(itemView);

            mView = itemView;

            //Views
            container = mView.findViewById(R.id.container_allposts);
            mTitle = mView.findViewById(R.id.textView2_title);
            mDesc = mView.findViewById(R.id.textView3_description);
            mDate = mView.findViewById(R.id.textView4_date);
            mTime = mView.findViewById(R.id.textView5_time);
            mLocation = mView.findViewById(R.id.textView6_location);
            mGender = mView.findViewById(R.id.textView7_gender);
            mName = mView.findViewById(R.id.textView1_name);
            circleImageView = mView.findViewById(R.id.profile_picture_viewPost);
            mPostedTime = mView.findViewById(R.id.textView8_postedOn);
            shareButton = mView.findViewById(R.id.imageView_share);


            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {


                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            listener.onMessageClick(position);

                            //To prevent double click
                            if (isClickable) {
                                return;
                            }
                            isClickable = true;
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isClickable = false;
                                }
                            }, 500);

                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(view, position);
                        }
                    }

                    return true;
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onShareClick(position);
                        }
                    }

                }
            });

        }
    }

    @NonNull
    @Override
    public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_profile_layout, parent, false);
        postViewHolder pvh = new postViewHolder(v, mListener);
        return pvh;
    }

    /**
     * constructor to be called
     *
     * @param postList
     * @param context
     * @param isAdded
     */
    public allPosts_Adapter(ArrayList<PostModel> postList, Context context, boolean isAdded) {
        mPostList = postList;
        this.context = context;
        this.isAdded = isAdded;

    }


    @Override
    public void onBindViewHolder(@NonNull final postViewHolder holder, int position) {


        PostModel postModel = mPostList.get(position);

        holder.circleImageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_recycleview));

        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
        //Set data to views
        holder.mTitle.setText(postModel.getTitle());
        holder.mDesc.setText(postModel.getDescription());
        holder.mDate.setText("On: " + postModel.getDate());
        holder.mTime.setText("At: " + postModel.getTime());
        holder.mLocation.setText("Location: " + postModel.getLocation());


        //getting time of the post from the current time
        getPostedTime(postModel);

        //setting time
        holder.mPostedTime.setText(TimePostedOn);

        //getting user id
        final String userID = postModel.getUser_id();

        //setting user details( Names, userId)
        settingUserDetails(holder, userID);


    }


    /**
     * Filers
     */
    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public Filter getFilter() {
        return titleFilter;
    }

    public Filter getLocationFilter() {
        return locationFilter;
    }

    public Filter getGenderFilter() {
        return genderFilter;
    }

    public Filter getUserNameFilter() {
        return userNameFilter;
    }


    /**
     * creating location Filter
     */
    private Filter locationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //creating filterList
            List<PostModel> filteredList = new ArrayList<>();

            //comparing data
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mPostListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();


                for (PostModel item : mPostListFull) {
                    if (item.getLocation().toLowerCase().contains(filterPattern)) {
                        //adding to filter list
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {

                    Toast toast = Toast.makeText(context, "No Posts", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mPostList.clear();
            mPostList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    /**
     * creating Gender Filter
     */
    private Filter genderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //creating filterList
            final List<PostModel> filteredList = new ArrayList<>();

            //comparing data
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mPostListFull);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();


                final int[] index = {0};

                for (final PostModel item : mPostListFull) {
                    //getting user id
                    String userId = item.getUser_id();


                    DatabaseReference specificUser = userRef.child(userId);

                    specificUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //getting the user details and saving in the user model
                            userModel mUser = dataSnapshot.getValue(userModel.class);
                            String gender = mUser.getGender().toLowerCase().trim();
                            if (filterPattern.equals("male")) {

                                if (gender.contains(filterPattern) && !gender.equals("female")) {
                                    //adding to filter list
                                    filteredList.add(item);
                                }
                            } else {

                                if (gender.contains(filterPattern)) {
                                    //adding to filter list
                                    filteredList.add(item);
                                }

                            }


                            index[0] += 1;
                            int itemCount = mPostListFull.size();
                            if (index[0] == itemCount) {

                                if (((List) mPostList).isEmpty()) {

                                    Toast toast = Toast.makeText(context, "No Posts", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }

                            mPostList.clear();
                            mPostList.addAll(filteredList);
                            notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mPostList.clear();
            mPostList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    /**
     * creating user name Filter
     */
    private Filter userNameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //creating filterList
            final List<PostModel> filteredList = new ArrayList<>();

            //comparing data
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mPostListFull);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();

                final int[] index = {0};
                for (final PostModel item : mPostListFull) {
                    //getting user id
                    String userId = item.getUser_id();

                    try {


                        DatabaseReference specificUser = userRef.child(userId);

                        specificUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //getting the user details and saving in the user model
                                userModel mUser = dataSnapshot.getValue(userModel.class);
                                String userName = mUser.getName().toLowerCase().trim();
                                if (userName.contains(filterPattern)) {
                                    //adding to filter list
                                    filteredList.add(item);
                                }


                                index[0] += 1;
                                mPostList.clear();
                                mPostList.addAll(filteredList);
                                notifyDataSetChanged();

                                int itemCount = mPostListFull.size();
                                if (index[0] == itemCount) {

                                    if (((List) mPostList).isEmpty()) {

                                        Toast toast = Toast.makeText(context, "No Posts", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mPostList.clear();

            mPostList.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    };

    /**
     * creating title Filter
     */
    private Filter titleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //creating filterList
            List<PostModel> filteredList = new ArrayList<>();

            //comparing data
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mPostListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (PostModel item : mPostListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        //adding to filter list
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {

                    Toast toast = Toast.makeText(context, "No Posts", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            try {

                mPostList.clear();
                mPostList.addAll((List) filterResults.values);
                notifyDataSetChanged();
            } catch (Exception e) {

                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    /**
     * setting postListFUll(contain all data), which will be used as reference for searching.
     *
     * @param postList
     */
    public void setmPostListFull(ArrayList<PostModel> postList) {
        try {

            this.mPostListFull = new ArrayList<>(postList);

        } catch (Exception e) {

        }

    }

    /**
     * To get the time difference of the posts from the current time
     *
     * @param postModel
     */
    private void getPostedTime(PostModel postModel) {
        //getting the time, when the post was created
        long TimeOfPost = postModel.getTime_stamp();

        //getting the difference between the current time and the post time in milliseconds
        long msDiff = Calendar.getInstance().getTimeInMillis() - TimeOfPost;

        //getting the difference of; days between today and when the post created
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

    /**
     * getting user details and setting them in the views
     *
     * @param holder
     * @param userID
     */
    private void settingUserDetails(final postViewHolder holder, String userID) {
        //getting database reference
        DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    //getting the user details and saving in the user model
                    userModel mUser = dataSnapshot.getValue(userModel.class);

                    //setting username and gender
                    holder.mName.setText(mUser.getName());
                    holder.mGender.setText("Gender: " + mUser.getGender());

                    //getting image URL
                    String imageURl = mUser.getImageURL();
                    //setting image
                    if (isAdded)
                        if (imageURl.equals("default")) {
                            holder.circleImageView.setImageResource(R.drawable.ic_launcher_background);
                        } else {
                            Glide.with(context).load(imageURl).into(holder.circleImageView);
                        }
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }


}
