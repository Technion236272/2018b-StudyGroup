package com.example.studygroup;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class GroupsInACourseActivity extends AppCompatActivity {

    private ArrayList<Group> groups;
    GroupCardsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_in_acourse);

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
        // here --> get all groups from firebase, according to the course.
        Group g = new Group("CS", "Adham-Saif", "Android Project Test", "23/05/18",
                5, 2, "AdminToken", new ArrayList<User>(), 236503);
        groups.add(g);
        // doesn't count (the two lines above, just test)

        adapter = new GroupCardsViewAdapter(groups);
        recyclerView.setAdapter(adapter);

    }

}
