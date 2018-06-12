package study.group.Courses;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import study.group.R;
import study.group.Utilities.Course;
import study.group.Utilities.MyDatabaseUtil;

public class CoursesFragment extends Fragment {
    ArrayList<Course> favouriteCourses, otherCourses;
    TreeMap<String, Course> favouriteCoursesMap, otherCoursesMap;
    TreeMap<String, Course> lastFavouriteCoursesMap, lastOtherCoursesMap;
    Spinner facultiesSpinner;

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

    /*
     * A method for filtering by faculty. The user can choose a faculty from a faculties spinner,
     * as a result, he will get a list of the chosen faculty courses.
     */
    public void filterByFaculty(String faculty) {
        TreeMap<String, Course> filteredFavouritesMap = new TreeMap<>();
        TreeMap<String, Course> filteredOthersMap = new TreeMap<>();
        for (Map.Entry<String, Course> entry : favouriteCoursesMap.entrySet()) {
            Course c = entry.getValue();
            if (c.getFaculty().contains(faculty)) {
                filteredFavouritesMap.put(c.getId(), c);
            }
        }
        for (Map.Entry<String, Course> entry : otherCoursesMap.entrySet()) {
            Course c = entry.getValue();
            if (c.getFaculty().contains(faculty)) {
                filteredOthersMap.put(c.getId(), c);
            }
        }
        lastFavouriteCoursesMap = filteredFavouritesMap;
        lastOtherCoursesMap = filteredOthersMap;
        searchAdapter.filter(filteredFavouritesMap, filteredOthersMap);
        recyclerView.setAdapter(searchAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_main, menu);
        MenuItem item = menu.findItem(R.id.search_main);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(lastQuery, true);
                recyclerView.setAdapter(searchAdapter);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                TreeMap<String, Course> filteredFavouritesMap = new TreeMap<>();
                TreeMap<String, Course> filteredOthersMap = new TreeMap<>();
                for (Map.Entry<String, Course> entry : lastFavouriteCoursesMap.entrySet()) {
                    Course c = entry.getValue();
                    StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
                    if (sb.toString().toLowerCase().contains(newText.toLowerCase()) || c.getFaculty().contains(newText)) {
                        filteredFavouritesMap.put(c.getId(), c);
                    }
                }
                for (Map.Entry<String, Course> entry : lastOtherCoursesMap.entrySet()) {
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
                coursesAdapter.filter(lastFavouriteCoursesMap,lastOtherCoursesMap);
                recyclerView.setAdapter(coursesAdapter);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);
        final Context currentContext = getContext();
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> userFavouriteCourses = new ArrayList<>();
        favouriteCourses = new ArrayList<>();
        otherCourses = new ArrayList<>();
        otherCoursesMap = new TreeMap<>();
        favouriteCoursesMap = new TreeMap<>();
        facultiesSpinner = view.findViewById(R.id.faculties_spinner);
        recyclerView = view.findViewById(R.id.allCoursesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final String[] faculties = new String[]{"כל הקורסים", "מדעי המחשב", "הנדסת חשמל", "הנדסת מכונות", "פיזיקה", "מתמטיקה",
                "ביולוגיה", "כימיה", "הנדסה אזרחית וסביבתית", "הנדסת תעשיה וניהול", "הנדסה כימית", "הנדסה ביורפואית",
                "הנדסת ביוטכנולוגיה ומזון", "רפואה", "אנרגיה", "ארכיטקטורה ובינוי ערים", "הנדסת אוירונוטיקה וחלל",
                "לימודים הומניסטיים ואמנויות", "ננומדעים וננוטכנולוגיה", "חינוך למדע וטכנולוגיה", "חינוך גופני"};
        final List<String> facultiesList = new ArrayList<>(Arrays.asList(faculties));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentContext, android.R.layout.simple_spinner_item, facultiesList) {
            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
//                if (position == 0) {
//                    tv.setTextColor(Color.GRAY);
//                } else {
//                    tv.setTextColor(Color.BLACK);
//                }
                return view;
            }
        };
        facultiesSpinner.setAdapter(adapter);
        /*
         * Filter by Faculty Spinner.
         * Courses will be filtered by the chosen faculty.
         */
        facultiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    lastFavouriteCoursesMap = favouriteCoursesMap;
                    lastOtherCoursesMap = otherCoursesMap;
                    recyclerView.setAdapter(coursesAdapter);
                } else {
                    filterByFaculty(facultiesList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                        lastFavouriteCoursesMap = favouriteCoursesMap;
                        lastOtherCoursesMap = otherCoursesMap;
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
