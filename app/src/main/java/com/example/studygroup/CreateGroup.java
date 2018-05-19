package com.example.studygroup;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateGroup extends AppCompatActivity {
    private EditText groupSubject;
    private EditText Location;
    private Spinner day;
    private Spinner month;
    private Spinner year;
    private Spinner numOfParticipants;
    private Button createButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupSubject = (EditText)findViewById(R.id.groupSubject);
        Location = (EditText)findViewById(R.id.Location);
        day = (Spinner)findViewById(R.id.daySpinner);
        month = (Spinner)findViewById(R.id.monthSpinner);
        year = (Spinner)findViewById(R.id.yearsSpinner);
        numOfParticipants = (Spinner)findViewById(R.id.NumOfParticipants);
        createButton = (Button)findViewById(R.id.CreateGroup);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        String[] participantsNum = new String[]{"Number Of Participants","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};
        final List<String> participantsNumList = new ArrayList<>(Arrays.asList(participantsNum));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, participantsNumList)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                    return false;
                else
                    return true;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
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

        numOfParticipants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Toast.makeText
                            (getApplicationContext(), "Please select number of maximum participants!", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });








        Integer[] daysArr = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22
                                            ,23,24,25,26,27,28,29,30,31};
        ArrayAdapter<Integer> daysAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, daysArr );
        day.setAdapter(daysAdapter);

        Integer[] monthArr = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, monthArr);
        month.setAdapter(monthAdapter);


        Integer[] yearsArr = new Integer[]{2018,2019};
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, yearsArr);
        year.setAdapter(yearAdapter);


        createButton.setOnClickListener(new View.OnClickListener()
        {
            //TODO: get the user ID
            @Override
            public void onClick(View view)
            {
                String subject = groupSubject.getText().toString();
                String location = Location.getText().toString();
                String date = day.getSelectedItem() + "-" + month.getSelectedItem() + "-"+year.getSelectedItem();
                int numOfPart = (int)numOfParticipants.getSelectedItem();
                myRef.child("Groups").child(subject).child("Coures Name").setValue("235503");
                myRef.child("Groups").child(subject).child("Group Participants").setValue(numOfPart);
                myRef.child("Groups").child(subject).child("Location").setValue(location);
                myRef.child("Groups").child(subject).child("Date").setValue(date);

            }
        });

    }
}
