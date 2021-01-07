package com.example.chat.message;

import com.example.chat.R;

public class MessageModel {
    String User, MessageText, UserImgPath;
    public MessageModel(String User, String MessageText, String UserImgPath) {
        this.User = User;
        this.MessageText = MessageText;
        this.UserImgPath = UserImgPath;
    }

    public String getMessageText() {
        return MessageText;
    }

    public String getUser() {
        return User;
    }

    public String getUserImgPath() {
        return UserImgPath;
    }
}
