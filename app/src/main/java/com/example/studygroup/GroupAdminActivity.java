package com.example.studygroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class GroupAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_admin);
        String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String location = getIntent().getExtras().getString("groupLocation");
        EditText subjectET = (EditText) findViewById(R.id.subjectAdminEdit);
        subjectET.setText(subject);
        EditText dateET = (EditText)findViewById(R.id.dateAdminEdit);
        dateET.setText(date);
        EditText locationET = (EditText)findViewById(R.id.locationAdminEdit);
        locationET.setText(location);
    }
}
