package com.example.mymoviepartner;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mymoviepartner.ViewHolders.Post_ViewHolder;
import com.example.mymoviepartner.ViewHolders.allPosts_Adapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //creating variables
    private RecyclerView recyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseRecyclerOptions<PostModel> options;
    private FirebaseRecyclerAdapter<PostModel, Post_ViewHolder> mFirebaseAdapter;
    //creating string, which will contain the posted time
    private String TimePostedOn = "Posted: ";
    private LinearLayoutManager mLayoutManager;
    private Spinner spinner;

    //second
    private allPosts_Adapter allPosts_adapter;
    private ArrayList<PostModel> listPost = new ArrayList<>();
    private ArrayList<PostModel> copyList;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //referencing recycler view
        recyclerView = view.findViewById(R.id.my_recycler_view);

        //setting it to true, to setup the custom menu to the application
        setHasOptionsMenu(true);

        //setting up spinner(Drop down menu)
        setUpSpinner(view);

        //Referencing Navigation View and checking navigation menu item as home
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);


        //getting reference from the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Posts");


        //fetching data from the database
        fetchData();

        //setting up recycler view
        setUpRecyclerView();

        return view;
    }



    /**
     * fetching data from the firebase and adding to the list
     */
    private void fetchData() {


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the list
                listPost.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    PostModel postModel = postSnapshot.getValue(PostModel.class);
                    listPost.add(postModel);
                }
                copyList = new ArrayList<>(listPost);
                allPosts_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * setting up recycler view
     */
    private  void setUpRecyclerView(){
        //referencing recyclerView and setting fixed size
        recyclerView.setHasFixedSize(true);

        //setting up the layout manager and setting the latest post added at the top
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);


        allPosts_adapter = new allPosts_Adapter(listPost, getContext(), isAdded());

        //setting layout manager
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(allPosts_adapter);


    }

    /**
     * setting up spinner
     * @param view
     */
    private void setUpSpinner(View view){

        //getting reference
        spinner = view.findViewById(R.id.spinner1);

        //setting up adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray((R.array.names)));

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter
        spinner.setAdapter(arrayAdapter);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //inflating the search field
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        android.widget.SearchView searchView = (android.widget.SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //getting drop down item selected
                Integer index = spinner.getSelectedItemPosition();


                if (index == 1) {
                    //By userName
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getUserNameFilter().filter(query);
                }
                else if (index == 2) {
                    //By location
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getLocationFilter().filter(query);

                } else if (index == 3) {
                    //By Gender
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getGenderFilter().filter(query);
                } else {
                    //By default(title)
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getFilter().filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //getting drop down item selected
                Integer index = spinner.getSelectedItemPosition();


                if (index == 1) {
                    //By userName
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getUserNameFilter().filter(newText);
                }
                else if (index == 2) {
                    //By location
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getLocationFilter().filter(newText);

                } else if (index == 3) {
                    //By Gender
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getGenderFilter().filter(newText);
                } else {
                    //By default(title)
                    allPosts_adapter.setmPostListFull(copyList);
                    allPosts_adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }


    //private void displayData
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
