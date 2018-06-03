package study.group.Groups;

import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;

import study.group.R;

public class Chat extends AppCompatActivity {

    private TextView Messages;
    private EditText messageToSend;
    private Button Send;
    private DatabaseReference dataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //local variables
        final String groupID = getIntent().getExtras().getString("groupID");
        final String user = Profile.getCurrentProfile().getFirstName();

        //all the messsages in the chat group
        Messages = findViewById(R.id.messages);
        //the message that the current user wants to send
        messageToSend = findViewById(R.id.messageToSend);
        // the send button
        Send = findViewById(R.id.sendBtn);
        dataBase = FirebaseDatabase.getInstance().getReference();

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generate a random key for the new message
                String key = dataBase.child("Groups").child(groupID).child("Chat").push().getKey();
                //getting the content of the message to send
                String message = messageToSend.getText().toString();
                //adding the message to the database:
                //fist we enter the username and the the message
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("user").setValue(user);
                dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Message").setValue(message);
                //dataBase.child("Groups").child(groupID).child("Chat").child(key).child("TimeStamp").setValue(new Date());
            }
        });

        //adding a listener to the group chat
        dataBase.child("Groups").child(groupID).child("Chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //a helper function that creates the TextView that holds all of the group chat
    private void append_chat_conversation(DataSnapshot ds)
    {
        Iterator<DataSnapshot> i = ds.getChildren().iterator();
        String chatMessage, userName;
        while(i.hasNext())
        {
            DataSnapshot d = i.next();
            userName = (String)(d.child("user").getValue());
            chatMessage = (String)(d.child("Message")).getValue();
            Messages.append(userName + " : " + chatMessage + "\n");
        }
    }


}
