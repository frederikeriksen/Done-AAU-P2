package com.bignerdranch.android.done.DataBaseAndLogIn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bignerdranch.android.done.AppData.RegisteredUsers;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.AppData.User;
import com.bignerdranch.android.done.ActivitiesAndFragments.UserActivity;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Created by Ico on 19-Apr-16.
 */
public class RegisterActivity extends AppCompatActivity {

    private Firebase mRef;
    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextRepeatPassword;
    private Button mButtonRegister;
    private DataBaseUsers userNew;
    private static final String TAG = "DoneActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
    }

    @Override
    public void onStart() {
        super.onStart();

        mButtonRegister = (Button) findViewById(R.id.buttonRegister);
        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mEditTextRepeatPassword = (EditText) findViewById(R.id.editTextRepeatPassword);

        RegisteredUsers.get().getUsers().clear();
        mRef = new Firebase("https://doneaau.firebaseio.com/users/");
        mRef.addChildEventListener(new ChildEventListener() {
            // Retrieve all users as they are in the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                DataBaseUsers user = snapshot.getValue(DataBaseUsers.class);

                RegisteredUsers.get().getUsers().add(user);                 // add registered user in Reg.Users list
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //there shouldn't be any strange symbols here in order to work

                String email = mEditTextEmail.getText().toString();
                String name = mEditTextName.getText().toString();
                String password = mEditTextPassword.getText().toString();
                String repeatPass = mEditTextRepeatPassword.getText().toString();
                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || repeatPass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "There can't be any empty fields!", Toast.LENGTH_LONG).show();
                }
                else if (!password.equals(repeatPass)) {
                    Toast.makeText(getApplicationContext(), "The passwords must match!", Toast.LENGTH_LONG).show();
                }
                else if(!isConnectedToInternet()) {
                    Toast.makeText(getApplicationContext(), "Please connect to a network!", Toast.LENGTH_LONG).show();
                }
                else if(!isValidEmail(email)){
                    Toast.makeText(getApplicationContext(), "The email is not valid!", Toast.LENGTH_LONG).show();
                }
                else if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "This password is too short", Toast.LENGTH_LONG).show();
                }
                else if(password.length() > 12){
                    Toast.makeText(getApplicationContext(), "This password is too long", Toast.LENGTH_LONG).show();
                }
                else {
                    boolean emailExists = false;
                    for (DataBaseUsers currUser : RegisteredUsers.get().getUsers()) {
                        if (email.equals(currUser.getEmail())) {
                            Toast.makeText(getApplicationContext(), "This email already exists", Toast.LENGTH_LONG).show();
                            emailExists = true;
                            break;
                        }
                    }
                    if (!emailExists) {
                        userNew = new DataBaseUsers();                      // Saving User data to database
                        userNew.setUserId(UUID.randomUUID().toString());
                        userNew.setEmail(email);
                        userNew.setUserName(name);
                        userNew.setPassword(password);
                        userNew.setPhoto(imageIntoString());
                        mRef.child(userNew.getUserId()).setValue(userNew);

                        User.get().getUserLists().clear();                  // Existing User data emptied
                        User.get().setUserId(userNew.getUserId());          // User initialized from database user-data
                        User.get().setUserName(name);
                        User.get().setEmail(email);
                        User.get().setPassword(password);
                        User.get().setPhoto(imageIntoString());

                        Toast.makeText(getApplicationContext(), "User registered", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public String imageIntoString () {       // makes an empty picture icon into string

        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.photoicon);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Log.d(TAG, "Active Network: " + activeNetwork + activeNetwork.toString());
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}

