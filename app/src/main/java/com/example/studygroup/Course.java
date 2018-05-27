package com.example.studygroup;


import android.support.annotation.NonNull;

import java.util.Comparator;

public class Course implements Comparable<Course> {
    private String faculty;
    private String id;
    private String name;
    private boolean isFav;
    int index;

    Course(String faculty, String id, String name,boolean fav,int idx) {
        this.setFaculty(faculty);
        this.setId(id);
        this.setName(name);
        this.isFav = fav;
        index=idx;
    }


    public String getFaculty() {
        return faculty;
    }

    private void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    @Override
    public int compareTo(@NonNull Course o) {
        if(this.isFav == o.isFav){
            return this.id.compareTo(o.id);
        }else{
            if(isFav == true){
                return -1;
            }else{
                return 1;
            }
        }
    }
}
