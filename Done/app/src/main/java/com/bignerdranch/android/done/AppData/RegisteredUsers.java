package com.bignerdranch.android.done.AppData;

import com.bignerdranch.android.done.DataBaseAndLogIn.DataBaseUsers;

import java.util.ArrayList;

/**
 * Created by michalisgratsias on 05/05/16.
 */
public class RegisteredUsers {

    private String userId;
    private String userName;
    private String password;
    private String email;
    private String photo;
    private ArrayList<DataBaseUsers> mRUsers;
    private static RegisteredUsers sRUsers;

    public RegisteredUsers() {
        mRUsers = new ArrayList<DataBaseUsers>();
    }

    public static RegisteredUsers get() {             // creates list as a Singleton= only 1 User object possible
        if (sRUsers == null) {
            sRUsers = new RegisteredUsers();
        }
        return sRUsers;
    }

    public ArrayList<DataBaseUsers> getUsers() {                         // get all User Lists
        return mRUsers;
    }        // get all User-Lists

    public DataBaseUsers getUser(String id) {                                    // get a User-List by ID
        for (DataBaseUsers u : mRUsers) {
            if (u.getUserId().equals(id))
                return u;
        }
        return null;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String email) {
        this.photo = photo;
    }

}
