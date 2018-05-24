package com.example.studygroup;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsInACourseActivity extends AppCompatActivity {

    private ArrayList<Group> groups;
    private GroupCardsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_in_acourse);

        final String courseId = getIntent().getExtras().getString("courseId");
        String courseName = getIntent().getExtras().getString("courseName");
        StringBuilder idName = new StringBuilder(courseId).append(" - ").append(courseName);
        setTitle(idName.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createGroupFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsInACourseActivity.this, CreateGroup.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.GroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groups = new ArrayList<>();
        final Context context = this;

        MyDatabaseUtil my = new MyDatabaseUtil();
        my.getDatabase();
   //     FirebaseDatabase.getInstance().setPersistenceEnabled(true);     //
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey().contains(courseId)) {
                        String id = child.child("Course Number").getValue().toString();
                        String subject = child.child("Subject").getValue().toString();
                        String date = child.child("Date").getValue().toString();
                        Integer max = Integer.parseInt(child.child("Number of participants").getValue().toString());
                        Integer current = Integer.parseInt(child.child("current number").getValue().toString());
                        groups.add(new Group(id, subject, date, max, current));
                    }
                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.GroupsRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                adapter = new GroupCardsViewAdapter(groups);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}

