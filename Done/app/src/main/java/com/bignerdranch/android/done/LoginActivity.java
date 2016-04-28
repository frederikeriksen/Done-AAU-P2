package com.bignerdranch.android.done;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.*;

public class LoginActivity extends AppCompatActivity {

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
    protected void onStart() {
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

        /*
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " users");
                DataSnapshot snap = snapshot;
                System.out.println(snap.getValue());
                ArrayList<DataBaseUsers> test = new ArrayList<DataBaseUsers>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    DataBaseUsers user = postSnapshot.getValue(DataBaseUsers.class);
                    System.out.println(user.getEmail() + " - " + user.getPassword());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });*/

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
