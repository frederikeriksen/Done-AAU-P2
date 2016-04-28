package com.bignerdranch.android.done;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                 // from support library
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by michalisgratsias on 03/04/16.
 */
public class TaskFragment extends Fragment{

    private static final String TAG = "DoneActivity";
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_LIST_ID = "list_id";
    private static final String DIALOG_DATE1 = "DialogDate1"; // uniquely identifies the Fragment in the FM list
    private static final String DIALOG_DATE2 = "DialogDate2";
    private static final String DIALOG_NOTES = "DialogNotes";
    private Task mTask;
    private EditText mTitleField;
    private Button mAssignedTo;
    private Button mHiddenFrom;
    private Button mDueDateButton;
    private Button mReminderDateButton;
    private Button mAddNote;
    private TextView mNotesText;
    private Button mAddPhoto;
    private CheckBox mCompletedCheckBox;
    private CheckBox mVerifiedCheckBox;

    // Fragment-Arguments work just like Intent-Extras for an Activity
    public static TaskFragment newInstance(String taskId, String listId) {   // we use a method to create Fragment instead of using Constructor
        Bundle args = new Bundle();                         // creates Bundle for arguments
        args.putSerializable(ARG_TASK_ID, taskId);          // adds task ID to Bundle
        args.putSerializable(ARG_LIST_ID, listId);          // adds list ID to Bundle
        TaskFragment fragment = new TaskFragment();         // creates Fragment instance
        fragment.setArguments(args);                        // sets Arguments
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {       // it is Public because it can be called by various activities hosting it
        super.onCreate(savedInstanceState);
        String taskId = (String) getArguments().getSerializable(ARG_TASK_ID);   // accessing Fragment arguments for task id
        String listId = (String) getArguments().getSerializable(ARG_LIST_ID);   //  RETRIEVES List ID from Intent
        mTask = User.get().getList(listId).getTask(taskId);    // using a get method to get Task from ids
    }

    private RecyclerView mTaskRecyclerView;         // RecyclerView creates only enough views to fill the screen and scrolls them
    private TaskAdapter mAdapter;                  // Adapter controls the data to be displayed by RecyclerView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        mTaskRecyclerView = (RecyclerView)view.findViewById(R.id.single_task_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));    // handles the
        updateUI();     // sets up the UI                     // positioning of items and defines the scrolling behaviour
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 0: {
                Date date = (Date) data.getSerializableExtra(DueDatePickerFragment.EXTRA_DATE);
                mTask.setDueDate(date);
                updateDueDate();
                break;
            }
            case 1: {
                Date date = (Date) data.getSerializableExtra(ReminderDatePickerFragment.EXTRA_DATE);
                mTask.setReminderDate(date);
                updateReminderDate();
                break;
            }
            case 2: {
                String note = (String) data.getSerializableExtra(NotesPickerFragment.EXTRA_TITLE);
                mTask.addNote(note);
                mNotesText.setText(mNotesText.getText() + "\n" + User.get().getUserName() + ": "+note);
                break;
            }



        }
    }

    private void updateDueDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd, yyyy");
        mDueDateButton.setText(format.format(mTask.getDueDate()));
    }

    private void updateReminderDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd, yyyy");
        mReminderDateButton.setText(format.format(mTask.getReminderDate()));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new TaskAdapter(9);              // passing 9 fragments to the Recycle-View Adapter
            mTaskRecyclerView.setAdapter(mAdapter);}
        else {mAdapter.notifyDataSetChanged();}
    }

    private class TaskHolder0 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder0(View itemView) {     // constructor - stashes the views
            super(itemView);
            mTitleField = (EditText) itemView.findViewById(R.id.task_title);
        }
        public void bindTask() {
            mTitleField.setText(mTask.getTaskName());
            mTitleField.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence c, int start, int before, int count) { // CharSequence is user input
                    mTask.setTaskName(c.toString());
                }
                public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                    // This space intentionally left blank
                }
                public void afterTextChanged(Editable c) {
                    // This one too
                }
            });
        }
    }

    private class TaskHolder1 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder1(View itemView) {     // constructor - stashes the views
            super(itemView);
            mAssignedTo = (Button) itemView.findViewById(R.id.assigned_to);
        }
        public void bindTask() {
            mAssignedTo.setText("None");
            mAssignedTo.setEnabled(false);
        }
    }

    private class TaskHolder2 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder2(View itemView) {     // constructor - stashes the views
            super(itemView);
            mHiddenFrom = (Button) itemView.findViewById(R.id.hidden_from);
        }
        public void bindTask() {
            mHiddenFrom.setText("None");
            mHiddenFrom.setEnabled(false);
        }
    }

    private class TaskHolder3 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder3(View itemView) {     // constructor - stashes the views
            super(itemView);
            mDueDateButton = (Button) itemView.findViewById(R.id.due_date);
        }
        public void bindTask() {
            updateDueDate();
            mDueDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    DueDatePickerFragment dialog = DueDatePickerFragment.newInstance(mTask.getDueDate()); //shows due date
                    dialog.setTargetFragment(TaskFragment.this, 0);
                    dialog.show(manager, DIALOG_DATE1);
                }
            });
        }
    }

    private class TaskHolder4 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder4(View itemView) {     // constructor - stashes the views
            super(itemView);
            mReminderDateButton = (Button) itemView.findViewById(R.id.reminder_date);
        }
        public void bindTask() {
            updateReminderDate();
            mReminderDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    ReminderDatePickerFragment dialog = ReminderDatePickerFragment.newInstance(mTask.getReminderDate()); //shows reminder date
                    dialog.setTargetFragment(TaskFragment.this, 1);
                    dialog.show(manager, DIALOG_DATE2);
                }
            });
        }
    }

    private class TaskHolder5 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder5(View itemView) {     // constructor - stashes the views
            super(itemView);
            mAddNote = (Button) itemView.findViewById(R.id.add_note);
            mNotesText = (TextView) itemView.findViewById(R.id.notes);
            for (String n: mTask.getNotes()) mNotesText.setText(mNotesText.getText()+"\n"+ User.get().getUserName() + ": "+n);
        }
        public void bindTask() {
            mAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    NotesPickerFragment dialog = new NotesPickerFragment(); //shows reminder date
                    dialog.setTargetFragment(TaskFragment.this, 2);
                    dialog.show(manager, DIALOG_NOTES);
                }
            });
        }
    }

    private class TaskHolder6 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder6(View itemView) {     // constructor - stashes the views
            super(itemView);
            mAddPhoto = (Button) itemView.findViewById(R.id.add_photo);
        }
        public void bindTask() {
            mAddPhoto.setText("None");
            mAddPhoto.setEnabled(false);
        }
    }

    private class TaskHolder7 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder7(View itemView) {     // constructor - stashes the views
            super(itemView);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.task_completed);
        }
        public void bindTask() {
            mCompletedCheckBox.setChecked(mTask.isCompleted());
            mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mTask.setCompleted(isChecked);
                }
            });
        }
    }

    private class TaskHolder8 extends RecyclerView.ViewHolder {  // viewholder class holds reference to the entire view passed to super(view)

        public TaskHolder8(View itemView) {     // constructor - stashes the views
            super(itemView);
            mVerifiedCheckBox = (CheckBox) itemView.findViewById(R.id.task_verified);
        }
        public void bindTask() {
            mVerifiedCheckBox.setChecked(mTask.isCompleted());
            mVerifiedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mTask.setCompleted(isChecked);
                }
            });
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {  // adapter class
        // creates needed viewholders, binds them to the data
        private int mAdapterLength;

        public TaskAdapter(int length) {        // constructor
            mAdapterLength = length;
        }

        @Override
        public int getItemViewType(int position){
            return position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {              // needs new view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType) {
                case 0: {View view = layoutInflater.inflate(R.layout.fragment_task_title, parent, false); return new TaskHolder0(view);}
                case 1: {View view = layoutInflater.inflate(R.layout.fragment_task_assignees, parent, false); return new TaskHolder1(view);}
                case 2: {View view = layoutInflater.inflate(R.layout.fragment_task_viewers, parent, false); return new TaskHolder2(view);}
                case 3: {View view = layoutInflater.inflate(R.layout.fragment_task_due_date, parent, false); return new TaskHolder3(view);}
                case 4: {View view = layoutInflater.inflate(R.layout.fragment_task_reminder_date, parent, false); return new TaskHolder4(view);}
                case 5: {View view = layoutInflater.inflate(R.layout.fragment_task_notes, parent, false); return new TaskHolder5(view);}
                case 6: {View view = layoutInflater.inflate(R.layout.fragment_task_photos, parent, false); return new TaskHolder6(view);}
                case 7: {View view = layoutInflater.inflate(R.layout.fragment_task_completion, parent, false); return new TaskHolder7(view);}
                case 8: {View view = layoutInflater.inflate(R.layout.fragment_task_verification, parent, false); return new TaskHolder8(view);}
                default: return new TaskHolder0(layoutInflater.inflate(R.layout.fragment_task_title, parent, false));
            }
            // creates view and wraps it in a viewholder
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { // binds viewholder's view to a model object
            switch (getItemViewType(position)){
                case 0: TaskHolder0 h0 = (TaskHolder0)holder; h0.bindTask(); break;
                case 1: TaskHolder1 h1 = (TaskHolder1)holder; h1.bindTask(); break;
                case 2: TaskHolder2 h2 = (TaskHolder2)holder; h2.bindTask(); break;
                case 3: TaskHolder3 h3 = (TaskHolder3)holder; h3.bindTask(); break;
                case 4: TaskHolder4 h4 = (TaskHolder4)holder; h4.bindTask(); break;
                case 5: TaskHolder5 h5 = (TaskHolder5)holder; h5.bindTask(); break;
                case 6: TaskHolder6 h6 = (TaskHolder6)holder; h6.bindTask(); break;
                case 7: TaskHolder7 h7 = (TaskHolder7)holder; h7.bindTask(); break;
                case 8: TaskHolder8 h8 = (TaskHolder8)holder; h8.bindTask(); break;
            }
        }

        @Override
        public int getItemCount() {
            return mAdapterLength;
        }
    }
}



