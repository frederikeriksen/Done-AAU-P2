package com.bignerdranch.android.done.DataBaseAndLogIn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.bignerdranch.android.done.AppData.List;
import com.bignerdranch.android.done.AppData.RegisteredUsers;
import com.bignerdranch.android.done.AppData.Task;
import com.bignerdranch.android.done.AppData.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by Ico on 19-Apr-16.
 */
public class FireBaseDataRetrieve extends Service {

    private static final String TAG = "DoneActivity";
    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
    SimpleDateFormat format2 = new SimpleDateFormat("EEEE MMM dd, yyyy");
    ArrayList<String> users = new ArrayList<String>();
    User curUser;
    boolean userAlreadyAdded;
    boolean listAlreadyAdded;
    boolean listNameAlreadyUpdated;
    boolean listNotAlreadyDeleted;
    boolean taskAlreadyAdded;
    boolean taskNameAlreadyUpdated;
    boolean dueDateAlreadyUpdated;
    boolean reminderDateAlreadyUpdated;
    boolean completionAlreadyUpdated;
    boolean verificationAlreadyUpdated;
    boolean taskNotAlreadyDeleted;
    boolean userNameAlreadyUpdated;
    boolean passwordAlreadyUpdated;
    boolean emailAlreadyUpdated;
    boolean userNotAlreadyDeleted;

    public FireBaseDataRetrieve() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        curUser = User.get();
        Log.d(TAG, "User Name: " + User.get().getUserName());                // LOGS THE NAME OF THE USER

        //           R E T R I E V E S     U S E R S
        Firebase mRefUsers = new Firebase("https://doneaau.firebaseio.com/users/");

