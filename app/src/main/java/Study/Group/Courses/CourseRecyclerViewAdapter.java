package Study.Group.Courses;

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

import Study.Group.Groups.CourseGroups.GroupsInACourseActivity;
import Study.Group.R;
import Study.Group.Utilities.Course;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.CourseHolder> {

    CourseRecyclerViewAdapter oA;
    private int favouritesCount, filteredCount;
    private ArrayList<Course> filteredList;

    CourseRecyclerViewAdapter(ArrayList<Course> filteredList, int originalFavouritesCount, int filteredCount, CourseRecyclerViewAdapter otherAdapter) {
        this.filteredList = new ArrayList<>(filteredList);
        this.favouritesCount = originalFavouritesCount;
        this.filteredCount = filteredCount;
        oA= otherAdapter;
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
                                    if(oA == null){
                                        if(favouritesCount > 0) {
                                            favouritesCount--;
                                        }
                                        filteredList.remove(position);
                                        filteredList.add((c.indexInAdapter>favouritesCount)?c.indexInAdapter:favouritesCount,c);
                                    }else{
                                        if(favouritesCount>0) {
                                            favouritesCount--;
                                        }if(filteredCount>0) {
                                            filteredCount--;
                                        }
                                        filteredList.remove(position);
                                        filteredList.add((c.indexInFilteredAdapter>filteredCount)?c.indexInFilteredAdapter:filteredCount,c);
                                        oA.filteredList.remove(c.indexInAdapter);
                                        oA.filteredList.add((c.indexInAdapter>favouritesCount)?c.indexInAdapter:favouritesCount,c);
                                    }
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
                            if(oA == null){

                                filteredList.remove(position);
                                filteredList.add((c.indexInAdapter>favouritesCount)?favouritesCount:c.indexInAdapter,c);
                                favouritesCount++;
//                                favouritesCount++;
                            }else{

                                filteredList.remove(position);
                                filteredList.add((c.indexInFilteredAdapter>filteredCount)?filteredCount:c.indexInFilteredAdapter,c);
                                oA.filteredList.remove(c.indexInAdapter);
                                oA.filteredList.add((c.indexInAdapter>favouritesCount)?favouritesCount:c.indexInAdapter,c);
                                favouritesCount++;
                                filteredCount++;
                            }

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