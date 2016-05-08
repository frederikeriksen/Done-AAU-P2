package com.bignerdranch.android.done.AppData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by michalisgratsias on 27/03/16.
 */
public class Task {

    private String mTaskId;
    private String mListId;
    private Date mCreatedDate;
    private String mTaskName;
    private ArrayList<String> mAssignees;
    private ArrayList<String> mNonViewers = new ArrayList<>();
    private Date mDueDate;
    private Date mReminderDate;
    private ArrayList<String> mNotes;
    private String mPhoto;
    //idea is to save photo as a string here and in the getter and setter return it as bitmap;
    //private ArrayList<Image> mPhotos;
    private boolean mCompleted;
    private boolean mVerified;

    public Task(String listId) {                  // constructor with Task ID, List ID
        mListId = listId;
        mAssignees = new ArrayList<>();
        mNonViewers= new ArrayList<>();
        mDueDate = mCreatedDate;
        mReminderDate = mCreatedDate;
        mNotes = new ArrayList<>();
        //mPhotos = new ArrayList<>();
        mCompleted = false;
        mVerified = false;
    }

    public Task() {
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId (String taskId){
        mTaskId = taskId;
    }

    public String getListId() {
        return mListId;
    }

    public void setListId(String listId) {
        mListId = listId;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public ArrayList<String> getAssignees() {
        return mAssignees;
    }

    public void addAssignee(String userId) {mAssignees.add(userId);}

    public void removeAssignee(String userId) {mAssignees.remove(userId);}

    public ArrayList<String> getNonViewers() {
        return mNonViewers;
    }

    public void addNonViewer(String userId) {mNonViewers.add(userId);}

    public void removeNonViewer(String userId) {mNonViewers.remove(userId);}

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        mCreatedDate = createdDate;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public Date getReminderDate() {
        return mReminderDate;
    }

    public void setReminderDate(Date reminderDate) {
        mReminderDate = reminderDate;
    }

    public ArrayList<String> getNotes() {
        return mNotes;
    }

    public void setNotes(ArrayList<String> notes) {
        mNotes = notes;
    }

    public void addNote(String note) {
        mNotes.add(note);
    }

    public void removeNote(String note) {mNotes.remove(note);}

    public Bitmap getPhoto() {
        byte[] imageAsBytes = Base64.decode(mPhoto.getBytes(), Base64.DEFAULT);
        Bitmap photo = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

        return photo;
    }

    public void setPhoto(Bitmap photo) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        mPhoto = encoded;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }

    public boolean isVerified() {
        return mVerified;
    }

    public void setVerified(boolean verified) {
        mVerified = verified;
    }
}
