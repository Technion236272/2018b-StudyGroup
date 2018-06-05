package study.group.Courses;

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
import java.util.TreeMap;

import study.group.Groups.CourseGroups.GroupsInACourseActivity;
import study.group.R;
import study.group.Utilities.Course;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.CourseHolder> {

    CourseRecyclerViewAdapter oA;
    ArrayList<Course> favouriteCourses, otherCourses;
    TreeMap<String, Course> favouritesMap, othersMap;

    CourseRecyclerViewAdapter(TreeMap<String, Course> favouritesMap, TreeMap<String, Course> othersMap, CourseRecyclerViewAdapter o) {
        favouriteCourses = new ArrayList<>(favouritesMap.values());
        otherCourses = new ArrayList<>(othersMap.values());
        this.favouritesMap = favouritesMap;
        this.othersMap = othersMap;
        oA = o;
    }


    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_course_item, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int key) {
        @NonNull Course c;
        if (key < favouriteCourses.size()) {
            c = favouriteCourses.get(key);
        } else {
            c = otherCourses.get(key - favouriteCourses.size());
        }

        String sb = c.getId() + " - " + c.getName();
        holder.courseName.setText(sb);
        CheckBox checkBox = holder.itemView.findViewById(R.id.favouriteButton);
        checkBox.setChecked(c.isFav());
    }

    @Override
    public int getItemCount() {
        return favouriteCourses.size() + otherCourses.size();
    }

    public void filter(TreeMap<String, Course> filteredFavouritesMap, TreeMap<String, Course> filteredOthersMap) {
        favouritesMap = filteredFavouritesMap;
        othersMap = filteredOthersMap;
        favouriteCourses = new ArrayList<>(favouritesMap.values());
        otherCourses = new ArrayList<>(othersMap.values());
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
                    @NonNull String id, name;
                    if (position < favouriteCourses.size()) {
                        id = favouriteCourses.get(position).getId();
                        name = favouriteCourses.get(position).getName();
                    } else {
                        id = otherCourses.get(position - favouriteCourses.size()).getId();
                        name = otherCourses.get(position - favouriteCourses.size()).getName();
                    }
                    intent.putExtra("courseId", id);
                    intent.putExtra("courseName", name);
                    itemView.getContext().startActivity(intent);
                }
            });
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference();
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    @NonNull final String courseID, courseName;
                    if (position < favouriteCourses.size()) {
                        courseID = favouriteCourses.get(position).getId();
                        courseName = favouriteCourses.get(position).getName();
                    } else {
                        courseID = otherCourses.get(position - favouriteCourses.size()).getId();
                        courseName = otherCourses.get(position - favouriteCourses.size()).getName();
                    }
                    /*
                     * The user has clicked on followed course, we need to update the course to unfollowed course.
                     * Listener for updating the course (un)follow state.
                     */
                    myRef.child("Users").child(Profile.getCurrentProfile().getId()).child("FavouriteCourses").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                if ((d.getValue()).equals(courseID)) {
                                    d.getRef().removeValue();
                                    favouriteCourses.remove(position);
                                    Course c = favouritesMap.remove(courseID);
                                    c.setFav(false);
                                    othersMap.put(courseID, c);
                                    otherCourses = new ArrayList<>(othersMap.values());
                                    CheckBox cc = itemView.findViewById(R.id.favouriteButton);
                                    cc.setChecked(false);
                                    notifyDataSetChanged();
                                    if (oA != null) {
                                        updateBaseRecyclerOnUnfollowingCourse();
                                    }
                                    return;
                                }
                            }
                            /*
                             * The user has clicked on unfollowed course, we need to update the course to followed course.
                             * Listener for updating the course (un)follow state.
                             */
                            myRef.child("Users").child(Profile.getCurrentProfile().getId()).
                                    child("FavouriteCourses").child(courseName).setValue(courseID);
                            Course c = othersMap.remove(courseID);
                            otherCourses.remove(c);
                            c.setFav(true);
                            favouritesMap.put(courseID, c);
                            favouriteCourses = new ArrayList<>(favouritesMap.values());
                            CheckBox cc = itemView.findViewById(R.id.favouriteButton);
                            cc.setChecked(true);
                            notifyDataSetChanged();
                            if (oA != null) {
                                updateBaseRecyclerOnFollowingCourse();
                            }
                        }

                        /*
                         * Once the user searches for a course, and updates its unfollow state,
                         * the course needs to be updated also in the main adapter.
                         */
                        private void updateBaseRecyclerOnUnfollowingCourse() {
                            for (Course unfavCourse : otherCourses) {
                                if (oA.favouritesMap.containsKey(unfavCourse.getId())) {
                                    oA.favouritesMap.remove(unfavCourse.getId());
                                    oA.othersMap.put(unfavCourse.getId(), unfavCourse);
                                }
                            }
                            oA.otherCourses = new ArrayList<>(oA.othersMap.values());
                            oA.favouriteCourses = new ArrayList<>(oA.favouritesMap.values());
                            oA.notifyDataSetChanged();
                        }

                        /*
                         * Once the user searches for a course, and updates its follow state,
                         * the course needs to be updated also in the main adapter.
                         */
                        private void updateBaseRecyclerOnFollowingCourse() {
                            for (Course favCourse : favouriteCourses) {
                                if (!oA.favouritesMap.containsKey(favCourse.getId())) {
                                    oA.favouritesMap.put(favCourse.getId(), favCourse);
                                    oA.othersMap.remove(favCourse.getId());
                                }
                            }
                            oA.otherCourses = new ArrayList<>(oA.othersMap.values());
                            oA.favouriteCourses = new ArrayList<>(oA.favouritesMap.values());
                            oA.notifyDataSetChanged();
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