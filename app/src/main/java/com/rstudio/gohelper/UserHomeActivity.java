package com.rstudio.gohelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rstudio.gohelper.UserFragments.ResponsesFragment;
import com.rstudio.gohelper.UserFragments.SettingsFragment;
import com.rstudio.gohelper.UserFragments.UserHomeFragment;

public class UserHomeActivity extends AppCompatActivity {


    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_userHome,new UserHomeFragment()).commit();
        setToolbar();

        navView = findViewById(R.id.navMenu_UserHome);
        navView.setOnNavigationItemSelectedListener(listener);
        getPermissions();

    }

    private void getPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;
            switch (menuItem.getItemId()){
                case R.id.nav_Home:{
                    fragment = new UserHomeFragment();
                    break;
                }
                case R.id.nav_settings :{
                    fragment = new SettingsFragment();
                    break;
                }
                case R.id.nav_Responses:{
                    fragment = new ResponsesFragment();
                    break;
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fl_userHome,fragment).commit();
            return true;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your Location , App Will not Work", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.tb_userHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        tv.setText("Home");
    }
}
