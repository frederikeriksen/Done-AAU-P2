package com.bignerdranch.android.done.UserData;

import java.util.ArrayList;

/**
 * Created by michalisgratsias on 27/03/16.
 */
public class User {

    private String mUserId;
    private String mUserName;
    private String mPassword;
    private String mEmail;
    private ArrayList<List> mUserLists;
    private static User sUser;

    public User() {
        mUserLists = new ArrayList<List>();
    }

    public static User get() {             // creates list as a Singleton= only 1 User object possible
        if (sUser == null) {
            sUser = new User();
        }
        return sUser;
    }

    public ArrayList<List> getUserLists() {                         // get all User Lists
        return mUserLists;
    }        // get all User-Lists

    public List getList(String id) {                                    // get a User-List by ID
        for (List l : mUserLists) {
            if (l.getListId().equals(id))
                return l;
        }
        return null;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void addUserList(List list) {
        sUser.mUserLists.add(list);
    }

    public void removeUserList(List list) {
        sUser.mUserLists.remove(list);
    }
}
