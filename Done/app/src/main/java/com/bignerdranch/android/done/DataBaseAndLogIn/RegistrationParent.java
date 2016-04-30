package com.bignerdranch.android.done.DataBaseAndLogIn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bignerdranch.android.done.R;

/**
 * Created by michalisgratsias on 30/04/16.
 */
public class RegistrationParent extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);             // view inflated from xml layout

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // attaching layout to the Toolbar object
        setSupportActionBar(toolbar);
    }

}
