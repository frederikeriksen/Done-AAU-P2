package com.bignerdranch.android.done;

import com.firebase.client.Firebase;

/**
 * Created by Ico on 19-Apr-16.
 */
public class Done extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }
}
