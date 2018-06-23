package study.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import study.group.Courses.CoursesFragment;
import study.group.Groups.Fragments.GroupsFragment;
import study.group.Utilities.Credits;
import study.group.Utilities.MyDatabaseUtil;
import study.group.Utilities.ConnectionDetector;

public class MainActivity extends AppCompatActivity {
    GroupsFragment gf;
    CoursesFragment cf;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        gf = new GroupsFragment();
        cf = new CoursesFragment();
        cf.setGroupsFragment(gf);
        insertUserInfoToDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //checking connection
        ConnectionDetector cd = new ConnectionDetector(this);
        cd.isConnected();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Toast.makeText(this, "Settings will be available soon", Toast.LENGTH_LONG).show();
        } else if(id == R.id.credits) {
            Intent intent = new Intent(this, Credits.class);
            startActivity(intent);
        } else {
            String userId = mAuth.getUid();

            Map<String,Object> m = new HashMap<>();
            m.put("token_id", FieldValue.delete());

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("Users").document(Profile.getCurrentProfile().getId()).update(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LoginManager.getInstance().logOut();
                    mAuth.signOut();
                    finish();
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want exit the app?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                moveTaskToBack(true);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return gf;
                default:
                    return cf;

            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    private void insertUserInfoToDatabase() {
        final MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag = false;
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getKey().equals(Profile.getCurrentProfile().getId())) {
                        flag = true;
                        // more code
                    }
                }
                if(!flag) {
                    String fullName = Profile.getCurrentProfile().getFirstName() + " " +
                            Profile.getCurrentProfile().getLastName();
                    class UserData{
                        String token, name,profile;

                        UserData(String t,String n,Uri p){
                            token = t;
                            name = n;
                            profile = p.getPath();
                        }
                    }
                    UserData user = new UserData(Profile.getCurrentProfile().getId(), fullName,Profile.getCurrentProfile().getProfilePictureUri(30,30));

                    myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("name").setValue(user.name);
                    myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("profile").setValue(user.profile);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
