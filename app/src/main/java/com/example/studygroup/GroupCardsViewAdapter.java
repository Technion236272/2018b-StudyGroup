package com.example.studygroup;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupCardsViewAdapter extends RecyclerView.Adapter<GroupCardsViewAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;

    public GroupCardsViewAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_group_item, viewGroup, false);
        GroupViewHolder gvh = new GroupViewHolder(v);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder viewHolder, int i) {
        Group group = groups.get(i);
        viewHolder.subject.setText(group.getSubject());
        viewHolder.date.setText(group.getDate());
        viewHolder.userState.setText("Not finished!");
        StringBuilder sb = new StringBuilder(group.getCurrentNumOfPart()).append("/").append(group.getmaxNumOfPart());
        viewHolder.numOfPart.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return this.groups.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView subject;
        TextView date;
        TextView userState;
        TextView numOfPart;
        // group picture

        GroupViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.groupCardView);
            subject = (TextView) itemView.findViewById(R.id.groupSubjectCardView);
            date = (TextView) itemView.findViewById(R.id.groupDateCardView);
            userState = (TextView) itemView.findViewById(R.id.userStateInGroupCardView);
            numOfPart = (TextView) itemView.findViewById(R.id.groupNumberOfParticipantsCardView);

        }
    }

}
