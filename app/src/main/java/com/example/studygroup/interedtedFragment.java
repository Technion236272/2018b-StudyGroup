package com.example.studygroup;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class interedtedFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Map<Integer, Course> interestedGroups;
    private static userInformationAboutGroupsAdapter adapter;
    private RecyclerView recyclerView;

    public interedtedFragment() {
        // Required empty public constructor
    }

    public static interedtedFragment newInstance() {
        return new interedtedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_interested, container, false);

        recyclerView = view.findViewById(R.id.interestedGroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new userInformationAboutGroupsAdapter(interestedGroups);
        recyclerView.setAdapter(adapter);

        return inflater.inflate(R.layout.fragment_interested, container, false);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
