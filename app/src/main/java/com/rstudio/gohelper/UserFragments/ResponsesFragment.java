package com.rstudio.gohelper.UserFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rstudio.gohelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResponsesFragment extends Fragment {

    RelativeLayout layout;

    public ResponsesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_responses, container, false);


        return layout;
    }

}
