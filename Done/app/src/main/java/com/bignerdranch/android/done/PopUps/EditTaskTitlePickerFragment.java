package com.bignerdranch.android.done.PopUps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.UserData.User;

/**
 * Created by michalisgratsias on 30/04/16.
 */
public class EditTaskTitlePickerFragment extends android.support.v4.app.DialogFragment {

    private static final String ARG_TASK_ID = "taskId";
    private static final String ARG_LIST_ID = "listId";
    public static final String EXTRA_TASK_TITLE = "com.bignerdranch.android.done.taskId";
    private EditText mTitleField;
    private String mTaskTitle;

    public static EditTaskTitlePickerFragment newInstance(String taskId, String listId) {  // method to set fragment arguments
        Bundle args = new Bundle();                                         // that replaces the usual fragment constructor
        args.putSerializable(ARG_TASK_ID, taskId);
        args.putSerializable(ARG_LIST_ID, listId);
        EditTaskTitlePickerFragment fragment = new EditTaskTitlePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String taskId = (String) getArguments().getSerializable(ARG_TASK_ID);
        final String listId = (String) getArguments().getSerializable(ARG_LIST_ID);
        final String taskTitle = User.get().getList(listId).getTask(taskId).getTaskName();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_task_title,null);

        mTitleField = (EditText) v.findViewById(R.id.task_title);
        mTitleField.setText(taskTitle);

        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) { // CharSequence is user input
                mTaskTitle = c.toString();
                User.get().getList(listId).getTask(taskId).setTaskName(mTaskTitle);
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }
            public void afterTextChanged(Editable c) {
                // This one too
            }
        });

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.edit_task_title_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mTaskTitle);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String title) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TASK_TITLE, title);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
