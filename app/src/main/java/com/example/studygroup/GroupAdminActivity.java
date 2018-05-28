package com.example.studygroup;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.internal.on;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_admin);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final Context currentContext = this;

        String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String location = getIntent().getExtras().getString("groupLocation");
        final String groupID = getIntent().getExtras().getString("groupID");
        final Integer numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        final String adminID = getIntent().getExtras().getString("adminID");
        final String groupName = getIntent().getExtras().getString("groupName");

        final ArrayList<String> participants = new ArrayList<>();
        final ArrayList<User> requests = new ArrayList<>();

        setTitle(groupName);

        final EditText subjectET = findViewById(R.id.subjectAdminEdit);
        final EditText dateET = findViewById(R.id.dateAdminEdit);
        final EditText locationET = findViewById(R.id.locationAdminEdit);
        TextView currentNumOfParticipants = findViewById(R.id.participantsAdmin);

        subjectET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.child("Groups").child(groupID).child("subject").setValue(subjectET.getText().toString());
            }
        });

        dateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.child("Groups").child(groupID).child("date").setValue(dateET.getText().toString());
            }
        });

        locationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.child("Groups").child(groupID).child("location").setValue(locationET.getText().toString());
            }
        });

        Button deleteGroup = findViewById(R.id.deleteAdmin);
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentContext);
                alertDialog.setTitle(R.string.AreYouSure);
                alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.child("Groups").child(groupID).child("participants")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot child : dataSnapshot.getChildren())
                                        {
                                            String currentUser = child.getKey();
                                            database.child("Users").child(currentUser).child("Joined").child(groupID).removeValue();
                                            database.child("Groups").child(groupID).child("participants")
                                                    .child(currentUser).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        database.child("Groups").child(groupID).child("Requests")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot child : dataSnapshot.getChildren())
                                        {
                                            String currentUser = child.getKey();
                                            database.child("Users").child(currentUser).child("Requests").child(groupID).removeValue();
                                            database.child("Groups").child(groupID).child("Requests")
                                                    .child(currentUser).removeValue();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        database.child("Groups").child(groupID).removeValue();
                        finish();
                    }
                }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        subjectET.setText(subject);
        dateET.setText(date);
        locationET.setText(location);
        currentNumOfParticipants.setText(numOfParticipants.toString() + " Participants");
        final RecyclerView requestsRecycler = findViewById(R.id.requestAdminRecycler);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(currentContext));


        database.child("Groups").child(groupID).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    User userToAdd = new User(d.getKey().toString(),d.getValue().toString());
                    requests.add(userToAdd);
                }
                AdminRequestsAdapter requestsAdapter = new AdminRequestsAdapter(requests,groupID,numOfParticipants);
                requestsRecycler.setAdapter(requestsAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.child("Groups").child(groupID).child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecyclerView participantsRecycler = findViewById(R.id.recyclerPaticipantsGroup);
                participantsRecycler.setLayoutManager(new LinearLayoutManager(currentContext));
                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    participants.add(d.getValue().toString());
                }

                AdminParticipantsAdapter participantAdapter= new AdminParticipantsAdapter(participants);
                participantsRecycler.setAdapter(participantAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
