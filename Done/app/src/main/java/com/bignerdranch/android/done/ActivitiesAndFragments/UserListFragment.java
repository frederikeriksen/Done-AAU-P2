package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;                     // from support library
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;       // from support library
import android.support.v7.widget.RecyclerView;              // from support library
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bignerdranch.android.done.PopUps.DeleteListPickerFragment;
import com.bignerdranch.android.done.PopUps.EditListTitlePickerFragment;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.PopUps.ShareListPickerFragment;
import com.bignerdranch.android.done.UserData.User;
import com.bignerdranch.android.done.PopUps.ListTitlePickerFragment;
import com.bignerdranch.android.done.UserData.List;
import com.bignerdranch.android.done.DataBaseAndLogIn.DataBaseLists;
import com.firebase.client.Firebase;
import java.util.ArrayList;
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
    private Firebase mDataBaseListRef = new Firebase("https://doneaau.firebaseio.com/lists/");

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
                ListTitlePickerFragment dialog = new ListTitlePickerFragment(); //shows dialog for new list
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

                String title = (String) data.getSerializableExtra(ListTitlePickerFragment.EXTRA_TITLE);

                listDBNew = new DataBaseLists();                      // saving new list data to database
                listDBNew.setListId(UUID.randomUUID().toString());
                listDBNew.setListName(title);
                listDBNew.setCreatorId(User.get().getUserId());
                mDataBaseListRef.child(listDBNew.getListId()).setValue(listDBNew);

                mNewList = new List(User.get().getUserId());        // adding new List to Array
                mNewList.setListId(listDBNew.getListId());
                mNewList.setListName(title);
                mNewList.setCreatorId(User.get().getUserId());
                User.get().addUserList(mNewList);

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
            case 7: {       // EDITING LIST

            }
            case 8: {       // DELETING LIST

                String listId = (String) data.getSerializableExtra(DeleteListPickerFragment.EXTRA_ID);

                mDataBaseListRef.child(listId).setValue(null);      // deleting DB list

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
            mTaskButton = (Button) itemView.findViewById(R.id.task_button);
            mEditButton = (Button) itemView.findViewById(R.id.edit_list_button);
            mShareButton = (Button) itemView.findViewById(R.id.share_button);
            mDeleteButton = (Button) itemView.findViewById(R.id.delete_list_button);
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
                        Toast.makeText(getContext(), "The list title can be edited only by its creator", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    ShareListPickerFragment dialog = new ShareListPickerFragment(); //shares list
                    dialog.setTargetFragment(UserListFragment.this, 7);
                    dialog.show(manager, DIALOG_SHARE_LIST);
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
                        Toast.makeText(getContext(), "The list can be deleted only by its creator", Toast.LENGTH_SHORT).show();
                    }
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
