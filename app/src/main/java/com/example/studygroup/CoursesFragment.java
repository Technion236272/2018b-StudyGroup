package com.example.studygroup;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CoursesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Map<Integer, Course> allCourses;
    private MyItemRecyclerViewAdapter adapter;

    public CoursesFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);
        allCourses = new HashMap<>();
        EditText editText = (EditText) view.findViewById(R.id.editText11);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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
                adapter = new MyItemRecyclerViewAdapter(getContext(), allCourses);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;

    }

    private void filter(String text) {
        Map<Integer, Course> filteredList = new HashMap<>();
        int i = 0;
        for(Map.Entry<Integer, Course> course : allCourses.entrySet()) {
            Course c = course.getValue();
            StringBuilder sb = new StringBuilder(c.getId()).append(" - ").append(c.getName());
            if(sb.toString().toLowerCase().contains(text.toLowerCase())) {
                filteredList.put(i++, c);
            }
        }
        adapter.filterList(filteredList);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
