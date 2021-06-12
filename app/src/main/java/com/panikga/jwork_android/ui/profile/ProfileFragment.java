package com.panikga.jwork_android.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.panikga.jwork_android.ui.authentication.LoginActivity;
import com.panikga.jwork_android.R;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    LinearLayout btnLogout;
    TextView name;
    TextView email;

    private static int jobseekerId;
    private static String jobseekerName;
    private static String jobseekerEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = root.findViewById(R.id.btnLogout);
        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            jobseekerId = extras.getInt("jobseekerId");
            jobseekerName = extras.getString("jobseekerName");
            jobseekerEmail = extras.getString("jobseekerEmail");
        }

        name.setText(jobseekerName);
        email.setText(jobseekerEmail);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putInt("id", 0);
                editor.putString("name", "");
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().finish();
            }
        });
        return root;
    }
}