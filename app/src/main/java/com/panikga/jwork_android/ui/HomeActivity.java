package com.panikga.jwork_android.ui;

import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.panikga.jwork_android.R;
import com.panikga.jwork_android.ui.home.HomeFragment;
import com.panikga.jwork_android.ui.job.JobFragment;
import com.panikga.jwork_android.ui.profile.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class HomeActivity extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        chipNavigationBar = findViewById(R.id.nav_view);
        if (savedInstanceState == null) {
            chipNavigationBar.setItemSelected(R.id.navigation_home, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }

        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            if (i == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (i == R.id.navigation_job) {
                fragment = new JobFragment();
            } else if (i == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }
            if (fragment != null) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });
    }
}