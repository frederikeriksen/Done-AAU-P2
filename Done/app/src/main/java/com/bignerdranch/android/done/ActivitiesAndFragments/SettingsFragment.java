package com.bignerdranch.android.done.ActivitiesAndFragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.done.AppData.List;
import com.bignerdranch.android.done.AppData.Task;
import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.DataBaseAndLogIn.LogoPageActivity;
import com.bignerdranch.android.done.PopUps.DeleteUserPickerFragment;
import com.bignerdranch.android.done.PopUps.EditUserEmailPickerFragment;
import com.bignerdranch.android.done.PopUps.EditUserNamePickerFragment;
import com.bignerdranch.android.done.PopUps.EditUserPasswordPickerFragment;
import com.bignerdranch.android.done.R;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by michalisgratsias on 08/05/16.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "DoneActivity";
    private static final String DIALOG_USER_NAME = "EditUserName";
    private static final String DIALOG_USER_EMAIL = "EditUserEmail";
    private static final String DIALOG_USER_PASSWORD = "EditUserPassword";
    private static final String DIALOG_DELETE_USER = "DeleteUser";
    private Firebase mDataBaseUsersRef = new Firebase("https://doneaau.firebaseio.com/users/");
    private Firebase mDataBaseListRef = new Firebase("https://doneaau.firebaseio.com/lists/");
    private Firebase mDataBaseTaskRef = new Firebase("https://doneaau.firebaseio.com/tasks/");
    private Button mUserName;
    private TextView mUserNameTextView;
    private Button mUserEmail;
    private TextView mUserEmailTextView;
    private Button mUserPassword;
    private TextView mUserPasswordTextView;
    private Button mUserPhoto;
    public static Bitmap mImageBitmap;

    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private RecyclerView mUserRecyclerView;         // RecyclerView creates only enough views to fill the screen and scrolls them
    private TaskAdapter mAdapter;                  // Adapter controls the data to be displayed by RecyclerView

    @Override
    public void onCreate(Bundle savedInstanceState) {       // it is Public because it can be called by various activities hosting it
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mUserRecyclerView = (RecyclerView)view.findViewById(R.id.single_profile_item_recycler_view);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));    // handles the
        updateUI();     // sets up the UI                     // positioning of items and defines the scrolling behaviour
        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_user:
                DeleteUserPickerFragment dialog = new DeleteUserPickerFragment(); //shows dialog for deleting user
                FragmentManager manager = getFragmentManager();
                dialog.setTargetFragment(SettingsFragment.this, 17);
                dialog.show(manager, DIALOG_DELETE_USER);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {    // Actions happening after a pop-up ends
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 13: {     // CHANGING USER NAME

                String userName = (String) data.getSerializableExtra(EditUserNamePickerFragment.EXTRA_USER_NAME);

                mDataBaseUsersRef.child(User.get().getUserId()).child("userName").setValue(userName); // updating DB user name
                // updating Array Task Name is already done
                updateUI();                                         // and updating UI
                ((SettingsActivity)getActivity()).getSupportActionBar().setTitle(User.get().getUserName() + " - Profile Settings");
                break;
            }
            case 14: {     // CHANGING USER EMAIL

                String userEmail = (String) data.getSerializableExtra(EditUserEmailPickerFragment.EXTRA_USER_EMAIL);

                mDataBaseUsersRef.child(User.get().getUserId()).child("email").setValue(userEmail); // updating DB user email
                // updating Array Task Name is already done
                updateUI();                                         // and updating UI
                break;
            }
            case 15: {     // CHANGING USER PASSWORD

                String userPassword = (String) data.getSerializableExtra(EditUserPasswordPickerFragment.EXTRA_USER_PASSWORD);

                mDataBaseUsersRef.child(User.get().getUserId()).child("password").setValue(userPassword); // updating DB user password
                // updating Array Task Name is already done
                updateUI();                                         // and updating UI
                break;
            }
            case 16:{      // ADDING USER PHOTOS
                try {
                    String imgDecodableString = "";
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getContext().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    Toast.makeText(getContext(), "Image picked", Toast.LENGTH_LONG).show();

                    //imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                    String photoStr = bitmapToString(BitmapFactory.decodeFile(imgDecodableString));
                    User.get().setPhoto(photoStr);
                    Firebase mDataBasePhotoRef = new Firebase("https://doneaau.firebaseio.com/users/"+ User.get().getUserId() +"/photo/");
                    mDataBasePhotoRef.setValue(photoStr);           // adding photo to Database for that Task

                    ImageView imgView = (ImageView) getView().findViewById(R.id.photo_imageView);
                    byte[] imageAsBytes = Base64.decode(photoStr.getBytes(), Base64.DEFAULT);
                    Bitmap photo = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imgView.setImageBitmap(photo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 17: {      // DELETING USER

                String title = (String) data.getSerializableExtra(DeleteUserPickerFragment.EXTRA_NAME);

                mDataBaseUsersRef.child(User.get().getUserId()).setValue(null); // deleting user from database

                for (List l: User.get().getUserLists()) {                       // deleting DB lists of user
                    Log.d(TAG, " " + l.getListName());
                    mDataBaseListRef.child(l.getListId()).setValue(null);
                    for (Task t : User.get().getList(l.getListId()).getListTasks()) {  // deleting DB tasks of user
                        Log.d(TAG, " " + t.getTaskName());
                        mDataBaseTaskRef.child(t.getTaskId()).setValue(null);
                    }
                }

                Toast.makeText(getActivity(), "User successfully deleted!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), LogoPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);                                          // back to Logo screen

                break;
            }
        }
    }
    private String bitmapToString(Bitmap photo){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new TaskAdapter(4);              // passing 9 fragments to the Recycle-View Adapter
            mUserRecyclerView.setAdapter(mAdapter);}
        else {mAdapter.notifyDataSetChanged();}
    }

    private class UserHolder0 extends RecyclerView.ViewHolder { // EDIT USER NAME
        // viewholder class holds reference to the entire view passed
        public UserHolder0(View itemView) {     // constructor - stashes the views
            super(itemView);
            mUserName = (Button) itemView.findViewById(R.id.user_name);
            mUserNameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
        }
        public void bindTask() {
            mUserNameTextView.setText(User.get().getUserName());
            mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        FragmentManager manager = getFragmentManager();
                        EditUserNamePickerFragment dialog = new EditUserNamePickerFragment(); //edits user name
                        dialog.setTargetFragment(SettingsFragment.this, 13);
                        dialog.show(manager, DIALOG_USER_NAME);
                }
            });
            mUserName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Edit User Name", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class UserHolder1 extends RecyclerView.ViewHolder { // EDIT USER EMAIL
        // viewholder class holds reference to the entire view passed
        public UserHolder1(View itemView) {     // constructor - stashes the views
            super(itemView);
            mUserEmail = (Button) itemView.findViewById(R.id.email);
            mUserEmailTextView = (TextView) itemView.findViewById(R.id.email_text_view);
        }

        public void bindTask() {
            mUserEmailTextView.setText(User.get().getEmail());
            mUserEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    EditUserEmailPickerFragment dialog = new EditUserEmailPickerFragment(); //edits user email
                    dialog.setTargetFragment(SettingsFragment.this, 14);
                    dialog.show(manager, DIALOG_USER_EMAIL);
                }
            });
            mUserEmail.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Edit User Email", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class UserHolder2 extends RecyclerView.ViewHolder { // EDIT USER PASSWORD
        // viewholder class holds reference to the entire view passed
        public UserHolder2(View itemView) {     // constructor - stashes the views
            super(itemView);
            mUserPassword = (Button) itemView.findViewById(R.id.password);
            mUserPasswordTextView = (TextView) itemView.findViewById(R.id.password_text_view);
        }

        public void bindTask() {
            mUserPasswordTextView.setText(User.get().getPassword());
            mUserPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    EditUserPasswordPickerFragment dialog = new EditUserPasswordPickerFragment(); //edits user password
                    dialog.setTargetFragment(SettingsFragment.this, 15);
                    dialog.show(manager, DIALOG_USER_PASSWORD);
                }
            });
            mUserPassword.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "Edit User Password", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private class UserHolder3 extends RecyclerView.ViewHolder {  // ADDING PHOTOS
        // viewholder class holds reference to the entire view passed
        public UserHolder3(View itemView) {     // constructor - stashes the views
            super(itemView);
            mUserPhoto = (Button) itemView.findViewById(R.id.photo);
        }
        public void bindTask() {
            mUserPhoto.setEnabled(true);
            mUserPhoto.setOnClickListener(new View.OnClickListener() {
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
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, 16);
                        }
                    }
                }
            });
            mUserPhoto.setOnLongClickListener(new View.OnLongClickListener() {
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
                case 0: {View view = layoutInflater.inflate(R.layout.fragment_user_name, parent, false); return new UserHolder0(view);}
                case 1: {View view = layoutInflater.inflate(R.layout.fragment_user_email, parent, false); return new UserHolder1(view);}
                case 2: {View view = layoutInflater.inflate(R.layout.fragment_user_password, parent, false); return new UserHolder2(view);}
                case 3: {View view = layoutInflater.inflate(R.layout.fragment_user_photo, parent, false); return new UserHolder3(view);}
                default: return new UserHolder0(layoutInflater.inflate(R.layout.fragment_user_name, parent, false));
            }
            // creates view and wraps it in a viewholder
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { // binds viewholder's view to a model object
            switch (getItemViewType(position)){
                case 0: UserHolder0 h0 = (UserHolder0) holder; h0.bindTask(); break;
                case 1: UserHolder1 h1 = (UserHolder1) holder; h1.bindTask(); break;
                case 2: UserHolder2 h2 = (UserHolder2) holder; h2.bindTask(); break;
                case 3: UserHolder3 h3 = (UserHolder3) holder; h3.bindTask(); break;
            }
        }

        @Override
        public int getItemCount() {
            return mAdapterLength;
        }
    }
}
