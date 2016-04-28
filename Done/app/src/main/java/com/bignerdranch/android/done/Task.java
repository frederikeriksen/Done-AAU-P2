package com.bignerdranch.android.done;

import android.media.Image;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by michalisgratsias on 27/03/16.
 */
public class Task {

    private String mTaskId;
    private String mListId;
    private Date mCreatedDate;
    private String mTaskName;
    private ArrayList<String> mAssignees;
    private ArrayList<String> mViewers = new ArrayList<>();
    private Date mDueDate;
    private Date mReminderDate;
    private ArrayList<String> mNotes;
    private ArrayList<Image> mPhotos;
    private boolean mCompleted;
    private boolean mVerified;

    public Task(String listId) {                  // constructor with Task ID, List ID
        mListId = listId;
        mAssignees = new ArrayList<>();
        mViewers= new ArrayList<>();
        mDueDate = new Date();
        mReminderDate = new Date();
        mNotes = new ArrayList<>();
        mPhotos = new ArrayList<>();
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

    public void addAssignee(String mUserName) {
        // to be implemented
    }

    public void removeAssignee(String mUserName) {
        // to be implemented
    }

    public ArrayList<String> getViewers() {
        return mViewers;
    }

    public void addViewer(String mUserName) {
        // to be implemented
    }

    public void removeViewer(String mUserName) {
        // to be implemented
    }

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

    public void removeNote(String mNote) {
        // to be implemented
    }

    public ArrayList<Image> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(ArrayList<Image> photos) {
        mPhotos = photos;
    }

    public void addPhoto(Image mPhoto) {
        // to be implemented
    }

    public void removePhoto(Image mPhoto) {
        // to be implemented
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
