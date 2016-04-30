package com.bignerdranch.android.done.DataBaseAndLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bignerdranch.android.done.R;
import com.bignerdranch.android.done.UserData.User;
import com.bignerdranch.android.done.ActivitiesAndFragments.UserActivity;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.*;

/**
 * Created by Ico on 19-Apr-16.
 */
public class LoginActivity extends LogInParent {

    Firebase mRef;
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    Button mButtonLogin;
    ArrayList<DataBaseUsers> userList = new ArrayList<DataBaseUsers>();
    private static final String TAG = "DoneActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
    }

    @Override
    public void onStart() {
        super.onStart();
        mEditTextEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        mButtonLogin = (Button) findViewById(R.id.buttonLogin);

        mRef = new Firebase("https://doneaau.firebaseio.com/users/");
        mRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                DataBaseUsers user = snapshot.getValue(DataBaseUsers.class);
                //System.out.println(user.getEmail());
                userList.add(user);
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

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = mEditTextEmail.getText().toString();
                String passwordText = mEditTextPassword.getText().toString();
                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "There can't be any empty fields!", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(emailText)) {
                    Toast.makeText(getApplicationContext(), "The email is not valid!", Toast.LENGTH_LONG).show();
                } else if (passwordText.length() < 6) {
                    Toast.makeText(getApplicationContext(), "This password is too short", Toast.LENGTH_LONG).show();
                } else if (passwordText.length() > 12) {
                    Toast.makeText(getApplicationContext(), "This password is too long", Toast.LENGTH_LONG).show();
                } else {
                    boolean userExists = false;
                    for (int i = 0; i < userList.size(); i++) {
                        DataBaseUsers currUser = userList.get(i);
                        if (emailText.equals(currUser.getEmail()) && passwordText.equals(currUser.getPassword())) { //USER LOGIN SUCCESSFUL

                            User.get().getUserLists().clear();                  // Existing User data emptied
                            User.get().setUserId(currUser.getUserId());         // User initialized from database user-data
                            User.get().setUserName(currUser.getUserName());
                            User.get().setEmail(currUser.getEmail());
                            User.get().setPassword(currUser.getPassword());

                            userExists = true;
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                    if (!userExists) {
                        Toast.makeText(getApplicationContext(), "Email or password incorrect", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
