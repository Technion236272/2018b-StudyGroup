package study.group.Groups.CourseGroups;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Map;

import study.group.Groups.Chat.Chat;
import study.group.Groups.CreateGroup;
import study.group.Groups.Participant.GroupActivity;
import study.group.R;
import study.group.Utilities.Group;
import study.group.Utilities.MyDatabaseUtil;

public class GroupCardsViewAdapter extends RecyclerView.Adapter<GroupCardsViewAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;
    private boolean isJoined = false;
    private Resources resources;
    private Context context;
    private Transformation transformation;

    GroupCardsViewAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_group_item, viewGroup, false);
        resources = v.getResources();
        context = v.getContext();

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

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        StorageReference fileReference = mStorageRef.child(group.getGroupID());
        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    String downloadURL = task.getResult().toString();
                    Picasso.with(context)
                            .load(downloadURL)
                            .fit()
                            .transform(transformation)
                            .into(viewHolder.groupPhoto);
                }
            }
        });

        String userStatus = getUserStatus(group);
        viewHolder.userState.setText(userStatus);

        switch (userStatus) {
            case "Admin":  viewHolder.userState.setBackground(ContextCompat.getDrawable(context, R.drawable.admin_bubble));
                break;
            case "Joined": viewHolder.userState.setBackground(ContextCompat.getDrawable(context, R.drawable.join_bubble));
                break;
            case "Requested": viewHolder.userState.setBackground(ContextCompat.getDrawable(context, R.drawable.request_bubble));
                break;
            case "Interested": viewHolder.userState.setBackground(ContextCompat.getDrawable(context, R.drawable.interested_bubble));
                break;
            default: viewHolder.userState.setBackground(ContextCompat.getDrawable(context, R.drawable.empty_bubble));
                break;
        }



        String sb = String.valueOf(group.getCurrentNumOfPart()) + "/" + String.valueOf(group.getmaxNumOfPart());
        viewHolder.numOfPart.setText(sb);

        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("Joined").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isJoined = false;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(ds.getKey().equals(group.getGroupID())) {
                                isJoined = true;
                            }
                        }
                        String nextAdmin = group.getAdminID();
                        if(group.getParticipants().size() > 1) {
                            for(Map.Entry<String, String> user : group.getParticipants().entrySet()) {
                                if(!user.getKey().equals(group.getAdminID())) {
                                    nextAdmin = user.getKey();
                                    break;
                                }
                            }
                        }
                        if(group.getAdminID().equals(Profile.getCurrentProfile().getId())) {
                            Intent adminGroup = new Intent(v.getContext(), Chat.class);
                            adminGroup.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            adminGroup.putExtra("groupSubject",group.getSubject());
                            adminGroup.putExtra("groupDate",group.getDate());
                            adminGroup.putExtra("groupTime",group.getTime());
                            adminGroup.putExtra("groupID",group.getGroupID());
                            adminGroup.putExtra("groupLocation",group.getLocation());
                            adminGroup.putExtra("numOfParticipants",group.getCurrentNumOfPart());
                            adminGroup.putExtra("adminID",group.getAdminID());
                            adminGroup.putExtra("groupName",group.getName());
                            adminGroup.putExtra("groupCurrentParticipants",group.getCurrentNumOfPart());
                            adminGroup.putExtra("nextAdmin", nextAdmin);
                            v.getContext().startActivity(adminGroup);
                        } else {
                            Intent userGroup;
                            if(isJoined) {
                                userGroup = new Intent(v.getContext(), Chat.class);
                            } else {
                                userGroup = new Intent(v.getContext(), GroupActivity.class);
                            }
                            userGroup.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            userGroup.putExtra("groupSubject",group.getSubject());
                            userGroup.putExtra("groupDate",group.getDate());
                            userGroup.putExtra("groupTime",group.getTime());
                            userGroup.putExtra("groupID",group.getGroupID());
                            userGroup.putExtra("groupLocation",group.getLocation());
                            userGroup.putExtra("numOfParticipants",group.getCurrentNumOfPart());
                            userGroup.putExtra("adminID",group.getAdminID());
                            userGroup.putExtra("groupName",group.getName());
                            userGroup.putExtra("maxNumOfPart",group.getmaxNumOfPart());
                            userGroup.putExtra("groupCurrentParticipants",group.getCurrentNumOfPart());
                            userGroup.putExtra("nextAdmin", nextAdmin);
                            v.getContext().startActivity(userGroup);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });




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

    private String getUserStatus(Group group) {
        final String userID= Profile.getCurrentProfile().getId();

        if(group.getAdminID().equals(userID)) {

            return "Admin";
        }
        if(group.getParticipants() != null) {
            if(group.getParticipants().containsKey(userID)) {
                return "Joined";
            }
        }
        if(group.getRequests() != null) {
            if(group.getRequests().containsKey(userID)) {
                return "Requested";
            }
        }
        if(group.getInterested() != null) {
            if(group.getInterested().containsKey(userID)) {
                return "Interested";
            }
        }
        return "Empty";
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView subject;
        TextView date;
        TextView userState;
        TextView numOfPart;
        ImageView groupPhoto;
        // group picture

        GroupViewHolder(final View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.groupCardView);
            subject = itemView.findViewById(R.id.groupSubjectCardView);
            date = itemView.findViewById(R.id.groupDateCardView);
            userState = itemView.findViewById(R.id.userStateInGroupCardView);
            numOfPart = itemView.findViewById(R.id.groupNumberOfParticipantsCardView);
            groupPhoto = itemView.findViewById(R.id.groupPhotoCardView);
        }
    }


}
