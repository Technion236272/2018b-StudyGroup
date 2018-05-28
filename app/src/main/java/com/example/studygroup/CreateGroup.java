package com.example.studygroup;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.text.DateFormat.getDateInstance;

public class CreateGroup extends AppCompatActivity {
    private EditText groupSubject;
    private EditText Location;
    private Spinner day;
    private Spinner month;
    private Spinner year;
    private Spinner numOfParticipants;
    private Button createButton;
    private DatabaseReference myRef;

    private String courseId;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupSubject = findViewById(R.id.groupSubject);
        Location = findViewById(R.id.Location);
        day = findViewById(R.id.daySpinner);
        month = findViewById(R.id.monthSpinner);
        year = findViewById(R.id.yearsSpinner);
        numOfParticipants = findViewById(R.id.NumOfParticipants);
        createButton = findViewById(R.id.CreateGroup);

        courseId = getIntent().getExtras().getString("courseId");
        courseName = getIntent().getExtras().getString("courseName");
        String title = courseId + " - " + courseName;
        setTitle(title);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        String[] participantsNum = new String[]{"Number Of Participants","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
        final List<String> participantsNumList = new ArrayList<>(Arrays.asList(participantsNum));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, participantsNumList) {
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        numOfParticipants.setAdapter(adapter);

        Integer[] daysArr = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22
                                            ,23,24,25,26,27,28,29,30,31};
        ArrayAdapter<Integer> daysAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, daysArr );
        day.setAdapter(daysAdapter);

        Integer[] monthArr = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, monthArr);
        month.setAdapter(monthAdapter);

        Integer[] yearsArr = new Integer[]{2018,2019};
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, yearsArr);
        year.setAdapter(yearAdapter);
    }

    public void openAlertDialog(View view) throws ParseException {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        String subject = groupSubject.getText().toString();
        if(subject.length()==0) {
            alertDialog.setTitle(R.string.subjectError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }
        String location = Location.getText().toString();
        if (location .length()==0) {
            alertDialog.setTitle(R.string.locationError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }
        String date = day.getSelectedItem() + "/" + month.getSelectedItem() + "/" + year.getSelectedItem();
        Date currentDate = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = sdf1.parse(date);
        if(currentDate.after(strDate)) {
            alertDialog.setTitle(R.string.irrelevant_date);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }

        if(numOfParticipants.getSelectedItemPosition() == 0) {
            alertDialog.setTitle(R.string.participantsError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }

        Integer numOfPart = Integer.parseInt((numOfParticipants.getSelectedItem().toString()));
        Integer current = 1;

        String key = myRef.child("Groups").push().getKey();
        Group newGroup = new Group(key,courseId, subject, date, location, numOfPart, current,
                Profile.getCurrentProfile().getId());
        myRef.child("Groups").child(key).setValue(newGroup);
//        myRef.child("Groups").child(courseId + " - " + subject).setValue(newGroup);
        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child("GroupID").setValue(subject);
        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child("GroupID").setValue(subject);
        finish();
    }


}
