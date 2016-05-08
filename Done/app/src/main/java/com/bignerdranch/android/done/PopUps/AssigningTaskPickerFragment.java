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
public class AssigningTaskPickerFragment extends android.support.v4.app.DialogFragment {

    public static final String EXTRA_ASSIGNED = "com.bignerdranch.android.done.Assign";
    private static final String ARG_USERS = "users";
    private static final String ARG_LIST_ID = "listId";
    private static final String ARG_TASK_ID = "taskId";
    private ArrayList<String> possAssignees = new ArrayList<>();

    public static AssigningTaskPickerFragment newInstance(ArrayList<String> possAssignees, String listId, String taskId) {       // method to set fragment arguments
        Bundle args = new Bundle();                                 // that replaces the frag. constructor
        args.putSerializable(ARG_USERS, possAssignees);
        args.putSerializable(ARG_LIST_ID, listId);
        args.putSerializable(ARG_TASK_ID, taskId);
        AssigningTaskPickerFragment fragment = new AssigningTaskPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstance) {

        possAssignees = (ArrayList<String>) getArguments().getSerializable(ARG_USERS);
        final String listId = (String) getArguments().getSerializable(ARG_LIST_ID);
        final String taskId = (String) getArguments().getSerializable(ARG_TASK_ID);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_users,null);

        final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        final CharSequence[] items = new CharSequence[possAssignees.size()];
        final boolean[] checkedItems = new boolean[possAssignees.size()];

        for (int i=0; i<possAssignees.size();i++) {
            items[i]=RegisteredUsers.get().getUser(possAssignees.get(i)).getUserName();
            if (User.get().getList(listId).getTask(taskId).getAssignees().contains(possAssignees.get(i))) {
                mSelectedItems.add(i);
                checkedItems[i] = true;
            }
            else checkedItems[i] = false;
        }

        return new AlertDialog.Builder(getActivity())          // this class provides a fluent interface for constructing
                .setView(v)
                .setTitle(R.string.assign_task_picker_title)      // an object of Alert Dialog (pop-up)
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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

    private void sendResult(int resultCode, ArrayList assignees) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ASSIGNED, assignees);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}

/*
final CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
                // arraylist to keep the selected items
                final ArrayList seletedItems=new ArrayList();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select The Difficulty Level");
                builder.setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                 // indexSelected contains the index of item (of which checkbox checked)
                 @Override
                 public void onClick(DialogInterface dialog, int indexSelected,
                         boolean isChecked) {
                     if (isChecked) {
                         // If the user checked the item, add it to the selected items
                         // write your code when user checked the checkbox
                         seletedItems.add(indexSelected);
                     } else if (seletedItems.contains(indexSelected)) {
                         // Else, if the item is already in the array, remove it
                         // write your code when user Uchecked the checkbox
                         seletedItems.remove(Integer.valueOf(indexSelected));
                     }
                 }
             })
              // Set the action buttons
             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                     //  Your code when user clicked on OK
                     //  You can write the code  to save the selected item here

                 }
             })
             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                    //  Your code when user clicked on Cancel

                 }
             });

                dialog = builder.create();//AlertDialog dialog; create like this outside onClick
                dialog.show();
        }

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
    mSelectedItems = new ArrayList();  // Where we track the selected items
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    // Set the dialog title
    builder.setTitle(R.string.pick_toppings)
    // Specify the list array, the items to be selected by default (null for none),
    // and the listener through which to receive callbacks when items are selected
           .setMultiChoiceItems(R.array.toppings, null,
                      new DialogInterface.OnMultiChoiceClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which,
                       boolean isChecked) {
                   if (isChecked) {
                       // If the user checked the item, add it to the selected items
                       mSelectedItems.add(which);
                   } else if (mSelectedItems.contains(which)) {
                       // Else, if the item is already in the array, remove it
                       mSelectedItems.remove(Integer.valueOf(which));
                   }
               }
           })
    // Set the action buttons
           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   // User clicked OK, so save the mSelectedItems results somewhere
                   // or return them to the component that opened the dialog
                   ...
               }
           })
           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
              //         ...
               }
           });

    return builder.create();
}
*/