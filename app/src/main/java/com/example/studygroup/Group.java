package com.example.studygroup;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class Group {
 //   protected String faculty;
    private String name;
    private String id;
    private String subject;
    private String date;
    private String location;
    private int maxNumOfPart;
    private int currentNumOfPart;
    private String adminID;
    private String groupID;
    private ArrayList<String> Requests, participants;
//    protected ArrayList<User> users;

    Group() {}

    Group(String groupID, String id, String subject, String date, String location, int maxNumOfPart,
          int currentNumOfPart, String adminID) {
   //     this.faculty = faculty;
        this.name = id + "-" + subject;
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.setLocation(location);
        this.maxNumOfPart = maxNumOfPart;
        this.currentNumOfPart = currentNumOfPart;
        this.setAdminID(adminID);
        this.groupID = groupID;
        this.participants = new ArrayList<>();
        this.Requests = new ArrayList<>();
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

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getName() {
        return name;
    }

    public void setGroupID(String id){
        groupID = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGroupID() {
        return groupID;
    }

//
//    public ArrayList<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(ArrayList<User> users) {
//        this.users = users;
//    }


    public ArrayList<String> getRequests() {
        return Requests;
    }

    public void setRequests(ArrayList<String> requests) {
        Requests = requests;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        return other instanceof Group && this.getGroupID().equals(((Group)other).getGroupID());
    }
}
