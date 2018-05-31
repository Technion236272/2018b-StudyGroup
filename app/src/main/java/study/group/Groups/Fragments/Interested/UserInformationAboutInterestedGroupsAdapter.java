package study.group.Groups.Fragments.Interested;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import study.group.Groups.Admin.GroupAdminActivity;
import study.group.Groups.Participant.GroupActivity;
import study.group.R;
import study.group.Utilities.Group;
import study.group.Utilities.MyDatabaseUtil;

/*
 * userInformationAboutInterestedGroupsAdapter.
 */
public class UserInformationAboutInterestedGroupsAdapter extends RecyclerView.Adapter<UserInformationAboutInterestedGroupsAdapter.InfoHolder> {

    private ArrayList<Group> data;

    UserInformationAboutInterestedGroupsAdapter(ArrayList<Group> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_information_item, parent, false);
        return new InfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoHolder holder, int key) {
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

    class InfoHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerItem;
        TextView subject;
        TextView idAndName;
        TextView date;
        TextView participants;
        Button interestedButton;

        InfoHolder(final View itemView) {
            super(itemView);
            recyclerItem = itemView.findViewById(R.id.interestedGroupsRecyclerView);
            subject = itemView.findViewById(R.id.groupSubjectInfo);
            idAndName = itemView.findViewById(R.id.groupIdAndName);
            date = itemView.findViewById(R.id.groupMeetingDate);
            participants = itemView.findViewById(R.id.groupNumberOfParticipantsInfo);

            MyDatabaseUtil my = new MyDatabaseUtil();
            MyDatabaseUtil.getDatabase();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Group group = data.get(getAdapterPosition());
                    if (group.getAdminID().equals(Profile.getCurrentProfile().getId())) {
                        Intent adminGroup = new Intent(v.getContext(), GroupAdminActivity.class);
                        adminGroup.putExtra("groupSubject", group.getSubject());
                        adminGroup.putExtra("groupDate", group.getDate());
                        adminGroup.putExtra("groupID", group.getGroupID());
                        adminGroup.putExtra("groupLocation", group.getLocation());
                        adminGroup.putExtra("numOfParticipants", group.getCurrentNumOfPart());
                        adminGroup.putExtra("adminID", group.getAdminID());
                        adminGroup.putExtra("groupName", group.getName());

                        v.getContext().startActivity(adminGroup);
                    } else {
                        Intent userGroup = new Intent(v.getContext(), GroupActivity.class);
                        userGroup.putExtra("groupSubject", group.getSubject());
                        userGroup.putExtra("groupDate", group.getDate());
                        userGroup.putExtra("groupID", group.getGroupID());
                        userGroup.putExtra("groupLocation", group.getLocation());
                        userGroup.putExtra("numOfParticipants", group.getCurrentNumOfPart());
                        userGroup.putExtra("adminID", group.getAdminID());
                        userGroup.putExtra("groupName", group.getName());
                        v.getContext().startActivity(userGroup);
                    }
                }

            });
        }
    }
}
