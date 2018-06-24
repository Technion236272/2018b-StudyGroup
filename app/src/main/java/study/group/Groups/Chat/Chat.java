package study.group.Groups.Chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import study.group.Groups.Admin.GroupAdminActivity;
import study.group.Groups.Participant.GroupDetails;
import study.group.R;
import study.group.Utilities.User;
import study.group.Utilities.ConnectionDetector;

public class Chat extends AppCompatActivity {

 //   private TextView Messages;
    private EditText messageToSend;
    private Button Send;
    private DatabaseReference dataBase;
    private LinearLayoutManager lay;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<UserMessage> messages;
    private String adminID;
    private String groupID;
    private String groupName;
    private String subject;
    private String date;
    private String time;
    private String location;
    private Integer maxNumOfParticipants;
    private Integer currentNumOfParticipants;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;
    private Uri mImageUri;
    private Menu mMenuOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checking connection
        ConnectionDetector cd = new ConnectionDetector(this);
        cd.isConnected();

        final Context myContext = this;
        groupID = getIntent().getExtras().getString("groupID");
        groupName = getIntent().getExtras().getString("groupName");
        adminID = getIntent().getExtras().getString("adminID");
        subject = getIntent().getExtras().getString("groupSubject");
        date = getIntent().getExtras().getString("groupDate");
        time = getIntent().getExtras().getString("groupTime");
        location = getIntent().getExtras().getString("groupLocation");
        currentNumOfParticipants = getIntent().getExtras().getInt("groupCurrentParticipants");
        maxNumOfParticipants = getIntent().getExtras().getInt("numOfParticipants");

        setTitle(groupName);

        messages = new ArrayList<>();

        //all the messages in the chat group
        //Messages = findViewById(R.id.messages);
        //the message that the current user wants to send
        messageToSend = findViewById(R.id.messageToSend);
        // the send button
        Send = findViewById(R.id.sendBtn);
        dataBase = FirebaseDatabase.getInstance().getReference();

        mMessageAdapter= new MessageListAdapter(this,messages);
        mMessageRecycler = findViewById(R.id.messagesRecycler);
        mMessageRecycler.setAdapter(mMessageAdapter);
        lay = new LinearLayoutManager(this);
        lay.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(lay);

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generate a random key for the new message
                String message = messageToSend.getText().toString();
                if(message.length() == 0) {
                    return;
                }

                String key = dataBase.child("Groups").child(groupID).child("Chat").push().getKey();
                //getting the content of the message to send
                //adding the message to the database:
                //fist we enter the username and the the message
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("User").setValue(Profile.getCurrentProfile().getId());
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Message").setValue(message);
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("TimeStamp").setValue(new Date());
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Name").setValue(Profile.getCurrentProfile().getName());
                String pc = Profile.getCurrentProfile().getProfilePictureUri(30,30).toString();
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("ProfilePicture").setValue(pc);
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Type").setValue("Message");
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("GroupAdminID").setValue(adminID);
                messageToSend.setText("");

                String userName = Profile.getCurrentProfile().getName() + Profile.getCurrentProfile().getLastName();
                final Map<String, Object> notification = new HashMap<>();
                String newMessage = "New Message From: "+userName + Profile.getCurrentProfile().getLastName() + " in " + subject;
                notification.put("Notification", newMessage);
                notification.put("Type","New Message");
                notification.put("Sender",Profile.getCurrentProfile().getFirstName());

                final Set<String> participants = new HashSet<>();
                dataBase.child("Groups").child(groupID).child("Chat").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataBase.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                participants.clear();
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    if (!d.getKey().equals(Profile.getCurrentProfile().getId())){
                                        participants.add(d.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                for(String s:participants){
                    mFirestore.collection("Users/"+s+"/Notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
                }

//                dataBase.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot d:dataSnapshot.getChildren()){
//                            if(!d.getKey().equals(Profile.getCurrentProfile().getId())){
//                                mFirestore.collection("Users/"+d.getKey()+"/Notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                    @Override
//                                    public void onSuccess(DocumentReference documentReference) {
//
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }
        });

