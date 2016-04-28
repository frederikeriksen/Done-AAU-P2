package com.bignerdranch.android.done;


/**
 * Created by michalisgratsias on 25/04/16.
 */
public class DataBaseLists {

    private String mListId;
    private String CreatorId;
    private String mListName;

    DataBaseLists () {
    }

    public String getListId() {
        return mListId;
    }

    public void setListId(String listId) {
        mListId = listId;
    }

    public String getCreatorId() {
        return CreatorId;
    }

    public void setCreatorId(String creatorId) {
        CreatorId = creatorId;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String listName) {
        mListName = listName;
    }
}
