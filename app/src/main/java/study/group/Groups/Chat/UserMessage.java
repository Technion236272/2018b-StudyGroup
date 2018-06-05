package study.group.Groups.Chat;


import android.widget.ImageView;

import study.group.Utilities.User;

class UserMessage {
    private String message;
    private User sender;
    private long createdAt;
    //private ImageView profilePicture;

    public UserMessage(String chatMessage, User currentUser, long time) {
        this.message = chatMessage;
        this.sender = currentUser;
        this.createdAt = time;
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
}
