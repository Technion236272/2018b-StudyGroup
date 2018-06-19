package study.group.Utilities;


import android.net.Uri;

import java.util.HashMap;

public class Group {
    //   protected String faculty;
    private String name;
    private String id;
    private String subject;
    private String date;
    private String time;
    private String location;
    private int maxNumOfPart;
    private int currentNumOfPart;
    private String adminID;
    private String groupID;
    private String image;
    private HashMap<String,String> Requests;
    private HashMap<String,String> participants;
    private HashMap<String,String> interested;
//    protected ArrayList<User> users;

    Group() {}

    public Group(String groupID, String id, String subject, String date, String location, int maxNumOfPart,
                 int currentNumOfPart, String adminID, HashMap<String,String> Requests, HashMap<String,String> participants, HashMap<String,String> interested) {
        //     this.faculty = faculty;
        this.name = id + " - " + subject;
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.setLocation(location);
        this.maxNumOfPart = maxNumOfPart;
        this.currentNumOfPart = currentNumOfPart;
        this.setAdminID(adminID);
        this.groupID = groupID;
        this.participants = Requests;
        this.Requests = participants;
        this.interested = interested;
//        this.users = users;
    }

    public Group(String groupID, String id, String subject, String date, String location, int maxNumOfPart,
                 int currentNumOfPart, String adminID, String time, String image) {
        //     this.faculty = faculty;
        this.name = id + "-" + subject;
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.setLocation(location);
        this.maxNumOfPart = maxNumOfPart;
        this.currentNumOfPart = currentNumOfPart;
        this.adminID = adminID;
        this.groupID = groupID;
        this.time = time;
        this.image = image;
        this.participants = new HashMap<>();
        this.Requests = new HashMap<>();
        this.interested = new HashMap<>();
//        this.users = users;
    }

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

    public HashMap<String, String> getInterested() {
        return interested;
    }

    public void setInterested(HashMap<String, String> interested) {
        this.interested = interested;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public HashMap<String,String> getRequests() {
        return Requests;
    }

    public void setRequests(HashMap<String,String> requests) {
        Requests = requests;
    }

    public HashMap<String,String> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String,String> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        return other instanceof Group && this.getGroupID().equals(((Group)other).getGroupID());
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
