package com.bignerdranch.android.done;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by michalisgratsias on 17/04/16.
 */
public class TaskTitlePickerFragment extends DialogFragment{

    public static final String EXTRA_TITLE = "com.bignerdranch.android.done.taskTitle";
    private EditText mTitleField;
    private String mTaskTitle;

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_task_title,null);

        mTitleField = (EditText) v.findViewById(R.id.task_title);

        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) { // CharSequence is user input
                mTaskTitle = c.toString();
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
                .setTitle(R.string.task_title_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,mTaskTitle);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String title) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
