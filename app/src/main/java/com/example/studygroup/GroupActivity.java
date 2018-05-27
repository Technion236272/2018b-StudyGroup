package com.example.studygroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String subject = getIntent().getExtras().getString("groupSubject");
        String date = getIntent().getExtras().getString("groupDate");
        String location = getIntent().getExtras().getString("groupLocation");
        TextView subjectTV = (TextView)findViewById(R.id.SubjectInGroupContent);
        subjectTV.setText(subject);
        TextView dateTV = (TextView)findViewById(R.id.DateInGroupContent);
        dateTV.setText(date);
        TextView locationTV = (TextView)findViewById(R.id.LocationInGroupContent);
        locationTV.setText(location);
    }
}
