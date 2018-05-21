package com.example.studygroup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.CourseHolder>{

    private ArrayList<Course> data;
    private Context context;

    public MyItemRecyclerViewAdapter() {
        this.data = new ArrayList<Course>();
    }

    public MyItemRecyclerViewAdapter(Context context, ArrayList<Course> data) {
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
    public void onBindViewHolder(CourseHolder holder, int position) {
   //     holder.courseFaculty.setText(data.get(position).getFaculty());
    //    holder.courseId.setText(data.get(position).getId());
        holder.courseName.setText(data.get(position).getId() + " - " + data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
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