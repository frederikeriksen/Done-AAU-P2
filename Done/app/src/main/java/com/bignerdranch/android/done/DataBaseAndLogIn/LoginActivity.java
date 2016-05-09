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
import java.util.*;

/**
 * Created by Ico on 19-Apr-16.
 */
public class LoginActivity extends AppCompatActivity {

    Firebase mRef;
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    Button mButtonLogin;
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

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = mEditTextEmail.getText().toString();
                String passwordText = mEditTextPassword.getText().toString();

                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "There can't be any empty fields!", Toast.LENGTH_LONG).show();
                } else if(!isConnectedToInternet()){
                    Toast.makeText(getApplicationContext(),"Please connect to a network!",Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(emailText)) {
                    Toast.makeText(getApplicationContext(), "The email is not valid!", Toast.LENGTH_LONG).show();
                } else if (passwordText.length() < 6) {
                    Toast.makeText(getApplicationContext(), "This password is too short", Toast.LENGTH_LONG).show();
                } else if (passwordText.length() > 12) {
                    Toast.makeText(getApplicationContext(), "This password is too long", Toast.LENGTH_LONG).show();
                }
                else {
                    boolean userExists = false;
                    for (int i = 0; i < RegisteredUsers.get().getUsers().size(); i++) {
                        DataBaseUsers currUser = RegisteredUsers.get().getUsers().get(i);
                        if (emailText.equals(currUser.getEmail()) && passwordText.equals(currUser.getPassword())) {
                                                                                // USER LOGIN SUCCESSFUL
                            User.get().getUserLists().clear();                  // Existing User data emptied
                            User.get().setUserId(currUser.getUserId());         // User initialized from database user-data
                            User.get().setUserName(currUser.getUserName());
                            User.get().setEmail(currUser.getEmail());
                            User.get().setPassword(currUser.getPassword());
                            User.get().setPhoto(currUser.getPhoto());

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
    public boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    //Toast.makeText(getApplicationContext(),"Internet Connected",Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }
}
