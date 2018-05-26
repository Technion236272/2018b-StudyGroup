package com.example.studygroup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public class userInformationAboutGroupsAdapter extends RecyclerView.Adapter<userInformationAboutGroupsAdapter.InfoHolder>{
    private static Map<Integer, Course> data;

    userInformationAboutGroupsAdapter() {}

    userInformationAboutGroupsAdapter(Map<Integer, Course> data) {
        userInformationAboutGroupsAdapter.data = data;
    }

    @NonNull
    @Override
    public userInformationAboutGroupsAdapter.InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_information_item, parent, false);
        return new userInformationAboutGroupsAdapter.InfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userInformationAboutGroupsAdapter.InfoHolder holder, int key) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class InfoHolder extends RecyclerView.ViewHolder {
        TextView courseName;

        InfoHolder(final View itemView) {
            super(itemView);
        }
    }
}
