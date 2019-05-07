package com.rstudio.gohelper;

import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    String name, phone, message, status;
    TextView tvName, tvPhone, tvMessage, tvLocation;
    TextView tvRequestId;

    Spinner statusSpinner;
    MapView mapView;
    GoogleMap map;
    double lat, log;
    Button btSubmit;
    String newStatus, senderId, requestId;
    private static final String TAG = "ViewRequestActivity";
    private DatabaseReference ref, refAdmin;
    private String pincode;
    private String admincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        mapView = findViewById(R.id.mapViewAdmin);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        tvMessage = findViewById(R.id.tv_senderMessageAdmin);
        tvPhone = findViewById(R.id.tv_senderContactAdmin);
        statusSpinner = findViewById(R.id.spinner_sendresponse);
        tvName = findViewById(R.id.tv_senderNameAdmin);
        tvLocation = findViewById(R.id.tv_senderLocation);
        btSubmit = findViewById(R.id.bt_sendResponse);
        tvRequestId = findViewById(R.id.tv_requestIdadmin);
        ref = FirebaseDatabase.getInstance().getReference("requests");
        refAdmin = FirebaseDatabase.getInstance().getReference("requesttoadmin");

        setToolbar();
        loadValues();
        setSpinner();


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newStatus = statusSpinner.getSelectedItem().toString();
                ref.child(senderId).child(requestId).child("status").setValue(newStatus);
                refAdmin.child(pincode).child(admincode)
                        .child(requestId).child("status")
                        .setValue(newStatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ViewRequestActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ViewRequestActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d(TAG, "onMapReady: Map Ready");
        LatLng position = new LatLng(lat, log);
        map.addMarker(new MarkerOptions().position(position).title("Sender Position"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

    }

    private void loadValues() {
        name = getIntent().getStringExtra("name");
        phone = String.valueOf(getIntent().getExtras().getLong("phone"));
        message = getIntent().getStringExtra("message");
        lat = getIntent().getExtras().getDouble("lat");
        log = getIntent().getExtras().getDouble("log");
        status = getIntent().getStringExtra("status");
        senderId = getIntent().getStringExtra("senderid");
        requestId = getIntent().getStringExtra("requestid");


        tvName.setText(name);
        tvMessage.setText(message);
        tvPhone.setText(phone);
        tvLocation.setText(lat + " " + log);
        tvRequestId.setText(requestId);


        DatabaseReference r = FirebaseDatabase.getInstance().getReference("admins").child(FirebaseAuth.getInstance().getUid());
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                admincode = dataSnapshot.child("admincode").getValue(String.class);
                long pin = dataSnapshot.child("pincode").getValue(Long.class);
                pincode = String.valueOf(pin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_viewRequest);
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        tv.setText("View Request");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.response_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}
