package com.example.communiclean;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Activity");
        //firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");

        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        // swaps screens between fragments when selected
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            final int itemIds = menuItem.getItemId();
            if (itemIds == R.id.nav_home) {
                actionBar.setTitle("Home");
                HomeFragment fragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment, "");
                fragmentTransaction.commit();

                Log.d("DashboardActivity", "User navigated to Home fragment");

                return true;
            }
            else if (itemIds == R.id.nav_users) {
                actionBar.setTitle("Users");
                UsersFragment fragment2 = new UsersFragment();
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.content, fragment2, "");
                fragmentTransaction2.commit();

                Log.d("DashboardActivity", "User navigated to Users fragment");
                return true;
            }
            else if (itemIds == R.id.nav_addblogs) {
                actionBar.setTitle("Add Blogs");
                AddBlogsFragment fragment4 = new AddBlogsFragment();
                FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction4.replace(R.id.content, fragment4, "");
                fragmentTransaction4.commit();

                Log.d("DashboardActivity", "User navigated to Add Blogs fragment");
                return true;
            }
            else if (itemIds == R.id.nav_chat) {
                actionBar.setTitle("Chats");
                ChatListFragment listFragment = new ChatListFragment();
                FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction3.replace(R.id.content, listFragment, "");
                fragmentTransaction3.commit();

                // Temporary sign out, uncomment this and click the home button if there are issues
                // regarding nullifying non-existant users
                //FirebaseAuth.getInstance().signOut();
                //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //startActivity(intent);
                //finish();

                Log.d("DashboardActivity", "User navigated to Chat fragment");
                return true;
            }
            else if (itemIds == R.id.nav_profile) {
                actionBar.setTitle("Profile");
                ProfileFragment fragment1 = new ProfileFragment();
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.content, fragment1);
                fragmentTransaction1.commit();

                Log.d("DashboardActivity", "User navigated to Profile fragment");
                return true;
            }
            else {
                Log.d("DashboardActivity", "Unexpected value: " + menuItem.getItemId());
                return false;
            }
        }
    };
}