package com.example.studygroup;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoursesFragment extends Fragment {

    private Map<Integer, Course> allCourses;
    private static MyItemRecyclerViewAdapter adapter;
    private static String lastQuery = "";
    private static MyItemRecyclerViewAdapter lastAdapter;
    private RecyclerView recyclerView;

    private RecyclerView favouriteRecyclerView;
    private Map<Integer, Course> favourite;
    private static MyItemRecyclerViewAdapter adapter1;

    public CoursesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQuery(lastQuery, true);
        recyclerView.setAdapter(lastAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Map<Integer, Course> filteredList = new HashMap<>();
                int i = 0;
                for (Map.Entry<Integer, Course> course : allCourses.entrySet()) {
                    Course c = course.getValue();
                    StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
                    if (sb.toString().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.put(i++, c);
                    }
                }
                lastQuery = newText;
                lastAdapter.filterList(filteredList);
                recyclerView.setAdapter(lastAdapter);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);
        allCourses = new HashMap<>();

        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

        favourite = new HashMap<>();
        final ArrayList<String> temp = new ArrayList<>();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("Users").child(Profile.getCurrentProfile().getId())
                .child("FavouriteCourses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String current;
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    current = (String)child.getValue();
                    temp.add(current);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        database.child("Courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                int j = 0;
                recyclerView = view.findViewById(R.id.allCoursesRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                favouriteRecyclerView = view.findViewById(R.id.favouriteCoursesRecyclerView);
                favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String faculty = (String)child.child("faculty").getValue();
                    String id = (String)child.child("id").getValue();
                    String name = (String)child.child("name").getValue();
                    if(temp.contains(id)) {
                        favourite.put(j++, new Course(faculty, id, name));
                    } else {
                        allCourses.put(i++, new Course(faculty, id, name));
                    }
                }
                adapter = new MyItemRecyclerViewAdapter(allCourses);
                lastAdapter = new MyItemRecyclerViewAdapter(allCourses);
                recyclerView.setAdapter(adapter);
                adapter1 = new MyItemRecyclerViewAdapter(favourite);
                favouriteRecyclerView.setAdapter(adapter1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }


}
