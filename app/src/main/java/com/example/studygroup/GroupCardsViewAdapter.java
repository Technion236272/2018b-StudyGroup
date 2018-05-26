package com.example.studygroup;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class GroupCardsViewAdapter extends RecyclerView.Adapter<GroupCardsViewAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;

    GroupCardsViewAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_group_item, viewGroup, false);

        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();

        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder viewHolder, int i) {
        MyDatabaseUtil my = new MyDatabaseUtil();
        MyDatabaseUtil.getDatabase();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        final Group group = groups.get(i);
        viewHolder.subject.setText(group.getSubject());
        viewHolder.date.setText(group.getDate());
        viewHolder.userState.setText("Not finished!");
        String sb = String.valueOf(group.getCurrentNumOfPart()) + "/" + String.valueOf(group.getmaxNumOfPart());
        viewHolder.numOfPart.setText(sb);

        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(viewHolder.itemView.getContext());
                alertDialog.setTitle(viewHolder.subject.getText());
                alertDialog.setPositiveButton(R.string.request_to_join_group, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                }).show();
                alertDialog.setNegativeButton(R.string.group_of_interest, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myRef.child("Users")
                                .child(Profile.getCurrentProfile().getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Boolean flag = false;
                                User currentUser = new User();
                                currentUser.setToken(dataSnapshot.getValue(User.class).getToken());
                                currentUser.setName(dataSnapshot.getValue(User.class).getName());
                                currentUser.setInterested(dataSnapshot.getValue(User.class).getInterested());

                                String groupId = group.getId() + " - " + group.getSubject();

                                if(currentUser.getInterested() != null) {
                                    for(Map.Entry<String, String> current : currentUser.getInterested().entrySet()) {
                                        if(current.getKey().equals(groupId)) {
                                            flag = true;
                                        }
                                    }
                                }
                                if(!flag) {
                                    myRef.child("Users")
                                            .child(Profile.getCurrentProfile().getId())
                                            .child("interested")
                                            .child(groupId).setValue(group.getId());
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





                    }
                }).show();
            }
        });
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

        GroupViewHolder(final View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.groupCardView);
            subject = itemView.findViewById(R.id.groupSubjectCardView);
            date = itemView.findViewById(R.id.groupDateCardView);
            userState = itemView.findViewById(R.id.userStateInGroupCardView);
            numOfPart = itemView.findViewById(R.id.groupNumberOfParticipantsCardView);
        }
    }

    void something() {

    }

}
