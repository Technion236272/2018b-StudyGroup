package com.example.studygroup;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

class adminRequestsAdapter extends RecyclerView.Adapter<adminRequestsAdapter.adminHolder>{

    private ArrayList<String> requests;

    adminRequestsAdapter(ArrayList<String> arr)
    {
        requests = new ArrayList<>(arr);
    }

    @NonNull
    @Override
    public adminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_requests_adapter, parent, false);
        return new adminHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adminHolder holder, int position) {
        String currentParticipant = requests.get(position);
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
        }
    }
}
