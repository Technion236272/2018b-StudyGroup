package com.example.studygroup;


public class Course {
    protected String faculty;
    protected String id;
    protected String name;

    public Course(String faculty, String id, String name) {
        this.setFaculty(faculty);
        this.setId(id);
        this.setName(name);
    }


    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
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
}
