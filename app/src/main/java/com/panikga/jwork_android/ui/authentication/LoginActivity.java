package com.panikga.jwork_android.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.panikga.jwork_android.R;
import com.panikga.jwork_android.request.LoginRequest;
import com.panikga.jwork_android.ui.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);


        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                    editor.putInt("id", jsonObject.getInt("id"));
                    editor.putString("name", jsonObject.getString("name"));
                    editor.putString("email", jsonObject.getString("email"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login Successful " + jsonObject.getInt("id"), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.putExtra("jobseekerId", jsonObject.getInt("id"));
                    i.putExtra("jobseekerName", jsonObject.getString("name"));
                    i.putExtra("jobseekerEmail", jsonObject.getString("email"));
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            };

            LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}