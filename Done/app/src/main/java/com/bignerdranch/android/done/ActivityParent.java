package com.bignerdranch.android.done;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;                         // from support library
import android.support.v4.app.FragmentActivity;                 // from support library
import android.support.v4.app.FragmentManager;                  // from support library
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by michalisgratsias on 03/04/16.
 */
public abstract class ActivityParent extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected abstract Fragment createFragment();               // abstract method not implemented here

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);             // view inflated from xml layout

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // attaching layout to the Toolbar object
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();       // FM responsible for managing Fragments and adding their Views
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);   // using Support Library - give frmt to mgr.
        if (fragment == null) {                                 // Because maybe this ID is saved on rotation by fr.mgr, and is not null
            fragment = createFragment();                        // abstract method called
            fm.beginTransaction()                               // create a new Fragment Object hosted by the Activity
                    .add(R.id.fragment_container, fragment)     // include an ADD operation on it (identified by resource ID of container view)
                    .commit(); }                                // and commit the fragment transaction to the list of the mgr
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the main; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_to_do_lists) {
            // Get back to the original To-Do Lists page
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_incomplete_tasks) {
            // Show all incomplete tasks sorted by list name
        } else if (id == R.id.nav_unverified_tasks) {
            // Show any tasks the user assigned but not yet verified as done
        } else if (id == R.id.nav_completed_tasks) {
            // Show all completed tasks
        } else if (id == R.id.nav_profile) {
            // Handle changes on the profile
        } else if (id == R.id.nav_notifications) {
            // Handle notifications
        } else if (id == R.id.nav_sign_out) {
            // Sign out
            Intent intent = new Intent(getApplicationContext(), StartingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
