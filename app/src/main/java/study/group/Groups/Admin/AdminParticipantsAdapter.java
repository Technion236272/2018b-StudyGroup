package study.group.Groups.Admin;

import android.app.AlertDialog;
import android.content.Context;
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
import java.util.Date;

import study.group.R;

import static android.support.annotation.Dimension.DP;


class AdminParticipantsAdapter extends RecyclerView.Adapter<AdminParticipantsAdapter.adminPartHolder>{
    private ArrayList<Pair<String,String>> participants;
    String groupID;
    long currentNumOfParticipants;
    private DatabaseReference dataBase;
    Context groupContext;

    AdminParticipantsAdapter(ArrayList<Pair<String,String>> arr, String grpID,long currNumOfParticipants, Context context) {
        groupID = grpID;
        currentNumOfParticipants = currNumOfParticipants;
        participants = new ArrayList<>(arr);
        groupContext = context;
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
        holder.leaveAdmin.setVisibility(View.INVISIBLE);
        Pair<String,String> currentParticipant = participants.get(position);
        holder.participant.setText(currentParticipant.second);

        if((currentParticipant.first).equals(Profile.getCurrentProfile().getId())) {
     //       holder.leaveAdmin.setVisibility(View.VISIBLE);
            holder.removeUser.setVisibility(View.INVISIBLE);
     //       holder.removeUser.setText(R.string.leave);
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }


    class adminPartHolder extends RecyclerView.ViewHolder {
        TextView participant;
        Button removeUser;
        Button leaveAdmin;

        adminPartHolder(final View itemView) {
            super(itemView);
            participant = itemView.findViewById(R.id.adminPart);
            removeUser = itemView.findViewById(R.id.removeUser);
            leaveAdmin = itemView.findViewById(R.id.leaveAdmin);

            dataBase.child("Groups").child(groupID).child("currentNumOfPart").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        currentNumOfParticipants = (long)dataSnapshot.getValue();
                    } else {
                        currentNumOfParticipants = 0;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            removeUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
                    final int pos = getAdapterPosition();
                    final String currPartId = participants.get(pos).first;
                    alertDialog.setTitle("Are you sure you want to remove " + participants.get(pos).second + " from the group?");
                    alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dataBase.child("Groups").child(groupID).child("participants").child(currPartId).removeValue();
                            dataBase.child("Users").child(currPartId).child("Joined").child(groupID).removeValue();
                            --currentNumOfParticipants;
                            dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);

                            //adding a system message that the current participant has been removed from the group
                            String key = dataBase.child("Groups").child(groupID).child("Chat").push().getKey();
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("User").setValue(currPartId);
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Message").setValue("");
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("TimeStamp").setValue(new Date());
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Name").setValue(participants.get(pos).second);
                            String pc = Profile.getCurrentProfile().getProfilePictureUri(30,30).toString();
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("ProfilePicture").setValue(pc);
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("Type").setValue("System_Removed");
                            dataBase.child("Groups").child(groupID).child("Chat").child(key).child("GroupAdminID").setValue(Profile.getCurrentProfile().getId());

                        }
                    }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            });

//            leaveAdmin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if ((int) currentNumOfParticipants == 1) {
//                        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(itemView.getContext());
//                        alertDialog.setTitle(R.string.only_member_delete_group);
//                        alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dataBase.child("Groups").child(groupID).removeValue();
//                                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
//                                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child(groupID).removeValue();
//                                ((GroupAdminActivity)groupContext).finish();
//                            }
//                        }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }).show();
//                    } else {
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
//                        alertDialog.setTitle("Are you sure you want to leave the group?");
//                        alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dataBase.child("Groups").child(groupID).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                            if (!ds.getKey().equals(Profile.getCurrentProfile().getId())) {
//                                                String nextAdmin = ds.getKey();
//                                                dataBase.child("Groups").child(groupID).child("adminID").setValue(nextAdmin);
//                                                dataBase.child("Users").child(nextAdmin).child("myGroups").child(groupID).setValue("");
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                                dataBase.child("Groups").child(groupID).child("participants").child(Profile.getCurrentProfile().getId()).removeValue();
//                                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").child(groupID).removeValue();
//                                dataBase.child("Users").child(Profile.getCurrentProfile().getId()).child("myGroups").child(groupID).removeValue();
//                                --currentNumOfParticipants;
//                                dataBase.child("Groups").child(groupID).child("currentNumOfPart").setValue(currentNumOfParticipants);
//                                ((GroupAdminActivity)groupContext).finish();
//                            }
//                        }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }).show();
//                    }
//                }
//            });
        }

    }
}
