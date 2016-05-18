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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    User curUser;
    boolean userAlreadyAdded;
    boolean userNameAlreadyUpdated;
    boolean passwordAlreadyUpdated;
    boolean emailAlreadyUpdated;
    boolean photoAlreadyUpdated;
    boolean userNotAlreadyDeleted;
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
    boolean existsUserListForTask;

    public FireBaseDataRetrieve() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        curUser = User.get();
        Log.d(TAG, "User Name: " + User.get().getUserName());                // LOGS THE NAME OF THE CURRENT APP USER

        //           R E T R I E V E S     U S E R S
        Firebase mRefUsers = new Firebase("https://doneaau.firebaseio.com/users/");

        mRefUsers.addChildEventListener(new ChildEventListener() {          // Retrieves user registration data as they appear in the database

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) { // Retrieves new/old registered users for the user

                DataBaseUsers user = snapshot.getValue(DataBaseUsers.class);

                Log.d(TAG, "User Name: " + user.getUserName());          // LOGS THE NAME OF THE USER WHICH IS RETRIEVED

                userAlreadyAdded = RegisteredUsers.get().getUser(user.getUserId()) != null;

                Log.d(TAG, "User already added: " + userAlreadyAdded);  // LOGS IF THE USER IS ALREADY ADDED TO REG.USERS-ARRAY

                if (!userAlreadyAdded) {                                // USER IS NOT YET ADDED TO REG.USERS-ARRAY
                    DataBaseUsers currUser = new DataBaseUsers();
                    currUser.setUserId(user.getUserId());
                    currUser.setUserName(user.getUserName());
                    currUser.setPassword(user.getPassword());
                    currUser.setEmail(user.getEmail());
                    currUser.setPhoto(user.getPhoto());
                    RegisteredUsers.get().getUsers().add(currUser);     // ADDS DATABASE USER TO REG.USERS-ARRAY
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { // Retrieves updated data for the user

                String userId = (String) dataSnapshot.child("userId").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();
                String password = (String) dataSnapshot.child("password").getValue();
                String email = (String) dataSnapshot.child("email").getValue();
                String photo = (String) dataSnapshot.child("photo").getValue();

                Log.d(TAG, "Updated User Name: " + userName);
                Log.d(TAG, "Updated Password: " + password);
                Log.d(TAG, "Updated Email: " + email);
                Log.d(TAG, "Updated Photo: " + photo);

                userNameAlreadyUpdated = (RegisteredUsers.get().getUser(userId).getUserName()).equals(userName);
                passwordAlreadyUpdated = (RegisteredUsers.get().getUser(userId).getPassword()).equals(password);
                emailAlreadyUpdated = (RegisteredUsers.get().getUser(userId).getEmail()).equals(email);
                photoAlreadyUpdated = (RegisteredUsers.get().getUser(userId).getPhoto()).equals(photo);

                Log.d(TAG, "User Name already updated: " + userNameAlreadyUpdated);
                Log.d(TAG, "Password already updated: " + passwordAlreadyUpdated);
                Log.d(TAG, "Email already updated: " + emailAlreadyUpdated);
                Log.d(TAG, "Photo already updated: " + photoAlreadyUpdated);

                if (!userNameAlreadyUpdated) {                                      // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    RegisteredUsers.get().getUser(userId).setUserName(userName);    // ADDS DATABASE USER DATA TO REG-USER ARRAY
                }
                if (!passwordAlreadyUpdated) {                                      // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    RegisteredUsers.get().getUser(userId).setPassword(password);    // ADDS DATABASE USER DATA TO REG-USER ARRAY
                }
                if (!emailAlreadyUpdated) {                                         // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    RegisteredUsers.get().getUser(userId).setEmail(email);          // ADDS DATABASE USER DATA TO REG-USER ARRAY
                }
                if (!photoAlreadyUpdated) {                                         // USER DATA ARE NOT YET UPDATED IN USER ARRAY

                    RegisteredUsers.get().getUser(userId).setPhoto(photo);          // ADDS DATABASE USER DATA TO REG-USER ARRAY
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
                ArrayList<String> users = new ArrayList<String>();  // shared users
                for (DataSnapshot user : snapshot.child("shared_users/").getChildren()) {
                    users.add(user.getKey());
                    Log.d(TAG, "AddedList - Users: " + user.getKey());
                }
                Log.d(TAG, "Shared Users: " + users.contains(curUser.getUserId()));

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
                        for (String u: users) {
                            currList.addListUser(u);
                        }
                        curUser.addUserList(currList);                      // ADDS DATABASE LIST TO USER LISTS-ARRAY
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { // Retrieves updated list data for the user

                String listId = (String) dataSnapshot.child("listId").getValue();
                String creatorId = (String) dataSnapshot.child("creatorId").getValue();
                String listName = (String) dataSnapshot.child("listName").getValue();
                ArrayList<String> users = new ArrayList<String>();              // shared users
                for (DataSnapshot user : dataSnapshot.child("shared_users/").getChildren()) {
                    users.add(user.getKey());                                   // COLLECTS SHARED USERS IN LIST
                    Log.d(TAG, "AddedList - Users: " + user.getKey());
                }
                Log.d(TAG, "Shared Users: " + users.contains(curUser.getUserId()));

                if (User.get().getList(listId) != null) {                   // LIST ALREADY IN USER-LISTS (OWN OR SHARED)

                    if (curUser.getUserId().equals(creatorId) || users.contains(curUser.getUserId())) {

                        Log.d(TAG, "Updated List Name: " + listName);                  // LOGS THE UPDATED NAME OF THE LIST

                        listNameAlreadyUpdated = (curUser.getList(listId).getListName()).equals(listName);

                        Log.d(TAG, "List name already updated: " + listNameAlreadyUpdated);  // LOGS IF THE LIST NAME IS ALREADY UPDATED IN LISTS-ARRAY

                        if (!listNameAlreadyUpdated) {                              // LIST NAME IS NOT YET UPDATED IN USER LISTS-ARRAY

                            curUser.getList(listId).setListName(listName);             // CHANGES DATABASE LIST NAME IN USER LISTS-ARRAY LIST NAME
                        }
                        User.get().getList(listId).getListUsers().clear();
                        for (String u : users) {
                            User.get().getList(listId).getListUsers().add(u);       // CHANGES SHARED USERS IN LIST
                        }
                    }
                    else {
                        User.get().removeUserList(User.get().getList(listId));  // REMOVES LIST IF UN-SHARED AND NOT OWNED
                    }
                }
                else {                                                      // LIST NOT IN USER-LISTS
                    if (users.contains(curUser.getUserId())) {              // IF LIST SHARED
                        final List currList = new List(creatorId);
                        currList.setListId(listId);
                        currList.setListName(listName);
                        currList.setCreatorId(creatorId);
                        for (String u: users) {
                            currList.addListUser(u);                        // ADDS SHARED USERS IN LIST
                        }
                        curUser.addUserList(currList);
                    }                                           // ADDS DATABASE LIST TO USER LISTS-ARRAY
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
                ArrayList<String> users = new ArrayList<String>();                  // hidden users
                for (DataSnapshot userSnapshot : snapshot.child("users_hidden_from/").getChildren()) {
                    users.add(userSnapshot.getKey());                               // COLLECTS HIDDEN USERS IN TASK
                }

                existsUserListForTask = curUser.getList(listId) != null;

                if (existsUserListForTask) {                                // THERE IS A USER-LIST, WHERE THE TASK MUST GO

                    Log.d(TAG, "Task Name: " + taskName);                   // LOGS THE NAME OF THE TASK

                    taskAlreadyAdded = curUser.getList(listId).getTask(taskId) != null;

                    Log.d(TAG, "Task already added: " + taskAlreadyAdded);  // LOGS IF THE TASK IS ALREADY ADDED TO TASKS-ARRAY

                    if (!users.contains(curUser.getUserId())) {             // IF TASK NOT HIDDEN

                        if (!taskAlreadyAdded) {                            // TASK IS NOT YET ADDED TO USER-LIST TASKS-ARRAY
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
                            for (String u: users) {
                                userTask.getNonViewers().add(u);
                                Log.d(TAG, "Task Hidden From: " + RegisteredUsers.get().getUser(u));
                            }
                            for (DataSnapshot noteSnapshot : snapshot.child("notes/").getChildren()) {
                                DataBaseNotes note = noteSnapshot.getValue(DataBaseNotes.class);
                                userTask.getNotes().add(note.getUser() + ": " + note.getNote());
                            }
                            for (DataSnapshot photoSnapshot : snapshot.child("photos/").getChildren()) {
                                String photo = photoSnapshot.getValue(String.class);
                                userTask.addPhoto(photo);
                            }
                            userTask.setCompleted(completed);
                            userTask.setVerified(verified);

                            curUser.getList(listId).addListTask(userTask);             // ADDS DATABASE TASK TO USER-LIST TASKS-ARRAY
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String listId = (String) dataSnapshot.child("listId").getValue();
                String taskId = (String) dataSnapshot.child("taskId").getValue();
                String title = (String) dataSnapshot.child("taskName").getValue();
                String createdDate = (String) dataSnapshot.child("createdDate").getValue();
                String dueDate = (String) dataSnapshot.child("dueDate").getValue();
                String reminderDate = (String) dataSnapshot.child("reminderDate").getValue();
                Boolean completed = (Boolean) dataSnapshot.child("completed").getValue();
                Boolean verified = (Boolean) dataSnapshot.child("verified").getValue();
                ArrayList<String> users = new ArrayList<String>();                  // hidden users
                for (DataSnapshot userSnapshot : dataSnapshot.child("users_hidden_from/").getChildren()) {
                    users.add(userSnapshot.getKey());                               // COLLECTS HIDDEN USERS IN TASK
                }

                existsUserListForTask = curUser.getList(listId) != null;

                if (existsUserListForTask) {                             // THERE IS A USER-LIST, WHERE THE TASK MUST GO

                    Log.d(TAG, "Task Name: " + title);                   // LOGS THE NAME OF THE TASK

                    if (User.get().getList(listId).getTask(taskId) != null) {               // TASK EXISTS IN USER-LIST - OWN OR SHARED

                        if (!users.contains(curUser.getUserId())) {                         // IF TASK NOT HIDDEN

                            User.get().getList(listId).getTask(taskId).getNonViewers().clear();
                            for (String u: users) {
                                User.get().getList(listId).getTask(taskId).getNonViewers().add(u);   // CHANGES HIDDEN USERS IN TASK
                            }

                            User.get().getList(listId).getTask(taskId).getAssignees().clear();
                            for (DataSnapshot userSnapshot : dataSnapshot.child("users_assigned_to/").getChildren()) {
                                User.get().getList(listId).getTask(taskId).getAssignees().add(userSnapshot.getKey());   // CHANGES ASSIGNED USERS IN TASK
                            }

                            for (DataSnapshot noteSnapshot : dataSnapshot.child("notes/").getChildren()) { // CHANGES NOTES IN TASK
                                DataBaseNotes note = noteSnapshot.getValue(DataBaseNotes.class);
                                curUser.getList(listId).getTask(taskId).getNotes().add(note.getUser() + ": " + note.getNote());
                            }

                            for (DataSnapshot noteSnapshot : dataSnapshot.child("photos/").getChildren()) {
                                String photo = noteSnapshot.getValue(String.class);
                                curUser.getList(listId).getTask(taskId).addPhoto(photo);
                            }

                            Log.d(TAG, "Updated Task Name: " + title);                  // LOGS THE UPDATED DATA OF THE TASK
                            Log.d(TAG, "Updated Due Date: " + dueDate);
                            Log.d(TAG, "Updated Reminder Date: " + reminderDate);

                            taskNameAlreadyUpdated = (curUser.getList(listId).getTask(taskId).getTaskName()).equals(title);
                            if (curUser.getList(listId).getTask(taskId).getDueDate() == null)
                                dueDateAlreadyUpdated = false;
                            else
                                dueDateAlreadyUpdated = (format2.format(curUser.getList(listId).getTask(taskId).getDueDate())).equals(dueDate);
                            if (curUser.getList(listId).getTask(taskId).getReminderDate() == null)
                                reminderDateAlreadyUpdated = false;
                            else
                                reminderDateAlreadyUpdated = (format2.format(curUser.getList(listId).getTask(taskId).getReminderDate())).equals(reminderDate);

                            Log.d(TAG, "Task name already updated: " + taskNameAlreadyUpdated); // LOGS IF THE TASK DATA ARE ALREADY UPDATED IN TASKS-ARRAY
                            Log.d(TAG, "Due Date already updated: " + dueDateAlreadyUpdated);
                            Log.d(TAG, "Reminder Date already updated: " + reminderDateAlreadyUpdated);

                            if (!taskNameAlreadyUpdated) {                                      // TASK DATA ARE NOT YET UPDATED IN USER TASKS-ARRAY

                                curUser.getList(listId).getTask(taskId).setTaskName(title);     // ADDS DATABASE TASK DATA TO USER TASKS-ARRAY
                            }
                            if (!dueDateAlreadyUpdated && dueDate != null) {

                                try {
                                    curUser.getList(listId).getTask(taskId).setDueDate(format2.parse(dueDate));
                                } catch (ParseException e) {
                                }
                            }
                            if (!reminderDateAlreadyUpdated && reminderDate != null) {

                                try {
                                    curUser.getList(listId).getTask(taskId).setReminderDate(format2.parse(reminderDate));
                                } catch (ParseException e) {
                                }
                            }
                            Log.d(TAG, "Updated Completion: " + completed);
                            Log.d(TAG, "Updated Verification: " + verified);

                            completionAlreadyUpdated = (curUser.getList(listId).getTask(taskId).isCompleted()) == (completed);
                            verificationAlreadyUpdated = (curUser.getList(listId).getTask(taskId).isVerified()) == (verified);

                            Log.d(TAG, "Completion already updated: " + completionAlreadyUpdated);
                            Log.d(TAG, "Verification already updated: " + verificationAlreadyUpdated);

                            if (!completionAlreadyUpdated) {

                                curUser.getList(listId).getTask(taskId).setCompleted(completed);
                            }
                            if (!verificationAlreadyUpdated) {

                                curUser.getList(listId).getTask(taskId).setVerified(verified);
                            }
                        }
                        else {
                            curUser.getList(listId).removeListTask(curUser.getList(listId).getTask(taskId));    // TASK REMOVED IF HIDDEN
                        }
                    }
                    else {                                                                  // TASK NOT IN USER-LIST
                        if (!users.contains(curUser.getUserId())) {                         // IF TASK NOT HIDDEN
                            Task userTask = new Task(listId);
                            userTask.setTaskId(taskId);
                            userTask.setListId(listId);
                            userTask.setTaskName(title);
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
                            for (DataSnapshot userSnapshot : dataSnapshot.child("users_assigned_to/").getChildren()) {
                                userTask.getAssignees().add(userSnapshot.getKey());   // CHANGES ASSIGNED USERS IN TASK
                            }
                            for (String u: users) {
                                userTask.getNonViewers().add(u);                  // CHANGES HIDDEN USERS IN TASK
                            }
                            for (DataSnapshot noteSnapshot : dataSnapshot.child("notes/").getChildren()) {          // CHANGES NOTES IN TASK
                                DataBaseNotes note = noteSnapshot.getValue(DataBaseNotes.class);
                                userTask.getNotes().add(note.getUser() + ": " + note.getNote());
                            }
                            for (DataSnapshot noteSnapshot : dataSnapshot.child("photos/").getChildren()) {
                                String photo = noteSnapshot.getValue(String.class);
                                userTask.addPhoto(photo);
                            }
                            userTask.setCompleted(completed);
                            userTask.setVerified(verified);

                            curUser.getList(listId).addListTask(userTask);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String taskId = (String) dataSnapshot.child("taskId").getValue();
                String listId = (String) dataSnapshot.child("listId").getValue();
                String title = (String) dataSnapshot.child("taskName").getValue();

                Log.d(TAG, "Deleted Task Name: " + title);                  // LOGS THE NAME OF THE DELETED TASK

                existsUserListForTask = curUser.getList(listId) != null;

                if (existsUserListForTask) {

                    taskNotAlreadyDeleted = curUser.getList(listId).getTask(taskId) != null;

                    Log.d(TAG, "Task is already deleted: " + !taskNotAlreadyDeleted);  // LOGS IF THE TASK IS ALREADY DELETED FROM LISTS-ARRAY

                    if (taskNotAlreadyDeleted) {                                // LIST NAME IS NOT YET UPDATED IN USER LISTS-ARRAY

                        curUser.getList(listId).removeListTask(curUser.getList(listId).getTask(taskId)); // DELETES DATABASE TASK FROM USER LISTS-ARRAY
                    }
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
        stopService(new Intent(this, FireBaseDataRetrieve.class));
        super.onDestroy();

    }
}
