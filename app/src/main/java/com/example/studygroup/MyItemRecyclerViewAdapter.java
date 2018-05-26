package com.example.studygroup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.CourseHolder> {

    private static Map<Integer, Course> data;

    MyItemRecyclerViewAdapter(Map<Integer, Course> data) {
        MyItemRecyclerViewAdapter.data = data;
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_course_item, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int key) {
        Course c = data.get(key);
        String sb = c.getId() + " - " + c.getName();
        holder.courseName.setText(sb);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterList(Map<Integer, Course> filteredList) {
        data = filteredList;
        notifyDataSetChanged();
    }

    static class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseName;

        CourseHolder(final View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Intent intent = new Intent(itemView.getContext().getApplicationContext(), GroupsInACourseActivity.class);
                    intent.putExtra("courseId", data.get(position).getId());
                    intent.putExtra("courseName", data.get(position).getName());


                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

}