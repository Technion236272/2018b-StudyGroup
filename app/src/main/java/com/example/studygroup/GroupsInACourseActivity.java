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
        setTitle(courseId + " - " + courseName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createGroupFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsInACourseActivity.this, CreateGroup.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.GroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groups = new ArrayList<>();
        final Context context = this;

        MyDatabaseUtil my = new MyDatabaseUtil();
        my.getDatabase();
        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyDatabaseUtil my1 = new MyDatabaseUtil();
                int i = 0;
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getKey().contains(courseId)) {
                        Group newGroup = new Group();
                        newGroup.setId(data.getValue(Group.class).getId());
                        newGroup.setSubject(data.getValue(Group.class).getSubject());
                        newGroup.setDate(data.getValue(Group.class).getDate());
                        newGroup.setLocation(data.getValue(Group.class).getLocation());
                        newGroup.setmaxNumOfPart(data.getValue(Group.class).getmaxNumOfPart());
                        newGroup.setCurrentNumOfPart(data.getValue(Group.class).getCurrentNumOfPart());
                        newGroup.setAdminID(data.getValue(Group.class).getAdminID());
                        groups.add(newGroup);
                    }
                }
                if(groups.isEmpty()) {
                    TextView noGroups = (TextView) findViewById(R.id.noGroupsView);
                    noGroups.setText(R.string.no_active_groups);
                } else {
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.GroupsRecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    adapter = new GroupCardsViewAdapter(groups);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}

