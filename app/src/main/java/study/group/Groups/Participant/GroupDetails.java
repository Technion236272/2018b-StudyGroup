package study.group.Groups.Participant;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import study.group.R;

public class GroupDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 131;
    private DatabaseReference database;
    private String groupID;
    private String adminID;
    private int maxNumOfParticipants;
    private ImageView groupPhoto;
    private Uri mImageUri;
    private Transformation transformation;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference();

        String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String time = getIntent().getExtras().getString("groupTime");
        String location = getIntent().getExtras().getString("groupLocation");
        groupID = getIntent().getExtras().getString("groupID");
        final int numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        maxNumOfParticipants = getIntent().getExtras().getInt("maxNumOfParticipants");
        adminID = getIntent().getExtras().getString("adminID");
        final String groupName = getIntent().getExtras().getString("groupName");

        final ArrayList<String> participants = new ArrayList<>();

        setTitle(groupName);

        final TextView subjectET = findViewById(R.id.subjectGroupDetail);
        final TextView dateET = findViewById(R.id.dateGroupDetail);
        final TextView timeET = findViewById(R.id.timeGroupDetail);
        final TextView locationET = findViewById(R.id.locationGroupDetail);
        final TextView currentNumOfParticipants = findViewById(R.id.numberOfPartGroupDetail);
        groupPhoto = findViewById(R.id.imageGroupDetail);
        final TextView maxNumOfPart = findViewById(R.id.NumOfParticipantsGroupDetail);

        subjectET.setText(subject);
        dateET.setText(date);
        timeET.setText(time);
        locationET.setText(location);
        StringBuilder maxParticipantsBuilder = new StringBuilder("Max participants: ").append(String.valueOf(maxNumOfParticipants));
        maxNumOfPart.setText(maxParticipantsBuilder.toString());
        StringBuilder currentMessageBuilder = new StringBuilder(String.valueOf(numOfParticipants)).append(" current participants") ;
        currentNumOfParticipants.setText(currentMessageBuilder.toString());

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(60)
                .oval(false)
                .build();

        database.child("Groups").child(groupID).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mImageUri = Uri.parse((String)dataSnapshot.getValue());
                Picasso.with(GroupDetails.this)
                        .load(mImageUri)
                        .fit()
                        .transform(transformation)
                        .into(groupPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.child("Groups").child(groupID).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecyclerView participantsRecycler = findViewById(R.id.recyclerPaticipantsGroupDetail);
                participants.clear();
                participantsRecycler.setLayoutManager(new LinearLayoutManager(GroupDetails.this));
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    participants.add((String) d.getValue());
                }
                GroupParticipantsAdapter participantAdapter = new GroupParticipantsAdapter(participants);
                participantsRecycler.setAdapter(participantAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