        mRefUsers.addChildEventListener(new ChildEventListener() {          // Retrieves user registration data as they appear in the database

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) { // Retrieves new/old registered users for the user

                DataBaseUsers user = snapshot.getValue(DataBaseUsers.class);

                Log.d(TAG, "User Name: " + user.getUserName());          // LOGS THE NAME OF THE USER

                userAlreadyAdded = RegisteredUsers.get().getUser(user.getUserId()) != null;

                Log.d(TAG, "User already added: " + userAlreadyAdded);  // LOGS IF THE USER IS ALREADY ADDED TO REG.USERS-ARRAY

                if (!userAlreadyAdded) {                                // USER IS NOT YET ADDED TO REG.USERS-ARRAY
                    DataBaseUsers currUser = new DataBaseUsers();
                    currUser.setUserId(user.getUserId());
                    currUser.setUserName(user.getUserName());
                    currUser.setPassword(user.getPassword());
                    currUser.setEmail(user.getEmail());
                    RegisteredUsers.get().getUsers().add(currUser);     // ADDS DATABASE USER TO REG.USERS-ARRAY
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { // Retrieves updated data for the user

                String userName = (String) dataSnapshot.child("userName").getValue();
                String password = (String) dataSnapshot.child("password").getValue();
                String email = (String) dataSnapshot.child("email").getValue();

                Log.d(TAG, "Updated User Name: " + userName);
                Log.d(TAG, "Updated Password: " + password);
                Log.d(TAG, "Updated Email: " + email);

                userNameAlreadyUpdated = (curUser.getUserName()).equals(userName);
                passwordAlreadyUpdated = (curUser.getPassword()).equals(password);
                emailAlreadyUpdated = (curUser.getUserName()).equals(email);

                Log.d(TAG, "User Name already updated: " + userNameAlreadyUpdated);
                Log.d(TAG, "Password already updated: " + passwordAlreadyUpdated);
                Log.d(TAG, "Email already updated: " + emailAlreadyUpdated);

                if (!userNameAlreadyUpdated) {                                      // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    curUser.setUserName(userName);                                  // ADDS DATABASE USER DATA TO USER ARRAY
                }
                if (!passwordAlreadyUpdated) {                                      // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    curUser.setPassword(password);                                  // ADDS DATABASE USER DATA TO USER ARRAY
                }
                if (!emailAlreadyUpdated) {                                      // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    curUser.setEmail(email);                                  // ADDS DATABASE USER DATA TO USER ARRAY
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {         // Retrieves deleted users

                String userId = (String) dataSnapshot.child("userId").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();

                Log.d(TAG, "Deleted User Name: " + userName);                  // LOGS THE NAME OF THE DELETED USER

                userNotAlreadyDeleted = RegisteredUsers.get().getUser(userId) != null;

                Log.d(TAG, "User is already deleted: " + !userNotAlreadyDeleted);  // LOGS IF THE USER IS ALREADY DELETED FROM REGISTERED-USERS

                if (userNotAlreadyDeleted) {                                // USER IS NOT YET DELETED IN REGISTERED USERS

                    RegisteredUsers.get().getUsers().remove(RegisteredUsers.get().getUser(userId)); // DELETES DATABASE USER FROM REGISTERED USERS
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //           R E T R I E V E S     L I S T S
        final Firebase mRefLists = new Firebase("https://doneaau.firebaseio.com/lists/");

        mRefLists.addChildEventListener(new ChildEventListener() {          // Retrieves user-list data as they appear in the database

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) { // Retrieves new/old lists for the user

                String listId = (String) snapshot.child("listId").getValue();
                String creatorId = (String) snapshot.child("creatorId").getValue();
                String listName = (String) snapshot.child("listName").getValue();
                users.clear();
                for (DataSnapshot user : snapshot.child("shared_users/").getChildren()) {
                    users.add(user.getKey());
                }

                if (curUser.getUserId().equals(creatorId) || users.contains(curUser.getUserId())) {      // THE DATABASE LIST IS CREATED BY THIS USER
                                                                                                        // OR IS SHARED WITH THIS USER
                    Log.d(TAG, "List Name: " + listName);          // LOGS THE NAME OF THE LIST

                    listAlreadyAdded = curUser.getList(listId) != null;

                    Log.d(TAG, "List already added: " + listAlreadyAdded);  // LOGS IF THE LIST IS ALREADY ADDED TO LISTS-ARRAY

                    if (!listAlreadyAdded) {                                // LIST IS NOT YET ADDED TO USER LISTS-ARRAY from CODE
                        final List currList = new List(creatorId);
                        currList.setListId(listId);
                        currList.setListName(listName);
                        currList.setCreatorId(creatorId);

                        for (DataSnapshot user : snapshot.child("shared_users/").getChildren()) {
                            currList.addListUser(user.getKey());
                            Log.d(TAG, "List Shared With: " + RegisteredUsers.get().getUser(user.getKey()).getUserName());
                        }
                        curUser.addUserList(currList);                      // ADDS DATABASE LIST TO USER LISTS-ARRAY
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { // Retrieves updated list data for the user

                String listId = (String) dataSnapshot.child("listId").getValue();
                String title = (String) dataSnapshot.child("listName").getValue();

                User.get().getList(listId).getListUsers().clear();
                for (DataSnapshot userSnapshot: dataSnapshot.child("shared_users/").getChildren()) {
                    User.get().getList(listId).getListUsers().add(userSnapshot.getKey());   // CHANGES SHARED USERS IN LIST
                }

                Log.d(TAG, "Updated List Name: " + title);                  // LOGS THE UPDATED NAME OF THE LIST

                listNameAlreadyUpdated = (curUser.getList(listId).getListName()).equals(title);

                Log.d(TAG, "List name already updated: " + listNameAlreadyUpdated);  // LOGS IF THE LIST NAME IS ALREADY UPDATED IN LISTS-ARRAY

                if (!listNameAlreadyUpdated) {                              // LIST NAME IS NOT YET UPDATED IN USER LISTS-ARRAY

                    curUser.getList(listId).setListName(title);             // CHANGES DATABASE LIST NAME IN USER LISTS-ARRAY LIST NAME
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {         // Retrieves deleted lists for the user

                String listId = (String) dataSnapshot.child("listId").getValue();
                String title = (String) dataSnapshot.child("listName").getValue();

                Log.d(TAG, "Deleted List Name: " + title);                  // LOGS THE NAME OF THE DELETED LIST

                listNotAlreadyDeleted = curUser.getList(listId) != null;

                Log.d(TAG, "List is already deleted: " + !listNotAlreadyDeleted);  // LOGS IF THE LIST IS ALREADY DELETED FROM LISTS-ARRAY

                if (listNotAlreadyDeleted) {                                // LIST NAME IS NOT YET UPDATED IN USER LISTS-ARRAY

                    curUser.removeUserList(curUser.getList(listId));        // DELETES DATABASE LIST FROM USER LISTS-ARRAY
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //           R E T R I E V E S     T A S K S
        Firebase mRefTasks = new Firebase("https://doneaau.firebaseio.com/tasks/");

        mRefTasks.addChildEventListener(new ChildEventListener() {          // Retrieves task data as they appear in the database

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                String listId = (String) snapshot.child("listId").getValue();
                String taskId = (String) snapshot.child("taskId").getValue();
                String taskName = (String) snapshot.child("taskName").getValue();
                String createdDate = (String) snapshot.child("createdDate").getValue();
                String dueDate = (String) snapshot.child("dueDate").getValue();
                String reminderDate = (String) snapshot.child("reminderDate").getValue();
                Boolean completed = (Boolean) snapshot.child("completed").getValue();
                Boolean verified = (Boolean) snapshot.child("verified").getValue();

                List arrayListForTask = curUser.getList(listId);

                if (arrayListForTask != null) {                             // THERE IS A USER-LIST ADDED, WHERE THE TASK MUST GO

                    Log.d(TAG, "Task Name: " + taskName);                   // LOGS THE NAME OF THE TASK

                    taskAlreadyAdded = arrayListForTask.getTask(taskId) != null;

                    Log.d(TAG, "Task already added: " + taskAlreadyAdded);  // LOGS IF THE TASK IS ALREADY ADDED TO TASKS-ARRAY

                    if (!taskAlreadyAdded) {                                // TASK IS NOT YET ADDED TO USER-LIST TASKS-ARRAY

                        Task userTask = new Task(listId);
                        userTask.setTaskId(taskId);
                        userTask.setListId(listId);
                        userTask.setTaskName(taskName);
                        try {
                            userTask.setCreatedDate(format.parse(createdDate));
                        } catch (ParseException e) {
                        }
                        if (dueDate != null) {
                            try {
                                userTask.setDueDate(format2.parse(dueDate));
                            } catch (ParseException e) {
                            }
                        }
                        if (reminderDate != null) {
                            try {
                                userTask.setReminderDate(format2.parse(reminderDate));
                            } catch (ParseException e) {
                            }
                        }
                        for (DataSnapshot user : snapshot.child("users_assigned_to/").getChildren()) {
                            userTask.addAssignee(user.getKey());
                            Log.d(TAG, "Task Assigned To: " + RegisteredUsers.get().getUser(user.getKey()).getUserName());
                        }
                        for (DataSnapshot user : snapshot.child("users_hidden_from/").getChildren()) {
                            userTask.addNonViewer(user.getKey());
                            Log.d(TAG, "Task Hidden From: " + RegisteredUsers.get().getUser(user.getKey()).getUserName());
                        }
                        for (DataSnapshot noteSnapshot : snapshot.child("notes/").getChildren()) {
                            DataBaseNotes note = noteSnapshot.getValue(DataBaseNotes.class);
                            userTask.getNotes().add(note.getUser() + ": " + note.getNote());
                        }
                        userTask.setCompleted(completed);
                        userTask.setVerified(verified);

                        arrayListForTask.addListTask(userTask);             // ADDS DATABASE TASK TO USER-LIST TASKS-ARRAY
                    }
                    if (curUser.getList(listId).getTask(taskId).getNonViewers() != null &&
                            curUser.getList(listId).getTask(taskId).getNonViewers().contains(curUser.getUserId())) {
                        curUser.getList(listId).getListTasks().remove(curUser.getList(listId).getTask(taskId));
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String listId = (String) dataSnapshot.child("listId").getValue();
                String taskId = (String) dataSnapshot.child("taskId").getValue();
                String title = (String) dataSnapshot.child("taskName").getValue();
                String dueDate = (String) dataSnapshot.child("dueDate").getValue();
                String reminderDate = (String) dataSnapshot.child("reminderDate").getValue();
                Boolean completed = (Boolean) dataSnapshot.child("completed").getValue();
                Boolean verified = (Boolean) dataSnapshot.child("verified").getValue();

                User.get().getList(listId).getTask(taskId).getAssignees().clear();
                for (DataSnapshot userSnapshot: dataSnapshot.child("users_assigned_to/").getChildren()) {
                    User.get().getList(listId).getTask(taskId).getAssignees().add(userSnapshot.getKey());   // CHANGES ASSIGNED USERS IN TASK
                }
                User.get().getList(listId).getTask(taskId).getNonViewers().clear();
                for (DataSnapshot userSnapshot: dataSnapshot.child("users_hidden_from/").getChildren()) {
                    User.get().getList(listId).getTask(taskId).getNonViewers().add(userSnapshot.getKey());   // CHANGES HIDDEN USERS IN TASK
                }
                curUser.getList(listId).getTask(taskId).getNotes().clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.child("notes/").getChildren()) {              // CHANGES NOTES IN TASK
                    DataBaseNotes note = noteSnapshot.getValue(DataBaseNotes.class);
                    curUser.getList(listId).getTask(taskId).getNotes().add(note.getUser() + ": " + note.getNote());
                }

                Log.d(TAG, "Updated Task Name: " + title);                  // LOGS THE UPDATED DATA OF THE TASK
                Log.d(TAG, "Updated Due Date: " + dueDate);
                Log.d(TAG, "Updated Reminder Date: " + reminderDate);
                Log.d(TAG, "Updated Completion: " + completed);
                Log.d(TAG, "Updated Verification: " + verified);

                taskNameAlreadyUpdated = (curUser.getList(listId).getTask(taskId).getTaskName()).equals(title);
                if (curUser.getList(listId).getTask(taskId).getDueDate() == null) dueDateAlreadyUpdated = false;
                else dueDateAlreadyUpdated = (format2.format(curUser.getList(listId).getTask(taskId).getDueDate())).equals(dueDate);
                if (curUser.getList(listId).getTask(taskId).getReminderDate() == null) reminderDateAlreadyUpdated = false;
                else reminderDateAlreadyUpdated = (format2.format(curUser.getList(listId).getTask(taskId).getReminderDate())).equals(reminderDate);
                completionAlreadyUpdated = (curUser.getList(listId).getTask(taskId).isCompleted()) == (completed);
                verificationAlreadyUpdated = (curUser.getList(listId).getTask(taskId).isVerified()) == (verified);

                Log.d(TAG, "Task name already updated: " + taskNameAlreadyUpdated); // LOGS IF THE TASK DATA ARE ALREADY UPDATED IN TASKS-ARRAY
                Log.d(TAG, "Due Date already updated: " + dueDateAlreadyUpdated);
                Log.d(TAG, "Reminder Date already updated: " + reminderDateAlreadyUpdated);
                Log.d(TAG, "Completion already updated: " + completionAlreadyUpdated);
                Log.d(TAG, "Verification already updated: " + verificationAlreadyUpdated);

                if (!taskNameAlreadyUpdated) {                                      // TASK DATA ARE NOT YET UPDATED IN USER TASKS-ARRAY

                    curUser.getList(listId).getTask(taskId).setTaskName(title);     // ADDS DATABASE TASK DATA TO USER TASKS-ARRAY
                }
                if (!dueDateAlreadyUpdated && dueDate != null) {

                    try{curUser.getList(listId).getTask(taskId).setDueDate(format2.parse(dueDate));}
                    catch(ParseException e){}
                }
                if (!reminderDateAlreadyUpdated && reminderDate != null) {

                    try{curUser.getList(listId).getTask(taskId).setReminderDate(format2.parse(reminderDate));}
                    catch(ParseException e){}
                }
                if (!completionAlreadyUpdated) {

                    curUser.getList(listId).getTask(taskId).setCompleted(completed);
                }
                if (!verificationAlreadyUpdated) {

                    curUser.getList(listId).getTask(taskId).setVerified(verified);
                }
                if (curUser.getList(listId).getTask(taskId).getNonViewers().contains(curUser.getUserId())) {
                    curUser.getList(listId).removeListTask(curUser.getList(listId).getTask(taskId));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String taskId = (String) dataSnapshot.child("taskId").getValue();
                String listId = (String) dataSnapshot.child("listId").getValue();
                String title = (String) dataSnapshot.child("taskName").getValue();

                Log.d(TAG, "Deleted Task Name: " + title);                  // LOGS THE NAME OF THE DELETED TASK

                taskNotAlreadyDeleted = curUser.getList(listId) != null;

                Log.d(TAG, "Task is already deleted: " + !taskNotAlreadyDeleted);  // LOGS IF THE TASK IS ALREADY DELETED FROM LISTS-ARRAY

                if (taskNotAlreadyDeleted) {                                // LIST NAME IS NOT YET UPDATED IN USER LISTS-ARRAY

                    curUser.getList(listId).removeListTask(curUser.getList(listId).getTask(taskId)); // DELETES DATABASE TASK FROM USER LISTS-ARRAY
                }
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
