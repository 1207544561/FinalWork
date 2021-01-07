package com.example.chat.person.ui.gallery;

import com.example.chat.person.ui.home.FriendListUser;

public class UserAndMessageModel extends FriendListUser {
    private String Message;
    public UserAndMessageModel(String UserName, String Password, String Name, String Email, String ImgPath, String friendgroup, String Message) {
        super(UserName, Password, Name, Email, ImgPath, friendgroup);
        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }
}
