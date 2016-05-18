package com.bignerdranch.android.done.PopUps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.AppData.User;

/**
 * Created by michalisgratsias on 29/04/16.
 */
public class EditListTitlePickerFragment extends android.support.v4.app.DialogFragment {

    private static final String TAG = "DoneActivity";
    private static final String ARG_LIST_ID = "listId";
    public static final String EXTRA_ID = "com.bignerdranch.android.done.listId";
    public static final String EXTRA_TITLE = "com.bignerdranch.android.done.listTitle";
    private EditText mTitleField;
    private String mListTitle;

    public static EditListTitlePickerFragment newInstance(String listId) {  // method to set fragment arguments
        Bundle args = new Bundle();                                         // that replaces the usual fragment constructor
        args.putSerializable(ARG_LIST_ID, listId);
        EditListTitlePickerFragment fragment = new EditListTitlePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String listId = (String) getArguments().getSerializable(ARG_LIST_ID);
        final String listTitle = User.get().getList(listId).getListName();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_list_title,null);

        mTitleField = (EditText) v.findViewById(R.id.list_title);
        mTitleField.setText(listTitle);

        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) { // CharSequence is user input
                mListTitle = (c.toString());
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // empty
            }
            public void afterTextChanged(Editable c) {
                // empty
            }
        });

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.edit_list_title_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,listId, mListTitle);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String id, String title) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
