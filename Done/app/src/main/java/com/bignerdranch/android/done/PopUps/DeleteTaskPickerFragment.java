package com.bignerdranch.android.done.PopUps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.UserData.User;

/**
 * Created by michalisgratsias on 29/04/16.
 */
public class DeleteTaskPickerFragment extends android.support.v4.app.DialogFragment {

    private TextView mQuestion;
    private static final String ARG_DELETE_TASK = "taskId";
    private static final String ARG_LIST_ID = "listId";
    public static final String EXTRA_ID = "com.bignerdranch.android.done.taskId";

    public static DeleteTaskPickerFragment newInstance(String taskId, String listId) {  // method to set fragment arguments
        Bundle args = new Bundle();                                         // that replaces the usual fragment constructor
        args.putSerializable(ARG_DELETE_TASK, taskId);
        args.putSerializable(ARG_LIST_ID, listId);
        DeleteTaskPickerFragment fragment = new DeleteTaskPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String taskId = (String) getArguments().getSerializable(ARG_DELETE_TASK);
        final String listId = (String) getArguments().getSerializable(ARG_LIST_ID);
        final String taskTitle = User.get().getList(listId).getTask(taskId).getTaskName();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_delete, null);

        mQuestion = (TextView) v.findViewById(R.id.delete);
        mQuestion.setText("Are you sure you want to delete the task: " + taskTitle + " and all of its contents?");

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.task_delete_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, taskId);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, taskId);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String taskId) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, taskId);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
