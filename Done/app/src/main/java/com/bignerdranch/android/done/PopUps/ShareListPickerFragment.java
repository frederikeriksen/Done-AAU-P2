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
import android.widget.TextView;

import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.R;

/**
 * Created by michalisgratsias on 29/04/16.
 */
public class ShareListPickerFragment extends android.support.v4.app.DialogFragment {

    private TextView mQuestion;
    private static final String ARG_LIST_ID = "listId";
    public static final String EXTRA_EMAIL = "com.bignerdranch.android.done.Share";
    public static final String EXTRA_ID = "com.bignerdranch.android.done.listId";
    private EditText mEmailField;
    private String mEmailAddress;

    public static ShareListPickerFragment newInstance(String listId) {  // method to set fragment arguments
        Bundle args = new Bundle();                                         // that replaces the usual fragment constructor
        args.putSerializable(ARG_LIST_ID, listId);
        ShareListPickerFragment fragment = new ShareListPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String listId = (String) getArguments().getSerializable(ARG_LIST_ID);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_share_list, null);

        mQuestion = (TextView) v.findViewById(R.id.quest);
        mQuestion.setText("What is the email address of the person you would like to share the: " + User.get().getList(listId).getListName() + " list with?");

        mEmailField = (EditText) v.findViewById(R.id.share_email);

        mEmailField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) { // CharSequence is user input
                mEmailAddress = c.toString();
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
                .setTitle(R.string.share_list_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mEmailAddress, listId);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String email, String listId) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ID, listId);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
