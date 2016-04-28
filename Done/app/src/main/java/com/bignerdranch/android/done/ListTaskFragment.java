package com.bignerdranch.android.done;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                 // from support library
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;   // from support library
import android.support.v7.widget.RecyclerView;          // from support library
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by michalisgratsias on 03/04/16.
 */
public class ListTaskFragment extends Fragment{

    private static final String TAG = "DoneActivity";
    private static final String DIALOG_TASK_TITLE = "DialogTaskTitle";
    private static final String ARG_LIST_ID = "list_id";
    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
    private List mList;
    private Task mNewTask;
    private DataBaseTasks taskNew;

    public static ListTaskFragment newInstance(String listId) {   // we use a method to create Fragment instead of using Constructor
        Bundle args = new Bundle();                         // creates Bundle for arguments
        args.putSerializable(ARG_LIST_ID, listId);          // adds task ID to Bundle
        ListTaskFragment fragment = new ListTaskFragment();         // creates Fragment instance
        fragment.setArguments(args);                        // sets Arguments
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {       // it is Public because it can be called by various activities hosting it
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String listId = (String) getArguments().getSerializable(ARG_LIST_ID);   // accessing Fragment arguments for task id
        mList = User.get().getList(listId);                    // using a get method to get List from id
    }

    private RecyclerView mTaskRecyclerView;         // RecyclerView creates only enough views to fill the screen and scrolls them
    private TaskAdapter mAdapter;                  // Adapter controls the data to be displayed by RecyclerView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);
        mTaskRecyclerView = (RecyclerView)view.findViewById(R.id.tasks_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));    // handles the
        updateUI();     // sets up the UI                     // positioning of items and defines the scrolling behaviour
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateUI();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_task:
                FragmentManager manager = getFragmentManager();
                TaskTitlePickerFragment dialog = new TaskTitlePickerFragment(); //shows dialog for new task
                dialog.setTargetFragment(ListTaskFragment.this, 11);
                dialog.show(manager, DIALOG_TASK_TITLE);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 11) {
            String title = (String) data.getSerializableExtra(TaskTitlePickerFragment.EXTRA_TITLE);

            taskNew = new DataBaseTasks();                      // saving new task data to database
            taskNew.setTaskId(UUID.randomUUID().toString());
            taskNew.setListId(mList.getListId());
            taskNew.setTaskName(title);
            Date created = new Date();
            taskNew.setCreatedDate(format.format(created));
            new Firebase("https://doneaau.firebaseio.com/tasks/").child(taskNew.getTaskId()).setValue(taskNew);
            //Map<String, Object> taskId = new HashMap<String, Object>();
            //taskId.put(taskNew.getTaskId(), true);
            //new Firebase("https://doneaau.firebaseio.com/lists/"+taskNew.getListId()+"/tasks/").updateChildren(taskId);

            mNewTask = new Task(mList.getListId());             // adding new Task to Array
            mNewTask.setTaskId(taskNew.getTaskId());
            mNewTask.setListId(taskNew.getListId());
            mNewTask.setTaskName(title);
            try {mNewTask.setCreatedDate(format.parse(taskNew.getCreatedDate()));}
            catch(ParseException e){}
            mList.addListTask(mNewTask);

            updateUI();                                         // and updating UI
        }
    }

    private void updateUI() {
        ArrayList<Task> tasks = mList.getListTasks();
        if (mAdapter == null) {
            mAdapter = new TaskAdapter(tasks);
            mTaskRecyclerView.setAdapter(mAdapter);}
        else {mAdapter.notifyDataSetChanged();}
    }

    private class TaskHolder extends RecyclerView.ViewHolder { // viewholder class
        // holds reference to the entire view passed to super(view)
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mCompletedCheckBox;
        private Button mEditButton;
        private Button mDeleteButton;
        private Task mTask;

        public TaskHolder(View itemView) {     // constructor - stashes the views
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_task_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_task_created_date_text_view);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_task_completed_check_box);
            mEditButton = (Button) itemView.findViewById(R.id.edit_task_button);
            mDeleteButton = (Button) itemView.findViewById(R.id.delete_task_button);
        }

        public void bindTask(Task task) {
            mTask = task;
            mTitleTextView.setText(mTask.getTaskName());
            mDateTextView.setText("Date Created: " + format.format(mTask.getCreatedDate()));
            mCompletedCheckBox.setChecked(mTask.isCompleted());
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = TaskActivity.newIntent(getActivity(), mTask.getTaskId(), mList.getListId());
                    startActivity(intent);                      // passes taskId, listID
                }
            });
        }

    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {  // adapter class
        // creates needed viewholders, binds them to the data
        private ArrayList<Task> mTasks;

        public TaskAdapter(ArrayList<Task> tasks) {        // constructor
            mTasks = tasks;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {              // needs new view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.tasks_item, parent, false);         // creates view
            return new TaskHolder(view);                                                   // wraps it in a viewholder
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) { // binds viewholder's view to a model object
            Task task = mTasks.get(position);
            holder.bindTask(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }
    }

}
