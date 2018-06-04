package study.group.Groups.Fragments.Joined;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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


public class joinedFragment extends Fragment {
    private GroupInformationAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Group> groups;

    private static String lastQuery = "";
    private static GroupInformationAdapter lastAdapter;

    public joinedFragment() {
        // Required empty public constructor
    }

    public static joinedFragment newInstance() {
        return new joinedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.joined_menu, menu);
        MenuItem item = menu.findItem(R.id.search_joined);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQuery(lastQuery, true);
        recyclerView.setAdapter(lastAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Group> filteredList = new ArrayList<>();
                for (Group g: groups) {
                    if (g.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(g);
                    }
                }
                lastQuery = newText;
                lastAdapter.filterList(filteredList);
                recyclerView.setAdapter(lastAdapter);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_joined, container, false);
        groups = new ArrayList<>();

        recyclerView = view.findViewById(R.id.joinedGroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

   //     final ArrayList<String> temp = new ArrayList<>();

        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDataBase.getReference();


        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups = new ArrayList<>();
                final Set<Group> tmpJoined = new HashSet<>();
                final ArrayList<String> tempArray = new ArrayList<>();
//                final ArrayList<Study.Study.Study.Study.Study.Group> newJoined = new ArrayList<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    tempArray.add(d.getKey());
                }
                myRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            if(tempArray.contains(child.getKey())) {
                                Group g = child.getValue(Group.class);
                                tmpJoined.add(g);
                                groups.add(g);
                            }
                        }
                        adapter = new GroupInformationAdapter(new ArrayList<>(tmpJoined), R.id.joinedGroupsRecyclerView);
                        lastAdapter = new GroupInformationAdapter(new ArrayList<>(tmpJoined), R.id.joinedGroupsRecyclerView);
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
