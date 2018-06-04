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

import study.group.MainActivity;
import study.group.R;

public class GroupActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 101;
    public static final String CHANNEL_ID = "my_notification_channel";
    public static final String TEXT_REPLY = "text_reply";

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
                    joinRequest.setText(R.string.cancel_request);
                    joinRequest.setBackgroundColor(getResources().getColor(R.color.Red));
                } else {
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
                            joinRequest.setText(R.string.request_to_join);
                            joinRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            Toast.makeText(currentContext, "Join request canceled", Toast.LENGTH_SHORT).show();

                        } else {
                            database.child("Users").child(userID).child("Requests").child(groupID).setValue(subject);
                            database.child("Groups").child(groupID).child("Requests").child(userID).setValue(userName);
                            joinRequest.setText(R.string.cancel_join_request);
                            joinRequest.setBackgroundColor(getResources().getColor(R.color.Red));
                            Toast.makeText(currentContext, "Join request has been sent", Toast.LENGTH_SHORT).show();

                     //       String notificationTitle = "StudyGroup - Join request";
                     //       String notificationContent = userName + " is wish to join " + subject;
                     //       setNotification(notificationTitle, notificationContent);
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
                            interestedButton.setText(R.string.Interested);
                            interestedButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        } else {
                            database.child("Users").child(userID).child("interested").child(groupID).setValue(subject);
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

}
