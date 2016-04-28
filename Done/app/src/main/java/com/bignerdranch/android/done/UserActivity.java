package com.bignerdranch.android.done;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                     // from support library


/**
 * Created by michalisgratsias on 03/04/16.
 */
public class UserActivity extends ActivityParent {

    @Override
    protected Fragment createFragment() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent databaseService = new Intent(this, FireBaseDataRetrieve.class); //HERE WE START THE SERVICE WHICH PULLS DATA!!!
        startService(databaseService);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My To-Do Lists");// (" + User.get().getUserLists().size() + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("My To-Do Lists");// (" + User.get().getUserLists().size() + ")");
    }
}


