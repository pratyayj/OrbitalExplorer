package com.orbital.wos.orbitalexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private double latitudeStart;
    private double longitudeStart;
    private String trailTitle;

    private double latA;
    private double longA;
    private String temp;
    private FirebaseDatabase firebaseDatabase;
    private MapsPOICallback mapsPOICallback;

    ArrayList<PointsOfInterest> poiArray;
    public static final int REQUEST_LOCATION_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        poiArray = new ArrayList<>();

        // Callback object that is called whenever a point of interest is created.
        mapsPOICallback = new MapsPOICallback() {
            @Override
            public void onCallBack(PointsOfInterest pointOfInterest) {
                createPoints(pointOfInterest);
                poiArray.add(pointOfInterest);
            }
        };

        /**
         * This group of method calls and assignments relate to
         * the ActionBar and ToolBar.
         */
        toolbarAssignment(R.drawable.ic_arrow_back_grey_24dp);
        firebaseDatabase = FirebaseDatabase.getInstance("https://orbitalexplorer-206609.firebaseio.com/");

        Intent intent = getIntent();
        latitudeStart = intent.getDoubleExtra("latitude", 0);
        longitudeStart = intent.getDoubleExtra("longitude", 0);
        trailTitle = intent.getStringExtra("title");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available (is a callback that is  triggered
     * when the map is ready to be used.)
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) { // called whenever the map is ready to be used
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // displays the map
            buildGoogleApiClient();
            // allows for my location to be found
            mMap.setMyLocationEnabled(true);

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setCompassEnabled(true);
            uiSettings.setZoomControlsEnabled(true);
        }

        getPointsOfInterestFirebase(mapsPOICallback);

        LatLng trailStart = new LatLng(latitudeStart, longitudeStart);
        Marker start = mMap.addMarker(new MarkerOptions()
                .position(trailStart)
                .title("Start here.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        start.setTag("start");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(trailStart));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                for (int i = 0; i < poiArray.size(); i++) {
                    if (marker.getTag().equals(poiArray.get(i))) {
                        // TO PASS AS AN OBJECT
                        PointsOfInterest tempPOI = (PointsOfInterest) marker.getTag();
                        String tempTitle = tempPOI.getTitle();
                        String tempDescription = tempPOI.getDescription();
                        String tempPlaceID = tempPOI.getPlaceID();
                        Intent intent = new Intent(MapsActivity.this, POIDisplayPopup.class);
                        intent.putExtra("poiTitle", tempTitle);
                        intent.putExtra("poiDescription", tempDescription);
                        intent.putExtra("poiPlaceID", tempPlaceID);
                        startActivity(intent);
                    } else {

                    }
                }
                return false;
            }
        });
    }

    protected void getPointsOfInterestFirebase(final MapsPOICallback mapsPOICallback) {
        DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference innerRef = mainRef.child("poi").child(trailTitle);

        innerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    PointsOfInterest poi = new PointsOfInterest();
                    poi = ds.getValue(PointsOfInterest.class);
                    mapsPOICallback.onCallBack(poi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void createPoints (PointsOfInterest poi) {
        Marker mark = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mark.setTag(poi);
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    // method for LocationListener
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location; // current location

        if (currentLocationMarker != null) { // removes old marker
            currentLocationMarker.remove();
        }

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        // move the camera to the location
        float zoomLevel = 7.5f; //This goes up to 21

        /*
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(15)
                    .bearing(lastLocation.getBearing())
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        */

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        // if there is a client, we want to stop location update after setting location to client
        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    // method for GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    /**
     * This method checks the location permissions for the application.
     * @return false if permission not given, true if given already.
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) { // if app asked before and the user said no
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } return false; // don't ask again option when it previously asked
        } else {
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // method for GoogleApiClient.OnConnectionFailedListener,
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * To set up the toolbar on the top of the Activity.
     * @param drawable The drawable item that will be used for the top-right hand icon.
     */
    protected void toolbarAssignment(int drawable) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(drawable);
    }

    // To return to the previous activity when the back arrow is pressed.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method produces a dialogue box for permission but overrides it if already given permission
     * before hand.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // permission is granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) { // if no client we create a new client
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else { // permission is denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                } return;
        }
    }


}
