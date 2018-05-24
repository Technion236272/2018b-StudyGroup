package com.example.studygroup;


import java.util.ArrayList;

public class Group {
 //   protected String faculty;
    protected String id;
    protected String subject;
    protected String date;
    protected int maxNumOfPart;
    protected int currentNumOfPart;
//    protected String adminToken;
//    protected ArrayList<User> users;
 //   protected int courseId;

    public Group(String id, String subject, String date, int maxNumOfPart,
                 int currentNumOfPart) {
   //     this.faculty = faculty;
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.maxNumOfPart = maxNumOfPart;
        this.currentNumOfPart = currentNumOfPart;
//        this.adminToken = adminToken;
//        this.users = users;
 //       this.courseId = courseId;
    }


//    public String getFaculty() {
//        return faculty;
//    }

//    public void setFaculty(String faculty) {
//        this.faculty = faculty;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getmaxNumOfPart() {
        return maxNumOfPart;
    }

    public void setmaxNumOfPart(int maxNumOfPart) {
        this.maxNumOfPart = maxNumOfPart;
    }

    public int getCurrentNumOfPart() {
        return currentNumOfPart;
    }

    public void setCurrentNumOfPart(int currentNumOfPart) {
        this.currentNumOfPart = currentNumOfPart;
    }

//    public String getAdminToken() {
//        return adminToken;
//    }
//
//    public void setAdminToken(String adminToken) {
//        this.adminToken = adminToken;
//    }
//
//    public ArrayList<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(ArrayList<User> users) {
//        this.users = users;
//    }
//
//    public int getCourseId() {
//        return courseId;
//    }
//
//    public void setCourseId(int courseId) {
//        this.courseId = courseId;
//    }

}
