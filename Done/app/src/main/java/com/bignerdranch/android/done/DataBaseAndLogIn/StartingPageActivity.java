package com.bignerdranch.android.done.DataBaseAndLogIn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.bignerdranch.android.done.R;

/**
 * Created by Ico on 19-Apr-16.
 */
public class StartingPageActivity extends AppCompatActivity {
    Button mButtonLogin;
    Button mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mButtonLogin = (Button) findViewById(R.id.buttonStartPageLogin);
        mButtonRegister = (Button) findViewById(R.id.buttonStartPageCreateAccount);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
