package study.group.Groups.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import study.group.R;


class AdminParticipantsAdapter extends RecyclerView.Adapter<AdminParticipantsAdapter.adminPartHolder>{
    private ArrayList<Pair<String,String>> participants;
    String groupID;
    long currentNumOfParticipants;
    private DatabaseReference dataBase;
    AdminParticipantsAdapter(ArrayList<Pair<String,String>> arr, String grpID,long currNumOfParticipants)
    {
        groupID = grpID;
        currentNumOfParticipants = currNumOfParticipants;
        participants = new ArrayList<>(arr);
        dataBase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public adminPartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_participants_adapter, parent, false);
        return new adminPartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adminPartHolder holder, int position) {
        Pair<String,String> currentParticipant = participants.get(position);
        holder.participant.setText(currentParticipant.second);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }


    class adminPartHolder extends RecyclerView.ViewHolder {
        TextView participant;
        Button removeUser;
        adminPartHolder(final View itemView) {
            super(itemView);
            participant = itemView.findViewById(R.id.adminPart);
            removeUser = itemView.findViewById(R.id.removeUser);

            dataBase.child("Groups").child(groupID).child("currentNumOfPart").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentNumOfParticipants = (long)dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            removeUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
                    int pos = getAdapterPosition();
                    final String currPartId = participants.get(pos).first;
                    alertDialog.setTitle("Are you sure you want to remove " + participants.get(pos).second + " from the group?");
                    alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dataBase.child("Groups").child(groupID).child("participants").child(currPartId).removeValue();
                            dataBase.child("Users").child(currPartId).child("Joined").child(groupID).removeValue();
                            --currentNumOfParticipants;
                            dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);
                        }
                    }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            });
        }
    }
}
