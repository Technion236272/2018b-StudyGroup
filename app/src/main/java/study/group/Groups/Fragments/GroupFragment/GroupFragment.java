package study.group.Groups.Fragments.GroupFragment;

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
import study.group.Utilities.ConnectionDetector;

public class GroupFragment extends Fragment {
    private static String lastQuery = "";
    private static GroupInformationAdapter lastAdapter;
    private String type;
    private GroupInformationAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Group> allRelevantGroups;
    private int recyclerViewId, layoutId;

    public GroupFragment() {
    }

    public void setType(String t) {
        this.type = t;
    }

    public void setRecyclerView(int id) {
        this.recyclerViewId = id;
    }

    public void setLayout(int id) {
        this.layoutId = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checking connection
        ConnectionDetector cd = new ConnectionDetector(getContext());
        cd.isConnected();
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_menu, menu);
        MenuItem item = menu.findItem(R.id.groups_menu);
        item.setVisible(true);
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
                for (Group g : allRelevantGroups) {
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
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(layoutId, container, false);

        allRelevantGroups = new ArrayList<>();

        recyclerView = view.findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allRelevantGroups = new ArrayList<>();
                final ArrayList<String> tempArray = new ArrayList<>();
                final Set<Group> tempRelevantGroups = new HashSet<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    tempArray.add(d.getKey());
                }
                myRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (tempArray.contains(child.getKey())) {
                                Group g = child.getValue(Group.class);
                                tempRelevantGroups.add(g);
                                allRelevantGroups.add(g);
                            }
                        }
                        adapter = new GroupInformationAdapter(new ArrayList<>(tempRelevantGroups), recyclerViewId);
                        lastAdapter = new GroupInformationAdapter(new ArrayList<>(tempRelevantGroups), recyclerViewId);
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
}
