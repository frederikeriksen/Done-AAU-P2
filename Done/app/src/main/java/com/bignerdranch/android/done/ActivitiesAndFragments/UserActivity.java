package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                     // from support library
import android.util.Log;

import com.bignerdranch.android.done.DataBaseAndLogIn.FireBaseDataRetrieve;
import com.bignerdranch.android.done.AppData.User;

/**
 * Created by michalisgratsias on 03/04/16.
 */
public class UserActivity extends ActivityParent {

    private static final String TAG = "DoneActivity";

    @Override
    protected Fragment createFragment() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent databaseService = new Intent(this, FireBaseDataRetrieve.class); //HERE WE START THE SERVICE WHICH PULLS DATA!!!
        startService(databaseService);
        super.onCreate(savedInstanceState);
        Log.d(TAG, " Activity Created ");
        getSupportActionBar().setTitle(User.get().getUserName() + " - My " + User.get().getUserLists().size() + " To-Do Lists");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " Activity Resumed ");
        getSupportActionBar().setTitle(User.get().getUserName() + " - My " + User.get().getUserLists().size() + " To-Do Lists");
    }
}


