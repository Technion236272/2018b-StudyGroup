package study.group.Groups;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import study.group.R;
import study.group.Utilities.Group;

public class CreateGroup extends AppCompatActivity {
    private EditText groupSubject;
    private EditText Location;
    private Spinner numOfParticipants;
    private Button createButton;
    private DatabaseReference myRef;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private TextView mDisplayDate;
    private Boolean dateFlag;
    private TextView mDisplayTime;
    private Boolean timeFlag;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private String courseId;
    private String courseName;

    private Uri mImageUri;
    private Button mButtonChooseImage;
    private static final int PICK_IMAGE_REQUEST = 112;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String finalExc;
    private ProgressBar mProgressBar;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();
        groupSubject = findViewById(R.id.groupSubject);
        Location = findViewById(R.id.Location);
        numOfParticipants = findViewById(R.id.NumOfParticipants);
        createButton = findViewById(R.id.CreateGroup);
        mProgressBar = findViewById(R.id.imageProgress);

        mButtonChooseImage = findViewById(R.id.chooseImage);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        dateFlag = false;
        timeFlag = false;

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

        Calendar cal  = Calendar.getInstance();
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = cal.get(Calendar.MONTH);
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentHour = (cal.get(Calendar.HOUR_OF_DAY) + 3) % 24;
        final int currentMinute = cal.get(Calendar.MINUTE);

        mDisplayDate = findViewById(R.id.dateDisplay);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePicker = new DatePickerDialog(CreateGroup.this, android.app.AlertDialog.THEME_HOLO_DARK
                        , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateFlag = true;
                        CreateGroup.this.day = dayOfMonth;
                        CreateGroup.this.month = month + 1;
                        CreateGroup.this.year = year;
                        String text = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", CreateGroup.this.month) + "/" + year;
                        mDisplayDate.setText(text);
                    }
                }, currentYear, currentMonth, currentDay);
                mDatePicker.show();
            }
        });

        mDisplayTime = findViewById(R.id.timeDisplay);
        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(CreateGroup.this, android.app.AlertDialog.THEME_HOLO_DARK
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeFlag = true;
                        CreateGroup.this.minute = selectedMinute;
                        CreateGroup.this.hour = selectedHour;
                        String text = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute);
                        mDisplayTime.setText(text);
                    }
                }, currentHour, currentMinute, true);
                mTimePicker.show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
        }
    }


    public void openAlertDialog(View view) throws ParseException {

        final String key = myRef.child("Groups").push().getKey();
        StorageReference fileReference = mStorageRef.child(key);
        if (mImageUri != null) {
            finalExc = getFileExtension(mImageUri);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Uri img = taskSnapshot.getDownloadUrl();
                            mImageUri = img;
                            myRef.child("Groups").child(key).child("image").setValue(img.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateGroup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });

        }
        if(mImageUri == null) {
            mImageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/b-studygroup.appspot.com/o/uploads%2FStudyGroup1.png?alt=media&token=74e1942d-c459-4f5a-a5fa-c024f259fac0.png");
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final String subject = groupSubject.getText().toString();
        if(subject.length()==0) {
            alertDialog.setTitle(R.string.subjectError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }
        final String location = Location.getText().toString();
        if (location.length()==0) {
            alertDialog.setTitle(R.string.locationError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }

        if(!dateFlag) {
            alertDialog.setTitle(R.string.dateError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }
        final String date = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + String.valueOf(year);
        Calendar cal  = Calendar.getInstance();
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = cal.get(Calendar.MONTH) + 1;
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentHour = (cal.get(Calendar.HOUR_OF_DAY) + 3) % 24;
        final int currentMinute = cal.get(Calendar.MINUTE);

        if((year < currentYear) || (month < currentMonth && year <= currentYear) ||
                (day < currentDay && month <= currentMonth && year <= currentYear)) {
            alertDialog.setTitle(R.string.irrelevant_date);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }

        if(!timeFlag) {
            alertDialog.setTitle(R.string.timeError);
            alertDialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).show();
            return;
        }

        if((hour < currentHour && day <= currentDay && month <= currentMonth && year <= currentYear) ||
                (minute < currentMinute && hour <= currentHour && day <= currentDay && month <= currentMonth && year <= currentYear)) {
            alertDialog.setTitle(R.string.irrelevant_time);
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

        final Integer numOfPart = Integer.parseInt((numOfParticipants.getSelectedItem().toString()));
        final Integer current = 1;
        final String time = String.format("%02d", hour) + ":" + String.format("%02d", minute);

//        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                myRef.child("Groups").child(key).child("image").setValue(uri.toString());
//            }
//        });

        Group newGroup = new Group(key,courseId, subject, date, location, numOfPart, current,
                Profile.getCurrentProfile().getId(), time, mImageUri.toString());

        myRef.child("Groups").child(key).setValue(newGroup);
        myRef.child("Groups").child(key).child("participants").child(Profile.getCurrentProfile().getId())
                .setValue(Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName());
        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child(key).setValue(subject);
        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(key).setValue(subject);
        final Map<String, Object> notification = new HashMap<>();
        String newGroupCreated = "Hi, "+subject+ "was created at " + courseName;
        notification.put("Notification", newGroupCreated);
        notification.put("Type","New Group");
        notification.put("Admin",Profile.getCurrentProfile().getFirstName());
        myRef.child("Courses").child(courseId).child("Followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    if(!d.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        mFirestore.collection("Users/"+d.getKey()+"/Notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(CreateGroup.this,"Notification Sent",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
