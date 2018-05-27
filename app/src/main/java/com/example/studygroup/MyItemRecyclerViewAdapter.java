package com.example.studygroup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.CourseHolder> {

    private ArrayList< Course> filteredList;
    private int favouritesCount, filteredCount;
    MyItemRecyclerViewAdapter oA;
    MyItemRecyclerViewAdapter(ArrayList<Course> filteredList,int originalFavouritesCount,int filteredCount,MyItemRecyclerViewAdapter otherAdapter) {
        this.filteredList = new ArrayList<>(filteredList);
        this.favouritesCount = originalFavouritesCount;
        oA= otherAdapter;
        this.filteredCount = filteredCount;
    }



    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_course_item, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int key) {
        Course c = filteredList.get(key);

        String sb = c.getId() + " - " + c.getName();
        holder.courseName.setText(sb);
        CheckBox checkBox = holder.itemView.findViewById(R.id.favouriteButton);
        checkBox.setChecked(c.isFav());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterList(ArrayList<Course> filteredList,int filteredCount) {
        this.filteredCount = filteredCount;
        this.filteredList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        CheckBox favouriteButton;
        CourseHolder(final View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseName);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Intent intent = new Intent(itemView.getContext().getApplicationContext(), GroupsInACourseActivity.class);
                    intent.putExtra("courseId", filteredList.get(position).getId());
                    intent.putExtra("courseName", filteredList.get(position).getName());
                    itemView.getContext().startActivity(intent);
                }
            });
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference();
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("FavouriteCourses").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d : dataSnapshot.getChildren()){
                                if(((String)d.getValue()).equals(filteredList.get(position).getId())){
                                    d.getRef().removeValue();
                                    Course c = filteredList.get(position);
                                    c.setFav(false);
                                    filteredList.remove(position);
                                    filteredList.add((c.filteredIndex > filteredCount)?c.filteredIndex :filteredCount,c);

                                    if(oA!= null){
                                        oA.filteredList.remove(c.originalIndex);
                                        oA.filteredList.add((c.originalIndex >favouritesCount)?c.originalIndex :favouritesCount,c);
                                    }
                                    favouritesCount--;
                                    filteredCount--;
                                    CheckBox cc = itemView.findViewById(R.id.favouriteButton);
                                    cc.setChecked(false);
                                    notifyDataSetChanged();
                                    return;
                                }
                            }
                            myRef.child("Users").child(Profile.getCurrentProfile().getId()).
                                    child("FavouriteCourses").child(filteredList.get(position).getName()).setValue(filteredList.get(position).getId());
                            Course c = filteredList.get(position);
                            c.setFav(true);
                            filteredList.remove(position);
                            filteredList.add((filteredCount>c.filteredIndex)?c.filteredIndex :filteredCount,c);
                            if(oA!= null){
                                oA.filteredList.remove(c);
                                oA.filteredList.add((favouritesCount>c.originalIndex)?c.originalIndex :favouritesCount,c);
                            }
                            favouritesCount++;
                            filteredCount++;
                            CheckBox cc = itemView.findViewById(R.id.favouriteButton);
                            cc.setChecked(true);
                            notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

        }
    }

}