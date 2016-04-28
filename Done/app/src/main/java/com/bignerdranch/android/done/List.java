package com.bignerdranch.android.done;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by michalisgratsias on 27/03/16.
 */
public class List {

    private String mListId;
    private String mCreatorId;
    private String mListName;
    private ArrayList<String> mListUsers;
    private ArrayList<Task> mListTasks;

    public List(String userId) {
        mCreatorId = userId;
        mListUsers = new ArrayList<String>();                                 // the Users of the list
        mListTasks = new ArrayList<Task>();
    }

    public List() {
    }

    public ArrayList<Task> getListTasks() {                                 // get all List Tasks
        return mListTasks;
    }

    public Task getTask(String id) {                                          // get a List Task by ID
        for (Task t : mListTasks) {
            if (t.getTaskId().equals(id))
                return t;
        }
        return null;
    }

    public String getListId() {
        return mListId;
    }

    public void setListId(String listId){mListId = listId;}

    public String getCreatorId() {
        return mCreatorId;
    }

    public void setCreatorId(String creatorId) {
        mCreatorId = creatorId;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String listName) {
        mListName = listName;
    }

    public ArrayList<String> getListUsers() {
        return mListUsers;
    }

    public void addListUser(String id) {
        mListUsers.add(id);
    }

    public void removeListUser(String id) {
        mListUsers.remove(id);
    }

    public void addListTask(Task task) {
        mListTasks.add(task);
    }

    public void removeListTask(Task task) {
        mListTasks.remove(task);
    }
}
