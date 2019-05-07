package com.rstudio.gohelper.UserFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.rstudio.gohelper.R;
import com.rstudio.gohelper.UserLoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    FrameLayout bt_Logout;
    FirebaseAuth auth;
    RelativeLayout layout;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_settings, container, false);
        setValues();
        setListeners();
        return layout;
    }

    private void setValues(){
        bt_Logout = layout.findViewById(R.id.bt_logoutUser);
        auth = FirebaseAuth.getInstance();
    }
    private void setListeners(){
        bt_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getContext(),UserLoginActivity.class));
            }
        });
    }

}
