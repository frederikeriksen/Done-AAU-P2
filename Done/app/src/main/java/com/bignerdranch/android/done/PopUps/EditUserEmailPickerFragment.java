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

import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.R;

/**
 * Created by michalisgratsias on 08/05/16.
 */
public class EditUserEmailPickerFragment extends android.support.v4.app.DialogFragment {

    public static final String EXTRA_USER_EMAIL = "com.bignerdranch.android.done.userEmail";
    private EditText mTitleField;
    private String mUserEmail;

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String userEmail = User.get().getEmail();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_user_email,null);

        mTitleField = (EditText) v.findViewById(R.id.dialog_email);
        mTitleField.setText(userEmail);

        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) { // CharSequence is user input
                mUserEmail = c.toString();
                User.get().setEmail(mUserEmail);
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
                .setTitle(R.string.edit_user_email_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mUserEmail);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String title) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_EMAIL, title);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
