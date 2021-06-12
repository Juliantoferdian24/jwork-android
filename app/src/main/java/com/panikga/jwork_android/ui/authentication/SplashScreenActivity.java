package com.panikga.jwork_android.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.panikga.jwork_android.R;
import com.panikga.jwork_android.ui.HomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences prefs = getSharedPreferences("name", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn) {
                    Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    i.putExtra("jobseekerId", prefs.getInt("id", 0));
                    i.putExtra("jobseekerName", prefs.getString("name", ""));
                    i.putExtra("jobseekerEmail", prefs.getString("email", ""));
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);
    }
}