package com.rstudio.gohelper;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

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

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.tb_userHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        tv.setText("Home");
    }
}
