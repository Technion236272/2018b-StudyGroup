package com.example.studygroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateGroup extends AppCompatActivity {
    private EditText groupSubject;
    private EditText Location;
    private EditText Date;
    private Spinner numOfParticipants;
    private Button createButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupSubject = (EditText)findViewById(R.id.groupSubject);
        Location = (EditText)findViewById(R.id.Location);
        Date = (EditText)findViewById(R.id.DateAndTime);
        numOfParticipants = (Spinner)findViewById(R.id.NumOfParticipants);
        createButton = (Button)findViewById(R.id.CreateGroup);

        createButton.setOnClickListener(new View.OnClickListener()
        {
            //TODO: check the Correctness of the input
            @Override
            public void onClick(View view)
            {
                String subject = groupSubject.getText().toString();
                String location = Location.getText().toString();
                String date = Date.getText().toString();
                int numOfPart = (int)numOfParticipants.getSelectedItem();
                
            }
        });

    }
}
