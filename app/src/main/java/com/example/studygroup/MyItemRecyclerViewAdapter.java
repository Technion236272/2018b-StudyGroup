package com.example.studygroup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.CourseHolder> {

    private Map<Integer, Course> data;
    private Context context;

    public MyItemRecyclerViewAdapter() {
        this.data = new HashMap<>();
    }

    public MyItemRecyclerViewAdapter(Context context, Map<Integer, Course> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_course_item, parent, false);
        CourseHolder holder = new CourseHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CourseHolder holder, int key) {
   //     holder.courseFaculty.setText(data.get(position).getFaculty());
    //    holder.courseId.setText(data.get(position).getId());

        Course c = data.get(key);
        StringBuilder sb = new StringBuilder(c.getId());
        sb.append(" - ");
        sb.append(c.getName());
        holder.courseName.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterList(Map<Integer, Course> filteredList) {
        data = filteredList;
        notifyDataSetChanged();
    }

    static class CourseHolder extends RecyclerView.ViewHolder
    {
    //    TextView courseFaculty;
    //    TextView courseId;
        TextView courseName;
        private Context mContext;

        public CourseHolder(View itemView) {
            super(itemView);
         //   courseFaculty = (TextView)itemView.findViewById(R.id.courseFaculty);
           //    courseId = (TextView)itemView.findViewById(R.id.courseId);
               courseName = (TextView)itemView.findViewById(R.id.courseName);
               mContext = itemView.getContext();
        }
    }

}