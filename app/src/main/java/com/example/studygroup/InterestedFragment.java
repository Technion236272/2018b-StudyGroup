package com.example.studygroup;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InterestedFragment extends Fragment {
    private userInformationAboutGroupsAdapter adapter;
    private RecyclerView recyclerView;

//    private static userInformationAboutGroupsAdapter lastAdapter;
//    private static String lastQuery = "";

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.options_menu, menu);
//        MenuItem item = menu.findItem(R.id.searchGroup);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setQuery(lastQuery, true);
//        recyclerView.setAdapter(lastAdapter);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                ArrayList<Group> filteredList = new ArrayList<>();
//                for (Group g: interestedGroups) {
//                    if(g.getSubject().contains(newText)){
//                        filteredList.add(g);
//                    }
//                }
//                lastQuery = newText;
//                lastAdapter.filterList(filteredList);
//                recyclerView.setAdapter(lastAdapter);
//                return false;
//            }
//        });
//        super.onCreateOptionsMenu(menu, inflater);
//    }


    public InterestedFragment() {
        // Required empty public constructor
    }

    public static InterestedFragment newInstance() {
        return new InterestedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_interested, container, false);

        recyclerView = view.findViewById(R.id.interestedGroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

        final ArrayList<String> temp = new ArrayList<>();

        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("interested").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> tempArray = new ArrayList<>();
//                final ArrayList<Group> newInterested = new ArrayList<>();
                final Set<Group> tmpInterested = new HashSet<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    tempArray.add(d.getKey());
//                    if(temp.contains(d.getKey())){
//                        continue;
//                    }
//                    temp.add(d.getKey());
                }
                myRef.child("Groups").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            if(tempArray.contains(child.getKey())) {
                                Group g = child.getValue(Group.class);
//                                newInterested.add(g);
                                tmpInterested.add(g);
                            }
                        }
                        adapter = new userInformationAboutGroupsAdapter(new ArrayList<Group>(tmpInterested));
                        recyclerView.setAdapter(adapter);
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

//        myRef.child("Groups").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot child : dataSnapshot.getChildren()) {
//                    if(interestedGroups.contains(child.getValue(Group.class))){
//                        continue;
//                    }
//                    if(temp.contains(child.getKey())) {
//                        Group g = child.getValue(Group.class);
//                        interestedGroups.add(g);
//                    }
//                }
//                adapter = new userInformationAboutGroupsAdapter(interestedGroups);
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        myRef.child("Users").child(Profile.getCurrentProfile().getId())
//                .child("interested").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
////                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
