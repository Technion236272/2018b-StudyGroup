package study.group.Groups.Fragments.Requests;

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

import android.widget.SearchView;
import android.widget.Toast;

public class RequestsFragment extends Fragment {
    private GroupInformationAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Group> requests;

    private static String lastQuery = "";
    private static GroupInformationAdapter lastAdapter;

    public RequestsFragment() {
    }

    public static RequestsFragment newInstance() {
        return new RequestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_main, menu);
        MenuItem item = menu.findItem(R.id.search_main);
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
            //    Toast.makeText(getContext(), "Requests", Toast.LENGTH_SHORT).show();
                ArrayList<Group> filteredList = new ArrayList<>();
                for (Group g: requests) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_requests, container, false);

        requests = new ArrayList<>();

        recyclerView = view.findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDataBase.getReference();

        myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests = new ArrayList<>();
                final ArrayList<String> tempArray = new ArrayList<>();
                final Set<Group> tmpRequests = new HashSet<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    tempArray.add(d.getKey());
                }
                myRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            if(tempArray.contains(child.getKey())) {
                                Group g = child.getValue(Group.class);
                                tmpRequests.add(g);
                                requests.add(g);
                            }
                        }
                        adapter = new GroupInformationAdapter(new ArrayList<>(tmpRequests), R.id.requestsRecyclerView);
                        lastAdapter = new GroupInformationAdapter(new ArrayList<>(tmpRequests), R.id.requestsRecyclerView);
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
