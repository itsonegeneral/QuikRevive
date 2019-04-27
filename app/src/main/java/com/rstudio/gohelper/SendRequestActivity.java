package com.rstudio.gohelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class SendRequestActivity extends AppCompatActivity implements OnMapReadyCallback ,LocationListener{

    private MapView mapView;
    private GoogleMap map;
    private String reqCode;
    private TextView tvHeading,tvLat,tvLong;
    private EditText etPincode, etMesasge;
    private ImageView mainLogo;
    private double currentLatitude=9.155416;
    private ProgressBar pgBar ;
    private double currentLongitude =76.72991;
    private static final String TAG = "SendRequestActivity";

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        if (getIntent().hasExtra("requestcode")) {
            reqCode = getIntent().getStringExtra("requestcode");
            loadData();
        } else {
            Toast.makeText(this, "Error 204", Toast.LENGTH_SHORT).show();
        }
        setValues();
        setToolbar();
        GPSTracker gpsTracker = new GPSTracker(this);
        if(gpsTracker.canGetLocation()){
            currentLatitude = gpsTracker.getLatitude();
            currentLongitude= gpsTracker.getLongitude();
            String lat = "Lat : " + String.valueOf(currentLatitude);
            String log = "Long : " + String.valueOf(currentLongitude);
            tvLat.setText(lat);
            tvLong.setText(log);
        }else{
            Log.d(TAG, "onCreate: Error");
        }
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d(TAG, "onMapReady: Map Ready");
        LatLng position= new LatLng(currentLatitude,currentLongitude);
        map.addMarker(new MarkerOptions().position(position).title("Current Position"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        pgBar.setVisibility(View.GONE);

    }




    private void loadData() {
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



    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_sendRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude= location.getLongitude();
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
