package com.bignerdranch.android.done;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                 // from support library
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;


import java.util.UUID;

public class TaskActivity extends ActivityParent {

    private static final String EXTRA_TASK_ID = "com.bignerdranch.android.done.task_id";
    private static final String EXTRA_LIST_ID = "com.bignerdranch.android.done.list_id";

    public static Intent newIntent(Context packageContext, String taskID, String listID) {  // PASSES the taskId and listID as an Intent Extra
        Intent intent = new Intent(packageContext, TaskActivity.class);                 // for the TaskFragment
        intent.putExtra(EXTRA_TASK_ID, taskID);
        intent.putExtra(EXTRA_LIST_ID, listID);
        return intent;
    }

    @Override
    protected Fragment createFragment() {               // overrides the abstract method for hosting a specific fragment
        String taskId = (String) getIntent().getSerializableExtra(EXTRA_TASK_ID);   //  RETRIEVES Task ID from Intent
        String listId = (String) getIntent().getSerializableExtra(EXTRA_LIST_ID);   //  RETRIEVES List ID from Intent
        return TaskFragment.newInstance(taskId, listId);        // calls the newInstance method to create TaskFragment and pass the ID
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String taskId = (String) getIntent().getSerializableExtra(EXTRA_TASK_ID);   //  RETRIEVES Task ID from Intent
        String listId = (String) getIntent().getSerializableExtra(EXTRA_LIST_ID);   //  RETRIEVES List ID from Intent
        getSupportActionBar().setTitle(User.get().getList(listId).getTask(taskId).getTaskName());
    }
}