        dataBase.child("Groups").child(groupID).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mImageUri = Uri.parse((String)dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //adding a listener to the group chat
        dataBase.child("Groups").child(groupID).child("Chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount()+1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount()+1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 7) {
                    HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                    HashMap<String,Long> timeStamp = (HashMap<String, Long>)data.get("TimeStamp");

                    String userID = (String) data.get("User");
                    String chatMessage;
                    String groupID = getIntent().getExtras().getString("groupID");
                    String userName = (String) data.get("Name");
                    Uri profilePic = Uri.parse((String)data.get("ProfilePicture"));
                    User currentUser = new User(userID,userName, profilePic);
                    String Type = (String) data.get("Type");
                    String AdminID = (String) data.get("GroupAdminID");
                    long time = timeStamp.get("time");
                    chatMessage = (String)data.get("Message");

                    UserMessage um = new UserMessage(chatMessage,currentUser,time,Type,AdminID,groupID);
                    messages.remove(um);
                    mMessageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dataBase.child("Groups").child(groupID).child("currentNumOfPart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((dataSnapshot.getValue()) != null) {
                    currentNumOfParticipants = Integer.parseInt(dataSnapshot.getValue().toString());
                } else {
                    currentNumOfParticipants = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dataBase.child("Groups").child(groupID).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag = false;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if(d.getKey().equals(Profile.getCurrentProfile().getId()))
                    {
                        flag = true;
                        break;
                    }
                }

                if(!flag)
                {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        mMenuOptions = menu;
        if(adminID.equals(Profile.getCurrentProfile().getId())) {
            getMenuInflater().inflate(R.menu.chat_menu_edit_details, mMenuOptions);
        } else {
            getMenuInflater().inflate(R.menu.chat_menu_details, mMenuOptions);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMenuOptions != null) {
            mMenuOptions.clear();
            dataBase.child("Groups").child(groupID).child("adminID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMenuOptions.clear();
                    if (dataSnapshot.getValue().toString().equals(Profile.getCurrentProfile().getId())) {
                        getMenuInflater().inflate(R.menu.chat_menu_edit_details, mMenuOptions);
                    } else {
                        getMenuInflater().inflate(R.menu.chat_menu_details, mMenuOptions);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit_group_details) {
            Intent intent = new Intent(this, GroupAdminActivity.class);
            intent.putExtra("groupSubject",subject);
            intent.putExtra("groupDate",date);
            intent.putExtra("groupTime",time);
            intent.putExtra("groupID",groupID);
            intent.putExtra("groupLocation",location);
            intent.putExtra("maxNumOfParticipants", maxNumOfParticipants);
            intent.putExtra("numOfParticipants", currentNumOfParticipants);
            intent.putExtra("adminID",adminID);
            intent.putExtra("groupName",groupName);
            startActivity(intent);      // ADD FLAGS TO THE INTENT
        } else if(id == R.id.group_details) {
            Intent intent = new Intent(this, GroupDetails.class);
            intent.putExtra("groupSubject",subject);
            intent.putExtra("groupDate",date);
            intent.putExtra("groupTime",time);
            intent.putExtra("groupID",groupID);
            intent.putExtra("groupLocation",location);
            intent.putExtra("maxNumOfParticipants", maxNumOfParticipants);
            intent.putExtra("numOfParticipants", currentNumOfParticipants);
            intent.putExtra("adminID",adminID);
            intent.putExtra("groupName",groupName);
            startActivity(intent);      // ADD FLAGS TO THE INTENT
        } else if(id == R.id.leave_group_chat) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.leaving_the_group);
            alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dataBase.child("Groups").child(groupID).child("participants").child(Profile.getCurrentProfile().getId()).removeValue();
                    dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
                    --currentNumOfParticipants;
                    dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);

                    //adding a system message that the current participant left
                    String key = dataBase.child("Groups").child(groupID).child("Chat").push().getKey();
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("User").setValue(Profile.getCurrentProfile().getId());
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Message").setValue("");
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("TimeStamp").setValue(new Date());
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Name").setValue(Profile.getCurrentProfile().getName());
                    String pc = Profile.getCurrentProfile().getProfilePictureUri(30,30).toString();
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("ProfilePicture").setValue(pc);
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Type").setValue("System_Left");
                    dataBase.child("Groups").child(groupID).child("Chat").child(key).child("GroupAdminID").setValue(adminID);

                    finish();
                }
            }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        } else if(id == R.id.leave_group_admin) {
            if(currentNumOfParticipants == 1) {
                // delete the group
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.only_member_delete_group);
                alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String checkImgUrl = "gs://b-studygroup.appspot.com/uploads/StudyGroup1.png";
                        mStorageRef.child(groupID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(!uri.toString().equals(checkImgUrl)) {
                                    StorageReference photoRef = mStorageRef.getStorage().getReferenceFromUrl(uri.toString());
                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                }
                            }
                        });
                        dataBase.child("Groups").child(groupID).removeValue();
                        dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
                        dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child(groupID).removeValue();

                        finish();

                    }
                }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            } else {
                // pass the administraship to someone else
                dataBase.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!ds.getKey().equals(Profile.getCurrentProfile().getId())) {
                                String nextAdmin = ds.getKey();
                                dataBase.child("Groups").child(groupID).child("adminID").setValue(nextAdmin);
                                dataBase.child("Users").child(nextAdmin).child("myGroups").child(groupID).setValue(subject);

                                String oldAdmin = Profile.getCurrentProfile().getName();
                                final Map<String, Object> notification = new HashMap<>();
                                String newMessage = oldAdmin + " has left "+subject+" you are now Administrator.";
                                notification.put("Notification", newMessage);
                                notification.put("Type","New Message");
                                notification.put("Sender",Profile.getCurrentProfile().getFirstName());
                                mFirestore.collection("Users/"+nextAdmin+"/Notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                    }
                                });

                                break;
                            }
                        }

                        //adding a system message that the current participant left
                        String key = dataBase.child("Groups").child(groupID).child("Chat").push().getKey();
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("User").setValue(Profile.getCurrentProfile().getId());
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Message").setValue("");
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("TimeStamp").setValue(new Date());
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Name").setValue(Profile.getCurrentProfile().getName());
                        String pc = Profile.getCurrentProfile().getProfilePictureUri(30,30).toString();
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("ProfilePicture").setValue(pc);
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Type").setValue("System_Left");
                        dataBase.child("Groups").child(groupID).child("Chat").child(key).child("GroupAdminID").setValue(adminID);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                dataBase.child("Groups").child(groupID).child("participants").child(Profile.getCurrentProfile().getId()).removeValue();
                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child(groupID).removeValue();
                --currentNumOfParticipants;
                dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);
                // Sending a notification to the new Admin
                // User: X , you are the new admin of : YY group


            }
        } else if(id == R.id.delete_group) {
            deleteTheGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    //a helper function that creates the TextView that holds all of the group chat
    private void append_chat_conversation(DataSnapshot ds) {
        if(ds.getChildrenCount() == 7) {
            HashMap<String, Object> data = (HashMap<String, Object>) ds.getValue();
            HashMap<String,Long> timeStamp = (HashMap<String, Long>)data.get("TimeStamp");

            String userID = (String) data.get("User");
            String chatMessage;
            String groupID = getIntent().getExtras().getString("groupID");
            String userName = (String) data.get("Name");
            Uri profilePic = Uri.parse((String)data.get("ProfilePicture"));
            User currentUser = new User(userID,userName, profilePic);
            String Type = (String) data.get("Type");
            String AdminID = (String) data.get("GroupAdminID");
            long time = timeStamp.get("time");
            chatMessage = (String)data.get("Message");

            UserMessage um = new UserMessage(chatMessage, currentUser, time, Type, AdminID, groupID);
            messages.add(um);
            mMessageAdapter.notifyDataSetChanged();
        }

    }


    private void deleteTheGroup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Chat.this);
        alertDialog.setTitle(R.string.AreYouSure);
        alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String checkImgUrl = "gs://b-studygroup.appspot.com/uploads/StudyGroup1.png";
                if(mImageUri != null) {
                    if (!mImageUri.toString().equals(checkImgUrl)) {
                        StorageReference photoRef = mStorageRef.getStorage().getReferenceFromUrl(mImageUri.toString());
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                            }
                        });
                    }
                }
                dataBase.child("Groups").child(groupID).child("participants")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot participant : dataSnapshot.getChildren()) {
                                    if (participant.getKey().equals(adminID)) {
                                        dataBase.child("Users").child(participant.getKey()).child("myGroups").child(groupID).removeValue();
                                    }
                                    dataBase.child("Users").child(participant.getKey()).child("Joined").child(groupID).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                dataBase.child("Groups").child(groupID).child("Requests")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String currentUser = child.getKey();
                                    dataBase.child("Users").child(currentUser).child("Requests").child(groupID).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                dataBase.child("Groups").child(groupID).removeValue();
                finish();
            }
        }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
