package com.bignerdranch.android.done.PopUps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.bignerdranch.android.done.AppData.RegisteredUsers;
import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.R;
import java.util.ArrayList;

/**
 * Created by michalisgratsias on 30/04/16.
 */
public class HidingTaskPickerFragment extends android.support.v4.app.DialogFragment  {

    public static final String EXTRA_HIDDEN_FROM = "com.bignerdranch.android.done.Hide";
    private static final String ARG_USERS = "users";
    private static final String ARG_LIST_ID = "listId";
    private static final String ARG_TASK_ID = "taskId";
    private ArrayList<String> possNonViewers = new ArrayList<>();

    public static HidingTaskPickerFragment newInstance(ArrayList<String> possNonViewers, String listId, String taskId) {       // method to set fragment arguments
        Bundle args = new Bundle();                                 // that replaces the frag. constructor
        args.putSerializable(ARG_USERS, possNonViewers);
        args.putSerializable(ARG_LIST_ID, listId);
        args.putSerializable(ARG_TASK_ID, taskId);
        HidingTaskPickerFragment fragment = new HidingTaskPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        possNonViewers = (ArrayList<String>) getArguments().getSerializable(ARG_USERS);
        final String listId = (String) getArguments().getSerializable(ARG_LIST_ID);
        final String taskId = (String) getArguments().getSerializable(ARG_TASK_ID);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_users,null);

        final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        final CharSequence[] items = new CharSequence[possNonViewers.size()];
        final boolean[] checkedItems = new boolean[possNonViewers.size()];

        for (int i=0; i<possNonViewers.size();i++) {
            items[i]=RegisteredUsers.get().getUser(possNonViewers.get(i)).getUserName();
            if (User.get().getList(listId).getTask(taskId).getNonViewers().contains(possNonViewers.get(i))) {
                mSelectedItems.add(i);
                checkedItems[i] = true;
            }
            else checkedItems[i] = false;
        }

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.hide_task_picker_title)      // an object of Alert Dialog (pop-up)
                .setMultiChoiceItems(items, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    // here you pass the object that implements
                    @Override                                                                      // the listener interface
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mSelectedItems);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, ArrayList nonViewers) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_HIDDEN_FROM, nonViewers);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
