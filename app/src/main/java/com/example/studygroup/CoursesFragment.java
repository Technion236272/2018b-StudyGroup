package com.example.studygroup;

import android.net.Uri;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CoursesFragment extends Fragment {

    private Map<Integer, Course> allCourses;
    private MyItemRecyclerViewAdapter adapter;

    private Map<Integer, Course> favorites;
    private MyItemRecyclerViewAdapter favoritesAdapter;

    public CoursesFragment() {
    }

    public static CoursesFragment newInstance() {
        CoursesFragment fragment = new CoursesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Map<Integer, Course> filteredList = new HashMap<>();
                if(newText.length() == 0) {
                    adapter.filterList(allCourses);
                } else {
                    int i = 0;
                    for (Map.Entry<Integer, Course> course : allCourses.entrySet()) {
                        Course c = course.getValue();
                        StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
                        if (sb.toString().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.put(i++, c);
                        }
                    }
                    adapter.filterList(filteredList);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);
        allCourses = new HashMap<>();
        favorites = new HashMap<>();

        MyDatabaseUtil my = new MyDatabaseUtil();
        my.getDatabase();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String faculty = child.child("faculty").getValue().toString();
                    String id = child.child("id").getValue().toString();
                    String name = child.child("name").getValue().toString();

                    allCourses.put(i++, new Course(faculty, id, name));
                }
                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.allCoursesRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new MyItemRecyclerViewAdapter(allCourses);
                recyclerView.setAdapter(adapter);

//                iterate over the user favorite courses and add them into the favorites hashmap, and remove from allcourses    // ???
//                final RecyclerView recyclerViewFavorites = (RecyclerView) view.findViewById(R.id.favouriteCoursesRecyclerView);
//                recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
//                favoritesAdapter = new MyItemRecyclerViewAdapter(favorites);
//                recyclerViewFavorites.setAdapter(favoritesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
