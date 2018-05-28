package com.example.studygroup;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
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

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        final Context currentContext = this;

        String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String location = getIntent().getExtras().getString("groupLocation");
        final String groupID = getIntent().getExtras().getString("groupID");
        final Integer numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        final String adminID = getIntent().getExtras().getString("adminID");

        TextView subjectTV = findViewById(R.id.SubjectInGroupContent);
        TextView dateTV = findViewById(R.id.DateInGroupContent);
        TextView locationTV = findViewById(R.id.LocationInGroupContent);
        TextView currentNumOfParticipants = findViewById(R.id.groupParticipants);
        Button joinRequest = findViewById(R.id.Request);
        Button interestedButton = findViewById(R.id.Interested);

        joinRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String userID = Profile.getCurrentProfile().getId();
                String userName = Profile.getCurrentProfile().getName();
                database.child("Users").child(userID).child("Requests").child(groupID).setValue(adminID);
                database.child("Groups").child(groupID).child("Requests").child(userID).setValue(userName);
            }
        });

        dateTV.setText(date);
        subjectTV.setText(subject);
        locationTV.setText(location);
        currentNumOfParticipants.setText(numOfParticipants.toString() + " Participants");

        final ArrayList<String> participants = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecyclerView participantsRecycler = (RecyclerView)findViewById(R.id.recyclerPaticipantsGroup);
                participantsRecycler.setLayoutManager(new LinearLayoutManager(currentContext));
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    participants.add(d.getValue().toString());
                }
                groupParticipantsAdapter participantsAdapter = new groupParticipantsAdapter(participants);
                participantsRecycler.setAdapter(participantsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        interestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                final String userID = Profile.getCurrentProfile().getId();

                database.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.child("Users").child(userID).child("interested")
                                .child(groupID).setValue("");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
