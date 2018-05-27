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
    private static Map<String, Group> data;

    userInformationAboutGroupsAdapter() {}

    userInformationAboutGroupsAdapter(Map<String, Group> data) {
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
        Group group = data.get(key);
        holder.subject.setText(group.getSubject());
        holder.idAndName.setText(group.getId());
        holder.date.setText(group.getDate());
        String sb = String.valueOf(group.getCurrentNumOfPart()) + "/" + String.valueOf(group.getmaxNumOfPart());
        holder.participants.setText(sb);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class InfoHolder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView idAndName;
        TextView date;
        TextView participants;

        InfoHolder(final View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.groupSubjectInfo);
            idAndName = itemView.findViewById(R.id.groupIdAndName);
            date = itemView.findViewById(R.id.groupMeetingDate);
            participants = itemView.findViewById(R.id.groupNumberOfParticipantsInfo);
        }
    }
}
