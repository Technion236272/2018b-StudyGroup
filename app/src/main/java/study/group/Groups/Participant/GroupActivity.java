package study.group.Groups.Participant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import study.group.Groups.Chat.Chat;
import study.group.MainActivity;
import study.group.R;

public class GroupActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 101;
    public static final String CHANNEL_ID = "my_notification_channel";
    public static final String TEXT_REPLY = "text_reply";
    final Context currentContext = this;
    //    static boolean isExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String subject = getIntent().getExtras().getString("groupSubject");
        final String date = getIntent().getExtras().getString("groupDate");
        final String time = getIntent().getExtras().getString("groupTime");
        final String location = getIntent().getExtras().getString("groupLocation");
        final String groupID = getIntent().getExtras().getString("groupID");
        final Integer numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        final String adminID = getIntent().getExtras().getString("adminID");

        setTitle(subject);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String userID = Profile.getCurrentProfile().getId();
        final String userName = Profile.getCurrentProfile().getName();
        final Button joinRequest = (Button) findViewById(R.id.Request);
        final Button cancelRequest = (Button) findViewById(R.id.cancelRequest);
        final Button interestedButton = (Button) findViewById(R.id.Interested);

        //In case the user is interested in a coursse
        database.child("Users").child(userID).child("interested").addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (isExist) {
                    interestedButton.setText(R.string.uninterested);
                    interestedButton.setBackgroundColor(getResources().getColor(R.color.Red));
                }
                else
                {
                    interestedButton.setText(R.string.Interested);
                    interestedButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //in case the user sends a request to a course
        database.child("Users").child(userID).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExist = false;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals(groupID)) {
                        isExist = true;
                    }
                }
                if (isExist) {
                    cancelRequest.setVisibility(View.VISIBLE);
                    joinRequest.setVisibility(View.GONE);
                    interestedButton.setVisibility(View.GONE);
                } else {
                    cancelRequest.setVisibility(View.GONE);
                    joinRequest.setVisibility(View.VISIBLE);
                    interestedButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TextView subjectTV = (TextView)findViewById(R.id.SubjectInGroupContent);
        TextView dateTV = (TextView)findViewById(R.id.DateInGroupContent);
        TextView timeTV = (TextView)findViewById(R.id.timeInGroupContent);
        TextView locationTV = (TextView)findViewById(R.id.LocationInGroupContent);
        TextView currentNumOfParticipants = (TextView)findViewById(R.id.groupParticipants);

        //The listener of the join requets button
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    database.child("Users").child(userID).child("Requests").child(groupID).removeValue();
                    database.child("Groups").child(groupID).child("Requests").child(userID).removeValue();
                    database.child("Groups").child(groupID).child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                HashMap<String, Object> m = (HashMap<String,Object>)ds.getValue();
                                if(m != null && m.size() == 7)
                                {
                                    if(m.get("Type").equals("Request") && m.get("User").equals(Profile.getCurrentProfile().getId()))
//                                        if(ds.child("Type").getValue().equals("Request")&&
//                                                ds.child("User").getValue().equals(Profile.getCurrentProfile().getId()))
                                    {
                                        database.child("Groups").child(groupID).child("Chat").child(ds.getKey()).removeValue();
                                        break;
                                    }
                                }

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    joinRequest.setVisibility(View.VISIBLE);
                    cancelRequest.setVisibility(View.GONE);
                    interestedButton.setVisibility(View.VISIBLE);
                    Toast.makeText(currentContext, "Join request canceled", Toast.LENGTH_SHORT).show();
            }


        });

        joinRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    interestedButton.setEnabled(true);
                    interestedButton.setVisibility(View.GONE);

                    database.child("Users").child(userID).child("Requests").child(groupID).setValue(subject);
                    database.child("Groups").child(groupID).child("Requests").child(userID).setValue(userName);

                    String key = database.child("Groups").child(groupID).child("Chat").push().getKey();
                    database.child("Groups").child(groupID).child("Chat").child(key).child("User").setValue(Profile.getCurrentProfile().getId());
                    database.child("Groups").child(groupID).child("Chat").child(key).child("Message").setValue("");
                    database.child("Groups").child(groupID).child("Chat").child(key).child("TimeStamp").setValue(new Date());
                    database.child("Groups").child(groupID).child("Chat").child(key).child("Name").setValue(Profile.getCurrentProfile().getName());
                    String pc = Profile.getCurrentProfile().getProfilePictureUri(30,30).toString();
                    database.child("Groups").child(groupID).child("Chat").child(key).child("ProfilePicture").setValue(pc);
                    database.child("Groups").child(groupID).child("Chat").child(key).child("Type").setValue("Request");
                    database.child("Groups").child(groupID).child("Chat").child(key).child("GroupAdminID").setValue(adminID);

                    joinRequest.setVisibility(View.GONE);
                    cancelRequest.setVisibility(View.VISIBLE);
                    Toast.makeText(currentContext, "Join request has been sent", Toast.LENGTH_SHORT).show();

                    //       String notificationTitle = "StudyGroup - Join request";
                    //       String notificationContent = userName + " is wish to join " + subject;
                    //       setNotification(notificationTitle, notificationContent);
            }
        });



        dateTV.setText(date);
        timeTV.setText(time);
        subjectTV.setText(subject);
        locationTV.setText(location);
        currentNumOfParticipants.setText(String.valueOf(numOfParticipants) + " Participants:");

        final Set<String> participants = new HashSet<>();

        //the DB listener of the group participants.
        database.child("Groups").child(groupID).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecyclerView participantsRecycler = (RecyclerView)findViewById(R.id.recyclerPaticipantsGroup);
                participantsRecycler.setLayoutManager(new LinearLayoutManager(currentContext));
                boolean flag = false;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    participants.add(d.getValue().toString());
                    if(d.getKey().equals(Profile.getCurrentProfile().getId()))
                    {
                        flag = true;
                        break;
                    }
                }

                if(flag)
                {
                    Intent chatActivity;
                    chatActivity = new Intent(currentContext, Chat.class);
                    chatActivity.putExtra("groupSubject",subject);
                    chatActivity.putExtra("groupDate",date);
                    chatActivity.putExtra("groupTime",time);
                    chatActivity.putExtra("groupID",groupID);
                    chatActivity.putExtra("groupLocation",location);
                    chatActivity.putExtra("numOfParticipants",numOfParticipants);
                    chatActivity.putExtra("adminID",adminID);
                    chatActivity.putExtra("groupName",getIntent().getExtras().getString("groupName"));
                    currentContext.startActivity(chatActivity);
                }

                GroupParticipantsAdapter participantsAdapter = new GroupParticipantsAdapter(new ArrayList<>(participants));
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
                            database.child("Groups").child(groupID).child("interested").child(userID).removeValue();
                            interestedButton.setText(R.string.Interested);
                            interestedButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        } else {
                            database.child("Users").child(userID).child("interested").child(groupID).setValue(subject);
                            database.child("Groups").child(groupID).child("interested").child(userID).setValue(userName);
                            interestedButton.setText(R.string.uninterested);
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


    public void setNotification(String title, String content) {
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_logo);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
//        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        //      mBuilder.setContentIntent(pendingIntent);

        //      mBuilder.addAction(R.drawable.ic_logo, "Yes", pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);     // TODO : check it out
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
