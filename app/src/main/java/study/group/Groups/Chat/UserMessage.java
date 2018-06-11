package study.group.Groups.Chat;


import android.widget.ImageView;

import study.group.Utilities.User;

class UserMessage {
    private String message;
    private User sender;
    private long createdAt;
    private String type;
    private String adminID;
    private String groupID;
    //private ImageView profilePicture;


    public UserMessage(String chatMessage, User currentUser, long time, String type, String adminID, String groupID) {
        this.message = chatMessage;
        this.sender = currentUser;
        this.createdAt = time;
        this.type = type;
        this.adminID = adminID;
        this.groupID = groupID;
    }

    public String getMessage() {
        return message;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public User getSender() {
        return sender;
    }

    public String getType() {return type;}

    public String getAdminID() {return adminID;}

    public String getGroupID() {return groupID;}

    public long getTime() {return createdAt;}

    @Override
    public boolean equals(Object m)
    {
        return ((UserMessage)m).getSender().getToken().equals(sender.getToken()) &&
                                                (((UserMessage)m).getTime() == getTime());
    }
}
