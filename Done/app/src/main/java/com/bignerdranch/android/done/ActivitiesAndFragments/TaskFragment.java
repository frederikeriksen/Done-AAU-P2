package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;                 // from support library
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.done.PopUps.AssigningTaskPickerFragment;
import com.bignerdranch.android.done.PopUps.DueDatePickerFragment;
import com.bignerdranch.android.done.PopUps.EditTaskTitlePickerFragment;
import com.bignerdranch.android.done.PopUps.HidingTaskPickerFragment;
import com.bignerdranch.android.done.PopUps.NotesPickerFragment;
import com.bignerdranch.android.done.PopUps.ReminderDatePickerFragment;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.AppData.List;
import com.bignerdranch.android.done.AppData.Task;
import com.bignerdranch.android.done.AppData.User;
import com.firebase.client.Firebase;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private static final String DIALOG_EDIT_TASK_TITLE = "EditTaskTitle";
    private static final String DIALOG_ASSIGN_TASK = "AssignTask";
    private static final String DIALOG_HIDE_TASK = "HideTask";
    private Firebase mDataBaseTaskRef = new Firebase("https://doneaau.firebaseio.com/tasks/");
    private SimpleDateFormat format2 = new SimpleDateFormat("EEEE MMM dd, yyyy");
    private Firebase mDataBaseNoteRef;
    private Task mTask;
    private List mList;
    private Button mTaskTitle;
    private TextView mTaskTitleTextView;
    private Button mAssignedTo;
    private TextView mAssignedToTextView;
    private Button mHiddenFrom;
    private TextView mHiddenFromTextView;
    private Button mDueDateButton;
    private TextView mDueDateTextView;
    private Button mReminderDateButton;
    private TextView mReminderDateTextView;
    private Button mAddNote;
    private TextView mNotesText;
    private Button mAddPhoto;
    private ImageView mPhoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private CheckBox mCompletedCheckBox;
    private CheckBox mVerifiedCheckBox;
    private Date date;

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
        mList = User.get().getList(listId);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {    // Actions happening after a pop-up ends
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 0: {      // ADDING DUE DATE

                Date date = (Date) data.getSerializableExtra(DueDatePickerFragment.EXTRA_DATE);

                mDataBaseTaskRef.child(mTask.getTaskId()).child("dueDate").setValue(format2.format(date));

                mTask.setDueDate(date);

                updateDueDate();
                break;
            }
            case 1: {      // ADDING REMINDER DATE

                Date date = (Date) data.getSerializableExtra(ReminderDatePickerFragment.EXTRA_DATE);

                mDataBaseTaskRef.child(mTask.getTaskId()).child("reminderDate").setValue(format2.format(date));

                mTask.setReminderDate(date);

                updateReminderDate();
                break;
            }
            case 2: {      // ADDING NOTES
                String note = (String) data.getSerializableExtra(NotesPickerFragment.EXTRA_TITLE);

                mDataBaseNoteRef = new Firebase("https://doneaau.firebaseio.com/tasks/"+ mTask.getTaskId() +"/notes/");
                Map<String, Object> dbnote = new HashMap<String, Object>();
                dbnote.put("user", User.get().getUserName());
                dbnote.put("note", note);
                mDataBaseNoteRef.push().setValue(dbnote);           // adding note to Database for that Task

                mTask.addNote(User.get().getUserName()+": "+ note); // adding note to Notes array

                String notes = "";
                for (String n: mTask.getNotes()) {notes += "\n" +n;}
                mNotesText.setText(notes);                          // updating notes view (UI)
                break;
            }
            case 3:{      // ADDING PHOTOS
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    // mImageView.setImageBitmap(mImageBitmap);
                    mTask.setPhoto(mImageBitmap);
                    ImageView imgShow = (ImageView) getView().findViewById(R.id.show_photo);
                    imgShow.setImageBitmap(mTask.getPhoto());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 4: {      // ASSIGNING TASKS
                break;
            }
            case 5: {      // HIDING TASKS
                break;
            }
            case 12: {     // CHANGING TASK NAME

                String taskTitle = (String) data.getSerializableExtra(EditTaskTitlePickerFragment.EXTRA_TASK_TITLE);

                mDataBaseTaskRef.child(mTask.getTaskId()).child("taskName").setValue(taskTitle); // updating DB task name
                                                                    // updating Array Task Name is already done
                updateUI();                                         // and updating UI
                ((TaskActivity)getActivity()).getSupportActionBar().setTitle("Task: "+ mTask.getTaskName());
                break;
            }
        }
    }

    private void updateTaskTitle() {mTaskTitleTextView.setText(mTask.getTaskName());}

    private void updateAssignees() {mAssignedToTextView.setText("");}

    private void updateViewers() {mHiddenFromTextView.setText("");}

    private void updateDueDate() {
        if (mTask.getDueDate() == null) mDueDateTextView.setText("Not Set");
        else mDueDateTextView.setText(format2.format(mTask.getDueDate()));
    }

    private void updateReminderDate() {
        if (mTask.getReminderDate() == null) mReminderDateTextView.setText("Not Set");
        else mReminderDateTextView.setText(format2.format(mTask.getReminderDate()));
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new TaskAdapter(9);              // passing 9 fragments to the Recycle-View Adapter
            mTaskRecyclerView.setAdapter(mAdapter);}
        else {mAdapter.notifyDataSetChanged();}
    }

    private class TaskHolder0 extends RecyclerView.ViewHolder { // EDIT TASK TITLE
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder0(View itemView) {     // constructor - stashes the views
            super(itemView);
            mTaskTitle = (Button) itemView.findViewById(R.id.task_title);
            mTaskTitleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
        }
        public void bindTask() {
            updateTaskTitle();
            mTaskTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.get().getUserId().equals(mList.getCreatorId())) {
                        FragmentManager manager = getFragmentManager();
                        EditTaskTitlePickerFragment dialog = EditTaskTitlePickerFragment.newInstance(mTask.getTaskId(), mList.getListId()); //edits task title
                        dialog.setTargetFragment(TaskFragment.this, 12);
                        dialog.show(manager, DIALOG_EDIT_TASK_TITLE);
                    }
                    else {
                        Toast.makeText(getContext(), "The list title can be edited only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mTaskTitle.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Edit Task Name", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class TaskHolder1 extends RecyclerView.ViewHolder {  // ASSIGNING TASKS
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder1(View itemView) {     // constructor - stashes the views
            super(itemView);
            mAssignedTo = (Button) itemView.findViewById(R.id.assigned_to);
            mAssignedToTextView = (TextView) itemView.findViewById(R.id.assigned_to_text_view);
        }
        public void bindTask() {
            updateAssignees();
            mAssignedToTextView.setText("None");
            mAssignedTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.get().getUserId().equals(mList.getCreatorId())) {
                        FragmentManager manager = getFragmentManager();
                        AssigningTaskPickerFragment dialog = new AssigningTaskPickerFragment();//assigns the task
                        dialog.setTargetFragment(TaskFragment.this, 4);
                        dialog.show(manager, DIALOG_ASSIGN_TASK);
                    }
                    else {
                        Toast.makeText(getContext(), "The task can be assigned only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mAssignedTo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Assign Task", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class TaskHolder2 extends RecyclerView.ViewHolder {  // HIDING TASKS
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder2(View itemView) {     // constructor - stashes the views
            super(itemView);
            mHiddenFrom = (Button) itemView.findViewById(R.id.hidden_from);
            mHiddenFromTextView = (TextView) itemView.findViewById(R.id.hidden_from_text_view);
        }
        public void bindTask() {
            updateViewers();
            mHiddenFromTextView.setText("None");
            mHiddenFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.get().getUserId().equals(mList.getCreatorId())) {
                        FragmentManager manager = getFragmentManager();
                        HidingTaskPickerFragment dialog = new HidingTaskPickerFragment();//hides the task
                        dialog.setTargetFragment(TaskFragment.this, 5);
                        dialog.show(manager, DIALOG_HIDE_TASK);
                    }
                    else {
                        Toast.makeText(getContext(), "The task can be assigned only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mHiddenFrom.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Hide Task", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class TaskHolder3 extends RecyclerView.ViewHolder {  // ADDING DUE DATE
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder3(View itemView) {     // constructor - stashes the views
            super(itemView);
            mDueDateButton = (Button) itemView.findViewById(R.id.due_date);
            mDueDateTextView = (TextView) itemView.findViewById(R.id.task_due_date_text_view);
        }
        public void bindTask() {
            updateDueDate();
            mDueDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    if (mTask.getDueDate() == null) date = mTask.getCreatedDate();
                    else date = mTask.getDueDate();
                    DueDatePickerFragment dialog = DueDatePickerFragment.newInstance(date); //shows due date
                    dialog.setTargetFragment(TaskFragment.this, 0);
                    dialog.show(manager, DIALOG_DATE1);
                }
            });
            mDueDateButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Set a Due Date for Task", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class TaskHolder4 extends RecyclerView.ViewHolder {  // ADDING REMINDER DATE
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder4(View itemView) {     // constructor - stashes the views
            super(itemView);
            mReminderDateButton = (Button) itemView.findViewById(R.id.reminder_date);
            mReminderDateTextView = (TextView) itemView.findViewById(R.id.task_reminder_date_text_view);
        }
        public void bindTask() {
            updateReminderDate();
            mReminderDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    if (mTask.getReminderDate() == null) date = mTask.getCreatedDate();
                    else date = mTask.getReminderDate();
                    ReminderDatePickerFragment dialog = ReminderDatePickerFragment.newInstance(date); //shows reminder date
                    dialog.setTargetFragment(TaskFragment.this, 1);
                    dialog.show(manager, DIALOG_DATE2);
                }
            });
            mReminderDateButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Set a Reminder Date for Task", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class TaskHolder5 extends RecyclerView.ViewHolder {  // ADDING NOTES
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder5(View itemView) {     // constructor - stashes the views
            super(itemView);
            mAddNote = (Button) itemView.findViewById(R.id.add_note);
            mNotesText = (TextView) itemView.findViewById(R.id.notes);
        }
        public void bindTask() {
            String notes = "";
            for (String n: mTask.getNotes()) {notes += "\n" +n;}
            mNotesText.setText(notes);                          // updating notes view (UI)
            mAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    NotesPickerFragment dialog = new NotesPickerFragment(); //shows reminder date
                    dialog.setTargetFragment(TaskFragment.this, 2);
                    dialog.show(manager, DIALOG_NOTES);
                }
            });
            mAddNote.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Add Notes", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class TaskHolder6 extends RecyclerView.ViewHolder {  // ADDING PHOTOS
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder6(View itemView) {     // constructor - stashes the views
            super(itemView);
            mAddPhoto = (Button) itemView.findViewById(R.id.add_photo);
        }
        public void bindTask() {
            mAddPhoto.setEnabled(true);
            mAddPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.i(TAG, "IOException");
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(cameraIntent, 3);
                        }
                    }
                }
            });
            mAddPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Add Photos", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
    //this is for the photo
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private class TaskHolder7 extends RecyclerView.ViewHolder {  // MARK COMPLETED
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder7(View itemView) {     // constructor - stashes the views
            super(itemView);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.task_completed);
        }
        public void bindTask() {
            mCompletedCheckBox.setChecked(mTask.isCompleted());
            mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mDataBaseTaskRef.child(mTask.getTaskId()).child("completed").setValue(isChecked);    // updates database
                    mTask.setCompleted(isChecked);                              // updates Task Array
                    Log.d(TAG, " Task completed: " + mTask.isCompleted());
                }
            });
        }
    }

    private class TaskHolder8 extends RecyclerView.ViewHolder {  // MARK VERIFIED
                                                // viewholder class holds reference to the entire view passed
        public TaskHolder8(View itemView) {     // constructor - stashes the views
            super(itemView);
            mVerifiedCheckBox = (CheckBox) itemView.findViewById(R.id.task_verified);
        }
        public void bindTask() {
            mVerifiedCheckBox.setChecked(mTask.isCompleted());
            mVerifiedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mDataBaseTaskRef.child(mTask.getTaskId()).child("verified").setValue(isChecked);    // updates database
                    mTask.setVerified(isChecked);                              // updates Task Array
                    Log.d(TAG, " Task verified: " + mTask.isVerified());
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



