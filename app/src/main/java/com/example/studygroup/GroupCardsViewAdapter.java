package com.example.studygroup;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupCardsViewAdapter extends RecyclerView.Adapter<GroupCardsViewAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;

    GroupCardsViewAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_group_item, viewGroup, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder viewHolder, int i) {
        Group group = groups.get(i);
        viewHolder.subject.setText(group.getSubject());
        viewHolder.date.setText(group.getDate());
        viewHolder.userState.setText("Not finished!");
        String sb = String.valueOf(group.getCurrentNumOfPart()) + "/" + String.valueOf(group.getmaxNumOfPart());
        viewHolder.numOfPart.setText(sb);
    }

    @Override
    public int getItemCount() {
        return this.groups.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView subject;
        TextView date;
        TextView userState;
        TextView numOfPart;
        // group picture

        GroupViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.groupCardView);
            subject = itemView.findViewById(R.id.groupSubjectCardView);
            date = itemView.findViewById(R.id.groupDateCardView);
            userState = itemView.findViewById(R.id.userStateInGroupCardView);
            numOfPart = itemView.findViewById(R.id.groupNumberOfParticipantsCardView);

        }
    }

}
