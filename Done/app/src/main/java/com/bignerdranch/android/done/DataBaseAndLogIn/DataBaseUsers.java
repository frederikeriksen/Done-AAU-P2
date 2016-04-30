package com.bignerdranch.android.done.DataBaseAndLogIn;

/**
 * Created by Ico on 19-Apr-16.
 */
public class DataBaseUsers {

    //IMPORTANT!!! the name of the fields should be the same as the one in the database!
    private String userId;
    private String userName;
    private String password;
    private String email;

    public DataBaseUsers(){
        // empty default constructor, necessary for Firebase
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

