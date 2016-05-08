package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.bignerdranch.android.done.AppData.User;


/**
 * Created by michalisgratsias on 08/05/16.
 */
public class SettingsActivity extends ActivityParent {

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(User.get().getUserName() + " - Profile Settings");
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(User.get().getUserName() + " - Profile Settings");
    }
}

