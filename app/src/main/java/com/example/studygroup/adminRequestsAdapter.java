package com.example.studygroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

class adminRequestsAdapter extends RecyclerView.Adapter<adminRequestsAdapter.adminHolder>{

    private ArrayList<User> requests;
    private String groupID;
    private Context context;
    adminRequestsAdapter(ArrayList<User> arr,String g)
    {
        requests = new ArrayList<>(arr);
        groupID = g;
    }

    @NonNull
    @Override
    public adminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_requests_adapter, parent, false);
        context = parent.getContext();
        return new adminHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adminHolder holder, int position) {
        String currentParticipant = requests.get(position).getName();
        holder.requestsText.setText(currentParticipant);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class adminHolder extends RecyclerView.ViewHolder {
        TextView requestsText;


        adminHolder(final View itemView) {
            super(itemView);
            requestsText = itemView.findViewById(R.id.userRequest);
            Button acceptBtn = itemView.findViewById(R.id.AcceptBtn);
            Button ignoreBtn = itemView.findViewById(R.id.IgnoreBtn);
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = requests.get(getAdapterPosition());
                    database.child("Users").child(user.getToken()).
                            child("Requests").child(groupID).removeValue();
                    database.child("Groups").child("Requests").child(user.getToken()).removeValue();

                    database.child("Groups").child("participants")
                            .child(user.getToken()).setValue(user.getName());
                    CharSequence text = "Request accepted!";

                    Toast.makeText(context,text,Toast.LENGTH_LONG);
                }
            });

            ignoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = requests.get(getAdapterPosition());
                    database.child("Users").child(user.getToken()).
                            child("Requests").child(groupID).removeValue();

                    database.child("Groups").child("Requests").child(user.getToken()).removeValue();

                    CharSequence text = "Request declined!";
                    Toast.makeText(context,text,Toast.LENGTH_LONG);
                }
            });

        }
    }
}
