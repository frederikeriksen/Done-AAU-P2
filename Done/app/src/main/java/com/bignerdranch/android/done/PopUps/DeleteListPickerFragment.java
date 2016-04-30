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
public class DeleteListPickerFragment extends android.support.v4.app.DialogFragment {

    private TextView mQuestion;
    private static final String ARG_DELETE_LIST = "listTitle";
    public static final String EXTRA_ID = "com.bignerdranch.android.done.listId";

    public static DeleteListPickerFragment newInstance(String listId) {  // method to set fragment arguments
        Bundle args = new Bundle();                                         // that replaces the usual fragment constructor
        args.putSerializable(ARG_DELETE_LIST, listId);
        DeleteListPickerFragment fragment = new DeleteListPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        final String listId = (String) getArguments().getSerializable(ARG_DELETE_LIST);
        final String listTitle = User.get().getList(listId).getListName();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_delete, null);

        mQuestion = (TextView) v.findViewById(R.id.delete);
        mQuestion.setText("Are you sure you want to delete the list: " + listTitle + " and all of its contents?");

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.list_delete_picker_title)      // an object of Alert Dialog (pop-up)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, listId);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, listId);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String listId) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, listId);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
