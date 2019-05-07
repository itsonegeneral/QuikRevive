package com.rstudio.gohelper.UserFragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rstudio.gohelper.R;
import com.rstudio.gohelper.SendRequestActivity;
import com.rstudio.gohelper.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHomeFragment extends Fragment {

    RelativeLayout layout;
    private TextView tvUserName, tvVerifiedStatus;
    private ImageView verifyImage;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private DatabaseReference ref;
    private ProgressBar pgBar;
    private FirebaseUser user;
    private Button btPolice, btAmbulance, btVehicle, btFireforce;
    private User userClass;

    public UserHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_user_home, container, false);

        tvUserName = layout.findViewById(R.id.tv_userHomeName);
        pgBar = layout.findViewById(R.id.pgBar_Home);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        tvVerifiedStatus = layout.findViewById(R.id.tv_userHomeVerifyStatus);
        verifyImage = layout.findViewById(R.id.img_verifiedBadge);
        user = mAuth.getCurrentUser();
        btAmbulance = layout.findViewById(R.id.bt_callAmbulance);
        btPolice = layout.findViewById(R.id.bt_callPolice);
        btFireforce = layout.findViewById(R.id.bt_callFireforce);
        btVehicle = layout.findViewById(R.id.bt_callVehicleMovers);


        loadData();
        setListeners();


        return layout;
    }

    private void setListeners() {
        tvVerifiedStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            AlertDialog alertDialog = new AlertDialog.Builder(layout.getContext()).create();
                            alertDialog.setTitle("Sent ");
                            alertDialog.setMessage("Check email for verification link");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });

        btVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SendRequestActivity.class);
                i.putExtra("requestcode", "vehicle");
                i.putExtra("username", userClass.getName());
                i.putExtra("userphone", userClass.getPhone());
                startActivity(i);
            }
        });
        btPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SendRequestActivity.class);
                i.putExtra("requestcode", "police");
                i.putExtra("username", tvUserName.getText().toString());
                i.putExtra("userphone", userClass.getPhone());
                startActivity(i);
            }
        });
        btFireforce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SendRequestActivity.class);
                i.putExtra("username", userClass.getName());
                i.putExtra("userphone", userClass.getPhone());
                i.putExtra("requestcode", "fireforce");
                startActivity(i);

            }
        });
        btAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), SendRequestActivity.class);
                i.putExtra("username", userClass.getName());
                i.putExtra("userphone", userClass.getPhone());
                i.putExtra("requestcode", "ambulance");
                startActivity(i);
            }
        });

    }


    private void loadData() {
        pgBar.setVisibility(View.VISIBLE);
        pgBar.setIndeterminate(true);
        userId = mAuth.getUid();
        ref.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userClass = dataSnapshot.getValue(User.class);
                    tvUserName.setText(userClass.getName());

                } else {
                    Toast.makeText(getContext(), "NO DATA", Toast.LENGTH_SHORT).show();
                }
                pgBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgBar.setVisibility(View.GONE);
            }
        });

        if (user.isEmailVerified()) {
            tvVerifiedStatus.setVisibility(View.GONE);
            verifyImage.setVisibility(View.VISIBLE);
        } else {
            tvVerifiedStatus.setVisibility(View.VISIBLE);
            verifyImage.setVisibility(View.GONE);

        }
    }
}


