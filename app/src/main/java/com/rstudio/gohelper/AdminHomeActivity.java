package com.rstudio.gohelper;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminHomeActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    private String adminName, adminCode, pincode, playerId;
    private DatabaseReference dbRef;
    private RecyclerView rView;
    private TextView tvAdminName, tvAdminPincode;
    private ArrayList<Request> requestList;
    private RequestAdapter mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        setToolbar();
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("admins");
        tvAdminName = findViewById(R.id.tv_adminNameHome);
        rView = findViewById(R.id.rView_admin);
        tvAdminPincode = findViewById(R.id.tv_adminPincode);
        requestList = new ArrayList<>();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        dbRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("adminname")) {
                    adminName = dataSnapshot.child("adminname").getValue(String.class);
                    tvAdminName.setText(adminName);
                }
                if (dataSnapshot.hasChild("admincode")) {
                    adminCode = dataSnapshot.child("admincode").getValue(String.class);
                }
                if (dataSnapshot.hasChild("pincode")) {
                    long pin = dataSnapshot.child("pincode").getValue(Long.class);
                    pincode = String.valueOf(pin);
                    tvAdminPincode.setText("Pincode : " + pincode);
                }
                loadRequests();
                updatePincode();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadRequests() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requesttoadmin").child(pincode).child(adminCode);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Request request = snapshot.getValue(Request.class);
                        requestList.add(request);
                    }
                } else {
                    Toast.makeText(AdminHomeActivity.this, "No Requests ..", Toast.LENGTH_SHORT).show();
                }

                Collections.sort(requestList, new Comparator<Request>() {
                    @Override
                    public int compare(Request o1, Request o2) {
                        return o1.getTime().compareTo(o2.getTime());
                    }
                });

                mAdaptor = new RequestAdapter(requestList, AdminHomeActivity.this);
                rView.setHasFixedSize(false);
                rView.setLayoutManager(new LinearLayoutManager(AdminHomeActivity.this));
                rView.setAdapter(mAdaptor);

                mAdaptor.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent i = new Intent(AdminHomeActivity.this,ViewRequestActivity.class);

                        i.putExtra("name",requestList.get(position).getSenderName());
                        i.putExtra("message",requestList.get(position).getSenderMessage());
                        i.putExtra("phone",requestList.get(position).getSenderPhone());
                        i.putExtra("lat",requestList.get(position).getPositionX());
                        i.putExtra("log",requestList.get(position).getPositionY());
                        i.putExtra("status",requestList.get(position).getStatus());
                        i.putExtra("senderid",requestList.get(position).getSenderId());
                        i.putExtra("requestid",requestList.get(position).getRequestId());

                        startActivity(i);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updatePincode() {
        //Get user ID and update
        //Get User ID
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        playerId = status.getSubscriptionStatus().getUserId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("adminids");

        ref.child(adminCode).child(pincode).setValue(playerId);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_adminHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        tv.setText("Admin Home");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logoutadmin: {
                mAuth.signOut();
                finish();
                startActivity(new Intent(AdminHomeActivity.this, UserLoginActivity.class));

                break;
            }
        }
        return true;
    }
}
