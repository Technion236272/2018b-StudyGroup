package study.group.Groups.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import study.group.Groups.CreateGroup;
import study.group.R;
import study.group.Utilities.User;

public class GroupAdminActivity extends AppCompatActivity {

    private DatabaseReference database;
    private String groupID;
    private String adminID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_admin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference();

        String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String time = getIntent().getExtras().getString("groupTime");
        String location = getIntent().getExtras().getString("groupLocation");
        groupID = getIntent().getExtras().getString("groupID");
        final int numOfParticipants = getIntent().getExtras().getInt("numOfParticipants");
        adminID = getIntent().getExtras().getString("adminID");
        final String groupName = getIntent().getExtras().getString("groupName");

        final Set<Pair<String,String>> participants = new HashSet<>();

        setTitle(groupName);

        final EditText subjectET = findViewById(R.id.subjectAdminEdit);
        final Button dateET = findViewById(R.id.dateAdminEdit);
        final Button timeET = findViewById(R.id.timeAdminEdit);
        final EditText locationET = findViewById(R.id.locationAdminEdit);
        final TextView currentNumOfParticipants = findViewById(R.id.participantsAdmin);

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

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal  = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(GroupAdminActivity.this, android.app.AlertDialog.THEME_HOLO_DARK
                        , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String text = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month+1) + "/" + year;
                        database.child("Groups").child(groupID).child("date").setValue(text);
                        dateET.setText(text);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            }
        });
        // need to notify something, not changing immediately   /////////////////////////////////////////////////////////////////////////////////////
        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal  = Calendar.getInstance();
                final int currentHour = (cal.get(Calendar.HOUR_OF_DAY) + 3) % 24;
                final int currentMinute = cal.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(GroupAdminActivity.this, android.app.AlertDialog.THEME_HOLO_DARK
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String text = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute);
                        database.child("Groups").child(groupID).child("time").setValue(text);
                        timeET.setText(text);

                    }
                }, currentHour, currentMinute, true);
                mTimePicker.show();
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

        subjectET.setText(subject);
        dateET.setText(date);
        timeET.setText(time);
        locationET.setText(location);

        database.child("Groups").child(groupID).child("currentNumOfPart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentNumOfParticipants.setText(dataSnapshot.getValue() + " Participants:");
                //TODO: potential Bug
                //numOfParticipants = (long)dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.child("Groups").child(groupID).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecyclerView participantsRecycler = findViewById(R.id.recyclerPaticipantsGroup);
                participants.clear();
                participantsRecycler.setLayoutManager(new LinearLayoutManager(GroupAdminActivity.this));
                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    if(!d.getKey().equals(Profile.getCurrentProfile().getId()))
                    {
                        participants.add(new Pair<>(d.getKey(), (String) d.getValue()));
                    }
                }

                AdminParticipantsAdapter participantAdapter = new AdminParticipantsAdapter(new ArrayList<>(participants),groupID,numOfParticipants);
                participantsRecycler.setAdapter(participantAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.pass_administraship) {
            Toast.makeText(this, "Passing administraship will be available soon", Toast.LENGTH_LONG).show();
        } else if(id == R.id.make_poll) {
            Toast.makeText(this, "Making a poll will be available soon", Toast.LENGTH_LONG).show();
        } else if(id == R.id.delete_group) {
     //       Toast.makeText(this, "Deleting the group will be available soon", Toast.LENGTH_LONG).show();
            deleteTheGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTheGroup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupAdminActivity.this);
        alertDialog.setTitle(R.string.AreYouSure);
        alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.child("Groups").child(groupID).child("participants")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot participant : dataSnapshot.getChildren()) {
                                    if (participant.getKey().equals(adminID)) {
                                        database.child("Users").child(participant.getKey()).child("myGroups").child(groupID).removeValue();
                                        }
                                        database.child("Users").child(participant.getKey()).child("Joined").child(groupID).removeValue();
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
                                for(DataSnapshot child : dataSnapshot.getChildren()) {
                                    String currentUser = child.getKey();
                                    database.child("Users").child(currentUser).child("Requests").child(groupID).removeValue();
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
