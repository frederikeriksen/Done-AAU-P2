package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                     // from support library
import android.util.Log;
import android.widget.TextView;

import com.bignerdranch.android.done.DataBaseAndLogIn.FireBaseDataRetrieve;
import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.R;

import org.w3c.dom.Text;

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
        getSupportActionBar().setTitle(User.get().getUserName() + " - My " + User.get().getUserLists().size() + " To-Do Lists");

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(User.get().getUserName() + " - My " + User.get().getUserLists().size() + " To-Do Lists");
    }
}


