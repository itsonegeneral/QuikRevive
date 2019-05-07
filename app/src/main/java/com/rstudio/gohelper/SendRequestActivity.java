package com.rstudio.gohelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class SendRequestActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private MapView mapView;
    private GoogleMap map;
    private String reqCode, userName, userPhone;
    private TextView tvHeading, tvLat, tvLong;
    private EditText etPincode, etMesasge;
    private ImageView mainLogo;
    private Button btSend;
    private double currentLatitude = 9.155416;
    private ProgressBar pgBar;
    private double currentLongitude = 76.72991;
    private static final String TAG = "SendRequestActivity";
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("adminids");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String playerID;
    private String userPlayerId;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        if (getIntent().hasExtra("requestcode")) {
            reqCode = getIntent().getStringExtra("requestcode");

        } else {
            Toast.makeText(this, "Error 204", Toast.LENGTH_SHORT).show();
        }
        setValues();

        //Get User ID
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        userPlayerId = status.getSubscriptionStatus().getUserId();

        setToolbar();
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.getLatitude();
            currentLongitude = gpsTracker.getLongitude();
            String lat = "Lat : " + String.valueOf(currentLatitude);
            String log = "Long : " + String.valueOf(currentLongitude);
            tvLat.setText(lat);
            tvLong.setText(log);
        } else {
            Log.d(TAG, "onCreate: Error");
        }
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    loadData();
                    Log.d(TAG, "onClick: Button Clicked");
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d(TAG, "onMapReady: Map Ready");
        LatLng position = new LatLng(currentLatitude, currentLongitude);
        map.addMarker(new MarkerOptions().position(position).title("Current Position"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        pgBar.setVisibility(View.GONE);

    }

    private void sendNotification(final String playerID, final String message) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        uploadData();

        try {

            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + playerID + "']}"), new OneSignal.PostNotificationResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, "Notification onSuccess: " + response.toString());
                }

                @Override
                public void onFailure(JSONObject response) {
                    Log.d(TAG, "Notification onFailure : " + response.toString());
                }
            });




        } catch (Throwable t) {
            t.printStackTrace();
        }

    }


    private void loadData() {
        Log.d(TAG, "loadData: Load Data");
        final String pin = etPincode.getText().toString();
        final String message = etMesasge.getText().toString();
        ref.child(reqCode).child(pin).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    playerID = dataSnapshot.getValue(String.class);
                    sendNotification(playerID, message);
                    Log.d(TAG, "onDataChange: Admin Present");
                } else {
                    Toast.makeText(SendRequestActivity.this, "No Authorities at your location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SendRequestActivity.this, "Database Error Occured", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData() {
        Log.d(TAG, "uploadData: Upload Data");

        //Get name and phone
        final String pin = etPincode.getText().toString();
        String reqId = UUID.randomUUID().toString();
        Date currentTime = Calendar.getInstance().getTime();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requesttoadmin");
        DatabaseReference uref = FirebaseDatabase.getInstance().getReference("requests").child(FirebaseAuth.getInstance().getUid());

        Request request = new Request();
        request.setPositionX(currentLatitude);
        request.setPositionY(currentLongitude);
        request.setSenderId(FirebaseAuth.getInstance().getUid());
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        request.setRequesttype(tv.getText().toString());
        request.setStatus("Incomplete");
        request.setTime(currentTime.toString());
        request.setRequestId(reqId);
        request.setSenderMessage(etMesasge.getText().toString());
        request.setSenderOneSignalId(userPlayerId);
        request.setSenderName(userName);
        request.setSenderPhone(Long.valueOf(userPhone));
        //Upload to admin side
        reference.child(pin).child(reqCode).child(reqId).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SendRequestActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SendRequestActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                //Upload to user side
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SendRequestActivity.this, "Failed "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        uref.child(reqId).setValue(request);

    }

    private void setValues() {
        tvHeading = findViewById(R.id.tv_sendRequestHeading);
        mapView = findViewById(R.id.mapViewUser);
        etMesasge = findViewById(R.id.et_messageRequest);
        etPincode = findViewById(R.id.et_pincodeRequest);
        mainLogo = findViewById(R.id.img_logoSendRequest);
        pgBar = findViewById(R.id.pgBar_mapViewUser);
        pgBar.setVisibility(View.VISIBLE);
        tvLat = findViewById(R.id.tv_sendRequestLat);
        tvLong = findViewById(R.id.tv_sendRequestLong);
        btSend = findViewById(R.id.bt_sendRequest);

        try {
            userName = getIntent().getStringExtra("username");
            userPhone = String.valueOf(getIntent().getLongExtra("userphone",0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        switch (reqCode) {
            case "vehicle": {
                mainLogo.setBackgroundResource(R.drawable.vehicle);
                tvHeading.setText("Send Alert To Vehicle Movers");
                TextView tv = findViewById(R.id.tv_toolbarHeading);
                tv.setText("Vehicle Movers");
                break;
            }
            case "ambulance": {
                mainLogo.setBackgroundResource(R.drawable.ambulance);
                tvHeading.setText("Send Alert To Ambulance");
                TextView tv = findViewById(R.id.tv_toolbarHeading);
                tv.setText("Call Ambulance");
                break;
            }
            case "police": {
                mainLogo.setBackgroundResource(R.drawable.keralapolice);
                tvHeading.setText("Send Alert To Police");
                TextView tv = findViewById(R.id.tv_toolbarHeading);
                tv.setText("Call Police");
                break;
            }
            case "fireforce": {
                mainLogo.setBackgroundResource(R.drawable.fireforce);
                tvHeading.setText("Send Alert To FireForce");
                TextView tv = findViewById(R.id.tv_toolbarHeading);
                tv.setText("Call FireForce");
                break;
            }

        }

    }

    private boolean validateInput() {
        String pin = etPincode.getText().toString();
        String message = etMesasge.getText().toString();
        if (message.isEmpty()) {
            etMesasge.setError("Enter Message");
        } else if (pin.isEmpty()) {
            etPincode.setError("Enter Pincode");
        } else {
            return true;
        }
        return false;
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_sendRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        String lat = "Lat : " + String.valueOf(currentLatitude);
        String log = "Long : " + String.valueOf(currentLongitude);
        tvLat.setText(lat);
        tvLong.setText(log);

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
