package com.bignerdranch.android.done.DataBaseAndLogIn;


/**
 * Created by michalisgratsias on 25/04/16.
 */
public class DataBaseLists {

    private String listId;
    private String creatorId;
    private String listName;

    public DataBaseLists () {
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
