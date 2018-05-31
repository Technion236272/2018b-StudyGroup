package study.group.Groups.Fragments.Interested;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import study.group.Groups.Fragments.GroupInformationAdapter;
import study.group.R;
import study.group.Utilities.Group;
import study.group.Utilities.MyDatabaseUtil;

public class InterestedFragment extends Fragment {
    private GroupInformationAdapter adapter;
    private RecyclerView recyclerView;


    public InterestedFragment() {
        // Required empty public constructor
    }

    public static InterestedFragment newInstance() {
        return new InterestedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_interested, container, false);

        recyclerView = view.findViewById(R.id.interestedGroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

        final ArrayList<String> temp = new ArrayList<>();

        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("interested").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> tempArray = new ArrayList<>();
                final Set<Group> tmpInterested = new HashSet<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    tempArray.add(d.getKey());
//
                }
                myRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            if(tempArray.contains(child.getKey())) {
                                Group g = child.getValue(Group.class);
                                tmpInterested.add(g);
                            }
                        }
                        adapter = new GroupInformationAdapter(new ArrayList<>(tmpInterested));
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
