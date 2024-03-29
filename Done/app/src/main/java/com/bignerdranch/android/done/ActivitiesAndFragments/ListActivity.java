package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                 // from support library
import com.bignerdranch.android.done.AppData.User;


/**
 * Created by michalisgratsias on 03/04/16.
 */
public class ListActivity extends ActivityParent {

    private static final String EXTRA_LIST_ID = "com.bignerdranch.android.done.list_id";

    public static Intent newIntent(Context packageContext, String listID) {     // PASSES the listId as an Intent Extra
        Intent intent = new Intent(packageContext, ListActivity.class);         // for the ListFragment
        intent.putExtra(EXTRA_LIST_ID, listID);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        String listId = (String) getIntent().getSerializableExtra(EXTRA_LIST_ID);
        return ListTaskFragment.newInstance(listId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String listId = (String) getIntent().getSerializableExtra(EXTRA_LIST_ID);
        getSupportActionBar().setTitle("List: " + User.get().getList(listId).getListName());
    }

    @Override
    public void onResume() {
        super.onResume();
        String listId = (String) getIntent().getSerializableExtra(EXTRA_LIST_ID);
        getSupportActionBar().setTitle("List: " + User.get().getList(listId).getListName());
    }
}
