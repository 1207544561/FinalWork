package com.example.chat.login;

public class UserModel {
    //private int id;
    private String UserName, Password, Name, Email, ImgPath;
    public UserModel(String UserName, String Password, String Name, String Email, String ImgPath) {
        this.UserName = UserName;
        this.Password = Password;
        this.Name = Name;
        this.Email = Email;
        this.ImgPath = ImgPath;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getImgPath() {
        return ImgPath;
    }

}
