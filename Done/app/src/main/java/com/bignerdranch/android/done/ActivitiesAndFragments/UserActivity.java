package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                     // from support library
import com.bignerdranch.android.done.DataBaseAndLogIn.FireBaseDataRetrieve;
import com.bignerdranch.android.done.UserData.User;

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
        getSupportActionBar().setTitle(User.get().getUserName() + " - My To-Do Lists");// + "(" + User.get().getUserLists().size() + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(User.get().getUserName() + " - My To-Do Lists");//+ "(" + User.get().getUserLists().size() + ")");
    }
}


