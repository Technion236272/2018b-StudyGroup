package study.group.Groups.CourseGroups;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import study.group.Groups.Admin.GroupAdminActivity;
import study.group.Groups.Chat;
import study.group.Groups.Participant.GroupActivity;
import study.group.R;
import study.group.Utilities.Group;
import study.group.Utilities.MyDatabaseUtil;

public class GroupCardsViewAdapter extends RecyclerView.Adapter<GroupCardsViewAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;
    private String id;
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
        boolean isJoined = false;
        final Group group = groups.get(i);
        viewHolder.subject.setText(group.getSubject());
        viewHolder.date.setText(group.getDate());
        viewHolder.userState.setText(R.string.admin);
        String sb = String.valueOf(group.getCurrentNumOfPart()) + "/" + String.valueOf(group.getmaxNumOfPart());
        viewHolder.numOfPart.setText(sb);

        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group.getAdminID().equals(Profile.getCurrentProfile().getId()))
                {
                    Intent adminGroup = new Intent(v.getContext(), Chat.class);
                    adminGroup.putExtra("groupSubject",group.getSubject());
                    adminGroup.putExtra("groupDate",group.getDate());
                    adminGroup.putExtra("groupID",group.getGroupID());
                    adminGroup.putExtra("groupLocation",group.getLocation());
                    adminGroup.putExtra("numOfParticipants",group.getCurrentNumOfPart());
                    adminGroup.putExtra("adminID",group.getAdminID());
                    adminGroup.putExtra("groupName",group.getName());

                    v.getContext().startActivity(adminGroup);
                }
                else
                {
                    Intent userGroup = new Intent(v.getContext(), Chat.class);
                    userGroup.putExtra("groupSubject",group.getSubject());
                    userGroup.putExtra("groupDate",group.getDate());
                    userGroup.putExtra("groupID",group.getGroupID());
                    userGroup.putExtra("groupLocation",group.getLocation());
                    userGroup.putExtra("numOfParticipants",group.getCurrentNumOfPart());
                    userGroup.putExtra("adminID",group.getAdminID());
                    userGroup.putExtra("groupName",group.getName());
                    v.getContext().startActivity(userGroup);
                }


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
