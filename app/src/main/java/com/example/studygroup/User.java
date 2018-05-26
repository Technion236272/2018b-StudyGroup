package com.example.studygroup;

import java.util.ArrayList;

class User {

    private String token;
    private String name;
    private ArrayList<Group> joined;
    private ArrayList<Group> interested;
    private ArrayList<Group> requests;
    private ArrayList<Group> groupAdmin;
    private ArrayList<Group> favoriteCourses;
    // more

    User(String token, String name) {
        this.setToken(token);
        this.setName(name);
        this.setJoined(new ArrayList<Group>());
        this.setInterested(new ArrayList<Group>());
        this.setRequests(new ArrayList<Group>());
        this.setGroupAdmin(new ArrayList<Group>());
        this.setFavoriteCourses(new ArrayList<Group>());
    }

    public User(String token, String name, ArrayList<Group> joined, ArrayList<Group> interested,
                ArrayList<Group> requests, ArrayList<Group> groupAdmin, ArrayList<Group> favoriteCourses) {
        this.setToken(token);
        this.setName(name);
        this.setJoined(joined);
        this.setInterested(interested);
        this.setRequests(requests);
        this.setGroupAdmin(groupAdmin);
        this.setFavoriteCourses(favoriteCourses);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Group> getJoined() {
        return joined;
    }

    public void setJoined(ArrayList<Group> joined) {
        this.joined = joined;
    }

    public ArrayList<Group> getInterested() {
        return interested;
    }

    public void setInterested(ArrayList<Group> interested) {
        this.interested = interested;
    }

    public ArrayList<Group> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Group> requests) {
        this.requests = requests;
    }

    public ArrayList<Group> getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(ArrayList<Group> groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public ArrayList<Group> getFavoriteCourses() {
        return favoriteCourses;
    }

    public void setFavoriteCourses(ArrayList<Group> favoriteCourses) {
        this.favoriteCourses = favoriteCourses;
    }
}
