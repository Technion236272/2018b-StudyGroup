package study.group.Groups.Chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import study.group.Groups.Admin.GroupAdminActivity;
import study.group.R;
import study.group.Utilities.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                if(flag == false)
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if(adminID.equals(Profile.getCurrentProfile().getId())) {
            getMenuInflater().inflate(R.menu.chat_menu_edit_details, menu);
        } else {
            getMenuInflater().inflate(R.menu.chat_menu_details, menu);
        }
        return true;
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
            Toast.makeText(this, "Details will be available soon", Toast.LENGTH_LONG).show();
        } else if(id == R.id.leave_group_chat) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.leaving_the_group);
            alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dataBase.child("Groups").child(groupID).child("participants").child(Profile.getCurrentProfile().getId()).removeValue();
                    dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
                    --currentNumOfParticipants;
                    dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);
                    finish();
//                    Intent intent1 = new Intent(Chat.this, GroupActivity.class);
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent1);
//                    Chat.this.finish();
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
                String nextAdmin = getIntent().getExtras().getString("nextAdmin");
                dataBase.child("Groups").child(groupID).child("adminID").setValue(nextAdmin);
                dataBase.child("Users").child(nextAdmin).child("myGroups").child(groupID).setValue(subject);
                dataBase.child("Users").child(nextAdmin).child("Joined").child(groupID).setValue(subject);
                dataBase.child("Groups").child(groupID).child("participants").child(Profile.getCurrentProfile().getId()).removeValue();
                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child(groupID).removeValue();
                --currentNumOfParticipants;
                dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);
                // Sending a notification to the new Admin
                // User: X , you are the new admin of : YY group
            }
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
