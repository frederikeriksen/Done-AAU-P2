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

import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.R;

/**
 * Created by michalisgratsias on 08/05/16.
 */
public class DeleteUserPickerFragment extends android.support.v4.app.DialogFragment {

    public static final String EXTRA_NAME = "com.bignerdranch.android.done.userName";
    private TextView mQuestion;

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String userName = User.get().getUserName();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_delete, null);

        mQuestion = (TextView) v.findViewById(R.id.delete);
        mQuestion.setText("Are you sure you want to delete the user: " + userName + " and all of its data?");

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.user_delete_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, userName);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, userName);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String userName) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NAME, userName);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
