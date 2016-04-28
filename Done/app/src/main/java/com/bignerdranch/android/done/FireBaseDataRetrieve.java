package com.bignerdranch.android.done;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class FireBaseDataRetrieve extends Service {

    private static final String TAG = "DoneActivity";
    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
    User curUser;

    public FireBaseDataRetrieve() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        curUser = User.get();
        Log.d(TAG, "User Name: " + User.get().getUserName());                // LOGS THE NAME OF THE USER

        Firebase mRefLists = new Firebase("https://doneaau.firebaseio.com/lists/");
        mRefLists.addChildEventListener(new ChildEventListener() {
            // Retrieve new lists as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                DataBaseLists list = snapshot.getValue(DataBaseLists.class);

                if (curUser.getUserId().equals(list.getCreatorId())) {      // THE DATABASE LIST IS CREATED BY THIS USER

                    Log.d(TAG, "List Name: " + list.getListName());          // LOGS THE NAME OF THE LIST

                    boolean listAlreadyAdded = curUser.getList(list.getListId()) != null;

                    Log.d(TAG, "List already added: " + listAlreadyAdded);  // LOGS IF THE LIST IS ALREADY ADDED TO LISTS-ARRAY

                    if (!listAlreadyAdded) {                                // LIST IS NOT YET ADDED TO USER LISTS-ARRAY
                        List currList = new List(list.getCreatorId());
                        currList.setListId(list.getListId());
                        currList.setListName(list.getListName());
                        currList.setCreatorId(list.getCreatorId());
                        curUser.addUserList(currList);                      // ADDS DATABASE LIST TO USER LISTS-ARRAY
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Firebase mRefTasks = new Firebase("https://doneaau.firebaseio.com/tasks/");
        mRefTasks.addChildEventListener(new ChildEventListener() {
            // Retrieve new tasks as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                DataBaseTasks task = snapshot.getValue(DataBaseTasks.class);

                List arrayListForTask = curUser.getList(task.getListId());

                if (arrayListForTask != null) {                             // THERE IS A USER-LIST ADDED, WHERE THE TASK MUST GO

                    Log.d(TAG, "Task Name: " + task.getTaskName());         // LOGS THE NAME OF THE TASK

                    boolean taskAlreadyAdded = arrayListForTask.getTask(task.getTaskId()) != null;

                    Log.d(TAG, "Task already added: " + taskAlreadyAdded);  // LOGS IF THE TASK IS ALREADY ADDED TO TASKS-ARRAY

                    if (!taskAlreadyAdded) {                                // TASK IS NOT YET ADDED TO USER-LIST TASKS-ARRAY
                        Task userTask = new Task(task.getListId());
                        userTask.setTaskId(task.getTaskId());
                        userTask.setListId(task.getListId());
                        userTask.setTaskName(task.getTaskName());
                        try {
                            userTask.setCreatedDate(format.parse(task.getCreatedDate()));
                        } catch (ParseException e) {
                        }
                        userTask.setCompleted(task.isCompleted());
                        userTask.setVerified(task.isVerified());

                        arrayListForTask.addListTask(userTask);             // ADDS DATABASE TASK TO USER-LIST TASKS-ARRAY
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
