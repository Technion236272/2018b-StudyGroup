package study.group.Groups.Participant;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import study.group.R;

public class GroupParticipantsAdapter extends RecyclerView.Adapter<GroupParticipantsAdapter.userHolder> {

    private ArrayList<String> participants;

    GroupParticipantsAdapter(ArrayList<String> arr) {
        participants = new ArrayList<>(arr);
    }

    @NonNull
    @Override
    public userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_participants_recycler, parent, false);
        return new userHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userHolder holder, int position) {
        String currentParticipant = participants.get(position);
        holder.participant.setText(currentParticipant);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    class userHolder extends RecyclerView.ViewHolder {
        TextView participant;

        userHolder(final View itemView) {
            super(itemView);

            participant = itemView.findViewById(R.id.participantInGroupRecycler);
        }
    }
}
