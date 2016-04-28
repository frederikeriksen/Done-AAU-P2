package com.bignerdranch.android.done;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                     // from support library
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;       // from support library
import android.support.v7.widget.RecyclerView;              // from support library
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by michalisgratsias on 03/04/16.
 */
public class UserListFragment extends Fragment {

    private static final String TAG = "DoneActivity";
    private static final String DIALOG_LIST_TITLE = "DialogListTitle";
    private RecyclerView mListRecyclerView;        // RecyclerView creates only enough views to fill the screen and scrolls them
    private ListAdapter mAdapter;                  // Adapter controls the data to be displayed by RecyclerView
    private List mNewList;
    private DataBaseLists listNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {   // it is Public because it can be called by various activities hosting it
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mListRecyclerView = (RecyclerView)view.findViewById(R.id.lists_recycler_view);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));    // handles the
        updateUI();     // sets up the UI                     // positioning of items and defines the scrolling behaviour
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();                           // after the Picker Fragment pop-up is gone, updates UI
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_user_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_list:
                FragmentManager manager = getFragmentManager();
                ListTitlePickerFragment dialog = new ListTitlePickerFragment(); //shows dialog for new list
                dialog.setTargetFragment(UserListFragment.this, 10);
                dialog.show(manager, DIALOG_LIST_TITLE);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 10) {
            String title = (String) data.getSerializableExtra(ListTitlePickerFragment.EXTRA_TITLE);

            listNew = new DataBaseLists();                      // saving new list data to database
            listNew.setListId(UUID.randomUUID().toString());
            listNew.setListName(title);
            listNew.setCreatorId(User.get().getUserId());
            new Firebase("https://doneaau.firebaseio.com/lists/").child(listNew.getListId()).setValue(listNew);

            mNewList = new List(User.get().getUserId());        // adding new List to Array
            mNewList.setListId(listNew.getListId());
            mNewList.setListName(title);
            mNewList.setCreatorId(User.get().getUserId());
            User.get().addUserList(mNewList);

            updateUI();                                         // and updating UI
        }
    }

    private void updateUI() {
        ArrayList<List> lists = User.get().getUserLists();    // gets all Lists of the User
        if (mAdapter == null) {
            mAdapter = new ListAdapter(lists);          // gives lists to adapter
            mListRecyclerView.setAdapter(mAdapter);}    // connects to recycler view
        else {mAdapter.notifyDataSetChanged();}         // if existing, updates data changes
    }

    private class ListHolder extends RecyclerView.ViewHolder { // viewholder class
        // holds reference to the entire view passed to super(view)
        private TextView mTitleTextView;
        private TextView mTaskTextView;
        private Button mEditButton;
        private Button mShareButton;
        private Button mDeleteButton;
        private Button mTaskButton;
        private List mList;

        public ListHolder(View itemView) {     // constructor - stashes the views
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_list_title_text_view);
            mTaskTextView = (TextView) itemView.findViewById(R.id.list_item_task_text_view);
            mEditButton = (Button) itemView.findViewById(R.id.edit_list_button);
            mShareButton = (Button) itemView.findViewById(R.id.share_button);
            mDeleteButton = (Button) itemView.findViewById(R.id.delete_list_button);
            mTaskButton = (Button) itemView.findViewById(R.id.task_button);
        }

        public void bindList(List list) {                   // list data entered in fragment viewholder
            mList = list;
            mTitleTextView.setText(mList.getListName());
            mTaskButton.setText(""+mList.getListTasks().size());            // updates task count
            mTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ListActivity.newIntent(getActivity(), mList.getListId()); //passes listId
                    startActivity(intent);
                }
            });
        }

    }

    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {  // adapter class
        // creates needed viewholders, binds them to the data
        private ArrayList<List> mLists;

        public ListAdapter(ArrayList<List> lists) {        // constructor
            mLists = lists;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {              // needs new view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.lists_item, parent, false);         // creates view
            return new ListHolder(view);                                                   // wraps it in a viewholder
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) { // binds viewholder's view to a model object
            List list = mLists.get(position);
            holder.bindList(list);
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }
    }


}
