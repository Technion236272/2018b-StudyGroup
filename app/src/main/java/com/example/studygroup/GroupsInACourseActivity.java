package com.example.studygroup;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsInACourseActivity extends AppCompatActivity {

    private ArrayList<Group> groups;
    private GroupCardsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_in_acourse);

        final String courseId = getIntent().getExtras().getString("courseId");
        final String courseName = getIntent().getExtras().getString("courseName");
        setTitle(courseId + " - " + courseName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createGroupFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsInACourseActivity.this, CreateGroup.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("courseName", courseName);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.GroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groups = new ArrayList<>();
        final Context context = this;

        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();
        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyDatabaseUtil my1 = new MyDatabaseUtil();
          //      int i = 0;
                for(DataSnapshot data : dataSnapshot.getChildren()) {
             //       String groupID = data.getKey();
                    Group g = data.getValue(Group.class);
                    if(g.getId().equals(courseId) && !groups.contains(g)) {
                        groups.add(g);
                    }
                }
                TextView noGroups = findViewById(R.id.noGroupsView);
                if(groups.isEmpty()) {
                    noGroups.setText(R.string.no_active_groups);
                } else {
                    noGroups.setText("");
                    RecyclerView recyclerView = findViewById(R.id.GroupsRecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    adapter = new GroupCardsViewAdapter(groups);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("Groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

