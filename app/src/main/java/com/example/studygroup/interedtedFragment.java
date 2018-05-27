package com.example.studygroup;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class interedtedFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Map<String, Group> interestedGroups;
    private static userInformationAboutGroupsAdapter adapter;
    private RecyclerView recyclerView;

    public interedtedFragment() {
        // Required empty public constructor
    }

    public static interedtedFragment newInstance() {
        return new interedtedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_interested, container, false);
        final ArrayList<String> temp = new ArrayList<>();
        interestedGroups = new HashMap<>();
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();
        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("interested").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    temp.add(d.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    if(temp.contains(child.getKey())) {
                        Group g = child.getValue(Group.class);
                        g.setGroupID(child.getKey());
                        interestedGroups.put(child.getKey(), g);
                    }
                }
                recyclerView = view.findViewById(R.id.interestedGroupsRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new userInformationAboutGroupsAdapter(interestedGroups);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return inflater.inflate(R.layout.fragment_interested, container, false);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
