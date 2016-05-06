package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import com.bignerdranch.android.done.AppData.RegisteredUsers;
import com.bignerdranch.android.done.DataBaseAndLogIn.DataBaseUsers;
import com.bignerdranch.android.done.PopUps.DeleteListPickerFragment;
import com.bignerdranch.android.done.PopUps.EditListTitlePickerFragment;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.PopUps.ShareListPickerFragment;
import com.bignerdranch.android.done.AppData.Task;
import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.PopUps.NewListTitlePickerFragment;
import com.bignerdranch.android.done.AppData.List;
import com.bignerdranch.android.done.DataBaseAndLogIn.DataBaseLists;
import com.firebase.client.Firebase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by michalisgratsias on 03/04/16.
 */
public class UserListFragment extends Fragment {

    private static final String TAG = "DoneActivity";
    private static final String DIALOG_LIST_TITLE = "DialogListTitle";
    private static final String DIALOG_EDIT_LIST_TITLE = "DialogEditListTitle";
    private static final String DIALOG_SHARE_LIST = "DialogShareList";
    private static final String DIALOG_DELETE_LIST = "DialogDeleteList";
    private RecyclerView mListRecyclerView;        // RecyclerView creates only enough views to fill the screen and scrolls them
    private ListAdapter mAdapter;                  // Adapter controls the data to be displayed by RecyclerView
    private List mNewList;
    private DataBaseLists listDBNew;
    private Firebase mDataBaseListUserRef;
    private Firebase mDataBaseListRef = new Firebase("https://doneaau.firebaseio.com/lists/");
    private Firebase mDataBaseTaskRef = new Firebase("https://doneaau.firebaseio.com/tasks/");

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
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));    // handles the positioning of items and defines the scrolling behaviour
        updateUI();     // sets up the UI
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
                NewListTitlePickerFragment dialog = new NewListTitlePickerFragment(); //shows dialog for new list
                FragmentManager manager = getFragmentManager();
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
        switch (requestCode) {
            case 10: {      // ADDING NEW LIST

                String title = (String) data.getSerializableExtra(NewListTitlePickerFragment.EXTRA_TITLE);

                if (title == null) break;                             // does not create empty list

                listDBNew = new DataBaseLists();                    // saving new list data to database
                listDBNew.setListId(UUID.randomUUID().toString());
                listDBNew.setListName(title);
                listDBNew.setCreatorId(User.get().getUserId());
                mDataBaseListRef.child(listDBNew.getListId()).setValue(listDBNew);

                mNewList = new List(User.get().getUserId());        // adding new List to Array
                mNewList.setListId(listDBNew.getListId());
                mNewList.setListName(title);
                mNewList.setCreatorId(User.get().getUserId());
                User.get().addUserList(mNewList);                   // updating AppBar, below
                ((UserActivity)getActivity()).getSupportActionBar().setTitle(User.get().getUserName() + " - My " + User.get().getUserLists().size() + " To-Do Lists");
                updateUI();                                         // and updating UI
                break;
            }
            case 6: {       // CHANGING LIST NAME

                String listId = (String) data.getSerializableExtra(EditListTitlePickerFragment.EXTRA_ID);

                mDataBaseListRef.child(listId).child("listName").setValue(User.get().getList(listId).getListName()); // updating DB list name
                                                                    // updating Array List Name is already done
                updateUI();                                         // and updating UI
                break;
            }
            case 7: {       // SHARING LIST

                String email = (String) data.getSerializableExtra(ShareListPickerFragment.EXTRA_EMAIL);
                String listId = (String) data.getSerializableExtra(EditListTitlePickerFragment.EXTRA_ID);

                Boolean noUser = true;
                for (DataBaseUsers u: RegisteredUsers.get().getUsers()) {
                    if (u.getEmail().equals(email)) {
                        mDataBaseListUserRef = new Firebase("https://doneaau.firebaseio.com/lists/"+ listId +"/shared_users/");
                        Map<String, Object> listUser = new HashMap<String, Object>();
                        listUser.put(u.getUserId(), true);
                        mDataBaseListUserRef.updateChildren(listUser);          // adding user to Database for that List

                        User.get().getList(listId).addListUser(u.getUserId());  // adding user to Array of List Users for that List
                        updateUI();                                             // and updating UI
                        noUser = false;
                        break;
                    }
                }
                if (noUser) Toast.makeText(getContext(), "No Registered user with that Email address exists in the Database", Toast.LENGTH_SHORT).show();
                break;
            }
            case 8: {       // DELETING LIST AND ITS TASKS

                String listId = (String) data.getSerializableExtra(DeleteListPickerFragment.EXTRA_ID);

                mDataBaseListRef.child(listId).setValue(null);      // deleting DB list and its tasks
                for (Task t: User.get().getList(listId).getListTasks()) {
                    Log.d(TAG, " "+ t.getTaskName());
                    mDataBaseTaskRef.child(t.getTaskId()).setValue(null);
                }

                User.get().getUserLists().remove(User.get().getList(listId)); // deleting list from User-List Array

                updateUI();                                         // and updating UI
                break;
            }
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
        private TextView mShareTextView;
        private TextView mCreatorTextView;
        private Button mEditButton;
        private Button mShareButton;
        private Button mDeleteButton;
        private Button mTaskButton;
        private List mList;

        public ListHolder(View itemView) {     // constructor - stashes the views
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_list_title_text_view);
            mShareTextView = (TextView) itemView.findViewById(R.id.share_list_view_names);
            mCreatorTextView = (TextView) itemView.findViewById(R.id.creator_list_view_name);
            mTaskButton = (Button) itemView.findViewById(R.id.task_button);
            mEditButton = (Button) itemView.findViewById(R.id.edit_list_button);
            mShareButton = (Button) itemView.findViewById(R.id.share_button);
            mDeleteButton = (Button) itemView.findViewById(R.id.delete_list_button);
        }

        public void bindList(List list) {                   // list data entered in fragment viewholder
            mList = list;
            mTitleTextView.setText(mList.getListName());
            mCreatorTextView.setText(RegisteredUsers.get().getUser(mList.getCreatorId()).getUserName());
            String shares = "";
            for (String n: mList.getListUsers()) {
                shares = shares + RegisteredUsers.get().getUser(n).getUserName()+" + ";
                mShareTextView.setText(shares.substring(0,shares.length()-2));
            }
            if (mList.getListUsers().size() == 0) mShareTextView.setText("None");
            mTaskButton.setTextColor(Color.WHITE);
            mTaskButton.setText(""+mList.getListTasks().size());            // updates task count
            mTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ListActivity.newIntent(getActivity(), mList.getListId()); //passes listId
                    startActivity(intent);
                }
            });
            mTaskButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "View Tasks", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.get().getUserId().equals(mList.getCreatorId())) {
                        FragmentManager manager = getFragmentManager();
                        EditListTitlePickerFragment dialog = EditListTitlePickerFragment.newInstance(mList.getListId()); //edits list title
                        dialog.setTargetFragment(UserListFragment.this, 6);
                        dialog.show(manager, DIALOG_EDIT_LIST_TITLE);
                    }
                    else {
                        Toast.makeText(getContext(), "This list title can be edited only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mEditButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Edit List Name", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.get().getUserId().equals(mList.getCreatorId())) {
                        FragmentManager manager = getFragmentManager();
                        ShareListPickerFragment dialog = ShareListPickerFragment.newInstance(mList.getListId()); //shares list
                        dialog.setTargetFragment(UserListFragment.this, 7);
                        dialog.show(manager, DIALOG_SHARE_LIST);
                    }
                    else {
                        Toast.makeText(getContext(), "This list can be shared only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mShareButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Share list with other users", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.get().getUserId().equals(mList.getCreatorId())) {
                        FragmentManager manager = getFragmentManager();
                        DeleteListPickerFragment dialog = DeleteListPickerFragment.newInstance(mList.getListId()); //deletes list
                        dialog.setTargetFragment(UserListFragment.this, 8);
                        dialog.show(manager, DIALOG_DELETE_LIST);
                    }
                    else {
                        Toast.makeText(getContext(), "This list can be deleted only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mDeleteButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Delete list", Toast.LENGTH_SHORT).show();
                    return true;
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
            ((UserActivity)getActivity()).getSupportActionBar().setTitle(User.get().getUserName() + " - My " + User.get().getUserLists().size() + " To-Do Lists");
            holder.bindList(list);
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }
    }
}
