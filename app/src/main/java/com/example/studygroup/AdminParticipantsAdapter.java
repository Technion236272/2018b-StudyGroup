package com.example.studygroup;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


class AdminParticipantsAdapter extends RecyclerView.Adapter<AdminParticipantsAdapter.adminPartHolder>{
    private ArrayList<String> participants;

    AdminParticipantsAdapter(ArrayList<String> arr)
    {
        participants = new ArrayList<>(arr);
    }

    @NonNull
    @Override
    public adminPartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_participants_adapter, parent, false);
        return new adminPartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adminPartHolder holder, int position) {
        String currentParticipant = participants.get(position);
        holder.participant.setText(currentParticipant);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    class adminPartHolder extends RecyclerView.ViewHolder {
        TextView participant;
        adminPartHolder(final View itemView) {
            super(itemView);
            participant = itemView.findViewById(R.id.adminPart);
        }
    }

}
