package study.group.Groups.Participant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import study.group.Groups.Chat.Chat;
import study.group.Groups.CreateGroup;
import study.group.MainActivity;
import study.group.R;
import study.group.Utilities.Writer.ConnectionDetector;

public class GroupActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private static final int NOTIFICATION_ID = 101;
    public static final String CHANNEL_ID = "my_notification_channel";
    public static final String TEXT_REPLY = "text_reply";
    final Context currentContext = this;
    private boolean isJoined = false;
    //    static boolean isExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //checking connection
        ConnectionDetector cd = new ConnectionDetector(this);
        cd.isConnected();

        mFirestore = FirebaseFirestore.getInstance();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String subject = getIntent().getExtras().getString("groupSubject");
        final String date = getIntent().getExtras().getString("groupDate");
        final String time = getIntent().getExtras().getString("groupTime");
        final String location = getIntent().getExtras().getString("groupLocation");
        final String groupID = getIntent().getExtras().getString("groupID");
        final Integer numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        final String adminID = getIntent().getExtras().getString("adminID");
        final Integer groupMaxParticipants = getIntent().getExtras().getInt("maxNumOfPart");


        setTitle(subject);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String userID = Profile.getCurrentProfile().getId();
        final String userName = Profile.getCurrentProfile().getName();
        final Button joinRequest = (Button) findViewById(R.id.Request);
        final Button cancelRequest = (Button) findViewById(R.id.cancelRequest);
        final Button interestedButton = (Button) findViewById(R.id.Interested);

        //if a group is full a user cant send a request
        if(groupMaxParticipants == numOfParticipants)
        {
            joinRequest.setVisibility(View.GONE);
        }
        //In case the user is interested in a coursse
        database.child("Users").child(userID).child("interested").addValueEventListener(new ValueEventListener() {
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
        final TextView currentNumOfParticipants = (TextView)findViewById(R.id.groupParticipants);

        //The listener of the join requets button
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here we check if the user is already accepted in the group
                database.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isJoined = false;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(ds.getKey().equals(Profile.getCurrentProfile().getId())) {
                                isJoined = true;
                            }
                        }
                        if(isJoined == true)
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentContext);
                            alertDialog.setTitle(R.string.already_joined);
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    return;
                                }
                            }).setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                        }
                        else
                        {
                            joinRequest.setVisibility(View.VISIBLE);
                            cancelRequest.setVisibility(View.GONE);
                            interestedButton.setVisibility(View.VISIBLE);
                            Toast.makeText(currentContext, "Join request canceled", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
            }
        });

        joinRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestedButton.setEnabled(true);
                interestedButton.setVisibility(View.GONE);

                database.child("Users").child(userID).child("Requests").child(groupID).setValue(subject);
                database.child("Groups").child(groupID).child("Requests").child(userID).setValue(userName);
                database.child("Users").child(userID).
                    child("interested").child(groupID).removeValue();
                database.child("Groups").child(groupID).child("interested").child(userID).removeValue();

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

                final Map<String, Object> notification = new HashMap<>();
                String newJoinRequest = "Hi, "+ userName +" "+Profile.getCurrentProfile().getLastName() +" wants to join "+subject;
                notification.put("Notification", newJoinRequest);
                notification.put("Type","Join Request");
                notification.put("Sender", userName + Profile.getCurrentProfile().getLastName());

                mFirestore.collection("Users/"+adminID+"/Notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });
//                database.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot d:dataSnapshot.getChildren()){
//                            if(!d.getKey().equals(FirebaseAuth.getInstance().getUid())){
//                               ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                           String notificationTitle = "StudyGroup - Join request";
                           String notificationContent = userName + " is wish to join " + subject;
                           setNotification(notificationTitle, notificationContent);
            }
        });



        dateTV.setText(date);
        timeTV.setText(time);
        subjectTV.setText(subject);
        locationTV.setText(location);
        currentNumOfParticipants.setText(String.valueOf(numOfParticipants) + " Participants:");

        final Set<String> participants = new HashSet<>();


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
        mBuilder.setSmallIcon(R.drawable.ic_white_logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.study_group_logo));
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
