package com.example.studygroup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CoursesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    String[] strings = {"1", "2", "3", "4", "5", "6", "7"};

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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Courses").addValueEventListener(new ValueEventListener() {
            ArrayList<Course> m = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {                  // works!
                    String faculty = child.child("faculty").getValue().toString();
                    String id = child.child("id").getValue().toString();
                    String name = child.child("name").getValue().toString();

                    m.add(new Course(faculty, id, name));
                }
                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.allCoursesRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(getContext(), m);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
