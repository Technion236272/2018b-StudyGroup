package study.group.Courses;

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

import study.group.R;
import study.group.Utilities.Course;
import study.group.Utilities.MyDatabaseUtil;

public class CoursesFragment extends Fragment {
    private ArrayList<Course> allCoursesList;
    private static CourseRecyclerViewAdapter adapter;
    private static String lastQuery = "";
    private static CourseRecyclerViewAdapter lastAdapter;
    private RecyclerView recyclerView;
    private static int favouritesCount = 0;
    private RecyclerView favouriteRecyclerView;

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
        inflater.inflate(R.menu.course_menu, menu);
        MenuItem item = menu.findItem(R.id.search_course);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQuery(lastQuery, true);
        recyclerView.setAdapter(lastAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<Course> filteredList = new ArrayList<>();
                int i = 0;
                int favouritesCountInFilteredAdapter = 0;
                for (Course c: allCoursesList) {
                    StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
                    if (sb.toString().toLowerCase().contains(newText.toLowerCase())) {
                        if (c.isFav()) {
                            filteredList.add(0, c);
                        } else {
                            filteredList.add(c);
                        }
                        c.indexInFilteredAdapter = i++;
                    }
                }

                lastQuery = newText;
                lastAdapter.filterList(filteredList,favouritesCountInFilteredAdapter);
                recyclerView.setAdapter(lastAdapter);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);
        allCoursesList =  new ArrayList<>();
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

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

        database.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allCoursesList = new ArrayList<>();
                int i = 0, j = temp.size();
                recyclerView = view.findViewById(R.id.allCoursesRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                favouriteRecyclerView = view.findViewById(R.id.favouriteCoursesRecyclerView);
                favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String faculty = (String)child.child("faculty").getValue();
                    String id = (String)child.child("id").getValue();
                    String name = (String)child.child("name").getValue();

                    if(temp.contains(id)) {
                        allCoursesList.add(0,new Course(faculty,id,name,true,i++));
                        favouritesCount++;
                    } else {
                        allCoursesList.add(new Course(faculty,id,name,false,i++));
                    }
                }

                adapter = new CourseRecyclerViewAdapter(allCoursesList, favouritesCount, favouritesCount, null);
                lastAdapter = new CourseRecyclerViewAdapter(allCoursesList, favouritesCount, favouritesCount, adapter);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;
    }


}
