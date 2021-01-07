package com.example.chat.person.ui.home;

import com.example.chat.login.UserModel;

public class FriendListUser extends UserModel {
    private String friendgroup;

    public FriendListUser(String UserName, String Password, String Name, String Email, String ImgPath, String friendgroup) {
        super(UserName, Password, Name, Email, ImgPath);
        this.friendgroup = friendgroup;
    }

    public String getFriendgroup() {
        return friendgroup;
    }
}
