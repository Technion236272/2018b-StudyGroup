package study.group.Groups.Participant;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import study.group.R;

public class GroupActivity extends AppCompatActivity {
//    static boolean isExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Context currentContext = this;

        final String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String location = getIntent().getExtras().getString("groupLocation");
        final String groupID = getIntent().getExtras().getString("groupID");
        final Integer numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        final String adminID = getIntent().getExtras().getString("adminID");

        setTitle(subject);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String userID = Profile.getCurrentProfile().getId();
        final String userName = Profile.getCurrentProfile().getName();
        final Button joinRequest = (Button) findViewById(R.id.Request);
        final Button interestedButton = (Button) findViewById(R.id.Interested);

        database.child("Users").child(userID).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExist = false;
                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    if(d.getKey().equals(groupID))
                    {
                        isExist = true;
                    }
                }
                if(isExist == true) {
                    joinRequest.setText(R.string.cancel_request);
                    joinRequest.setBackgroundColor(getResources().getColor(R.color.Red));
                }
                else
                {
                    joinRequest.setText(R.string.request_to_join);
                    joinRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TextView subjectTV = (TextView)findViewById(R.id.SubjectInGroupContent);
        TextView dateTV = (TextView)findViewById(R.id.DateInGroupContent);
        TextView locationTV = (TextView)findViewById(R.id.LocationInGroupContent);
        TextView currentNumOfParticipants = (TextView)findViewById(R.id.groupParticipants);

        joinRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child("Users").child(userID).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isExist = false;
                        isExist = false;
                        for (DataSnapshot d : dataSnapshot.getChildren())
                        {
                            if(d.getKey().equals(groupID)) {
                                isExist = true;
                                break;
                            }
                        }
                        if(isExist) {
                            database.child("Users").child(userID).child("Requests").child(groupID).removeValue();
                            database.child("Groups").child(groupID).child("Requests").child(userID).removeValue();
                            joinRequest.setText("Request to join");
                            joinRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        } else {
                            database.child("Users").child(userID).child("Requests").child(groupID).setValue(subject);
                            database.child("Groups").child(groupID).child("Requests").child(userID).setValue(userName);
                            joinRequest.setText("Cancel Request");
                            joinRequest.setBackgroundColor(getResources().getColor(R.color.Red));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        dateTV.setText(date);
        subjectTV.setText(subject);
        locationTV.setText(location);
        currentNumOfParticipants.setText(String.valueOf(numOfParticipants) + " Participants");

        final ArrayList<String> participants = new ArrayList<>();

        database.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecyclerView participantsRecycler = (RecyclerView)findViewById(R.id.recyclerPaticipantsGroup);
                participantsRecycler.setLayoutManager(new LinearLayoutManager(currentContext));
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    participants.add(d.getValue().toString());
                }
                GroupParticipantsAdapter participantsAdapter = new GroupParticipantsAdapter(participants);
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

                database.child("Users").child(userID).child("interested").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        database.child("Users").child(userID).child("interested")
//                                .child(groupID).setValue("");

                        boolean isExist = false;
                        for (DataSnapshot d : dataSnapshot.getChildren())
                        {
                            if(d.getKey().equals(groupID)) {
                                isExist = true;
                                break;
                            }
                        }
                        if(isExist) {
                            database.child("Users").child(userID).child("interested").child(groupID).removeValue();
                            interestedButton.setText("Interested");
                            interestedButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        } else {
                            database.child("Users").child(userID).child("interested").child(groupID).setValue(subject);
                            interestedButton.setText("Uninterested");
                            interestedButton.setBackgroundColor(getResources().getColor(R.color.Red));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
