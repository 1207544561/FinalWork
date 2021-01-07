package com.example.chat.person.addFriend;

import com.example.chat.login.UserModel;

public class UserListModel extends UserModel {
    private boolean IsFriend;
    private String UserGroup;
    public UserListModel(String UserName, String Password, String Name, String Email, String ImgPath, boolean IsFriend, String UserGroup) {
        super(UserName, Password, Name, Email, ImgPath);
        this.IsFriend = IsFriend;
        this.UserGroup = UserGroup;
    }

    public boolean isFriend() {
        return IsFriend;
    }

    public String getUserGroup() {
        return UserGroup;
    }
}
