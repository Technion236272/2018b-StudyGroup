package com.example.studygroup;


public class Group {
 //   protected String faculty;
    private String id;
    private String subject;
    private String date;
    private String location;
    private int maxNumOfPart;
    private int currentNumOfPart;
    protected String adminID;
//    protected ArrayList<User> users;

    Group() {}

    Group(String id, String subject, String date, String location, int maxNumOfPart,
          int currentNumOfPart, String adminID) {
   //     this.faculty = faculty;
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.setLocation(location);
        this.maxNumOfPart = maxNumOfPart;
        this.currentNumOfPart = currentNumOfPart;
        this.adminID = adminID;
//        this.users = users;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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


}
