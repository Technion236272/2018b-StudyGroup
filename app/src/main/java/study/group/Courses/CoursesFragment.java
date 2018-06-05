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
import java.util.Map;
import java.util.TreeMap;

import study.group.R;
import study.group.Utilities.Course;
import study.group.Utilities.MyDatabaseUtil;

public class CoursesFragment extends Fragment {
    ArrayList<Course> favouriteCourses, otherCourses;
    TreeMap<String, Course> favouriteCoursesMap, otherCoursesMap;
    CourseRecyclerViewAdapter coursesAdapter, searchAdapter;
    private RecyclerView recyclerView;
    private String lastQuery = "";

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
        inflater.inflate(R.menu.search_main, menu);
        MenuItem item = menu.findItem(R.id.search_main);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQuery(lastQuery, true);
        recyclerView.setAdapter(searchAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
            //    Toast.makeText(getContext(), "Courses", Toast.LENGTH_SHORT).show();
                TreeMap<String, Course> filteredFavouritesMap = new TreeMap<>();
                TreeMap<String, Course> filteredOthersMap = new TreeMap<>();
                for (Map.Entry<String, Course> entry : favouriteCoursesMap.entrySet()) {
                    Course c = entry.getValue();
                    StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
                    if (sb.toString().toLowerCase().contains(newText.toLowerCase()) || c.getFaculty().contains(newText)) {
                        filteredFavouritesMap.put(c.getId(), c);
                    }
                }
                for (Map.Entry<String, Course> entry : otherCoursesMap.entrySet()) {
                    Course c = entry.getValue();
                    StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
                    if (sb.toString().toLowerCase().contains(newText.toLowerCase()) ||
                            c.getFaculty().contains(newText)) {
                        filteredOthersMap.put(c.getId(), c);
                    }
                }

                lastQuery = newText;
                searchAdapter.filter(filteredFavouritesMap, filteredOthersMap);
                recyclerView.setAdapter(searchAdapter);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setAdapter(coursesAdapter);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> userFavouriteCourses = new ArrayList<>();
        favouriteCourses = new ArrayList<>();
        otherCourses = new ArrayList<>();
        otherCoursesMap = new TreeMap<>();
        favouriteCoursesMap = new TreeMap<>();
        recyclerView = view.findViewById(R.id.allCoursesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*
         * search for the user favourite courses, in order to show them as favourites in the recyclerView.
         */
        database.child("Users").child(Profile.getCurrentProfile().getId())
                .child("FavouriteCourses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String current;
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    current = (String)child.getValue();
                    userFavouriteCourses.add(current);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /*
         * Load all the courses from the DB onto a recyclerView. followed courses will be displayed first.
         */
        database.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                database.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int technionIndex = 0;
                        for (DataSnapshot course : dataSnapshot.getChildren()) {
                            String faculty = (String) course.child("faculty").getValue();
                            String id = (String) course.child("id").getValue();
                            String name = (String) course.child("name").getValue();
                            Course c = new Course(faculty, id, name, technionIndex++);
                            if (userFavouriteCourses.contains(id)) {
                                c.setFav(true);
                                favouriteCourses.add(c);
                                favouriteCoursesMap.put(id, c);
                            } else {
                                otherCourses.add(c);
                                otherCoursesMap.put(id, c);

                            }
                        }
                        coursesAdapter = new CourseRecyclerViewAdapter(favouriteCoursesMap, otherCoursesMap, null);
                        searchAdapter = new CourseRecyclerViewAdapter(favouriteCoursesMap, otherCoursesMap, coursesAdapter);
                        recyclerView.setAdapter(coursesAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }
}
