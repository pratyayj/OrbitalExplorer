package com.orbital.wos.orbitalexplorer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private double latitudeStart;
    private double longitudeStart;
    private String trailTitle;
    private int numberpoi;

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

        final String apiKey = getString(R.string.google_api_key);

        poiArray = new ArrayList<>();

        // Callback object that is called whenever a point of interest is created.
        mapsPOICallback = new MapsPOICallback() {
            @Override
            public void onCallBack(PointsOfInterest pointOfInterest) {
                createPoints(pointOfInterest);
                poiArray.add(pointOfInterest);
                Collections.sort(poiArray, new POIComparator());

                /* Once all the points have been added, this section of the code - i.e. the creation
                 * of the actual trail path - will be executed.
                 */
                if (poiArray.size() > numberpoi) {

                    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
                    for (PointsOfInterest poi : poiArray) {
                        LatLng temp = new LatLng(poi.getLatitude(), poi.getLongitude());
                        latLngArrayList.add(temp);
                    }

                    for (int i = 0; i < numberpoi; i++) {
                        String url = getUrl(latLngArrayList.get(i), latLngArrayList.get(i+1), apiKey);
                        FetchUrl fetchUrl = new FetchUrl();
                        fetchUrl.execute(url);
                    }

                }
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
        numberpoi = intent.getIntExtra("numberpoi", 0);


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

        LatLng trailStart = new LatLng(latitudeStart, longitudeStart);

        // Creation of a generic point of interest for the start point of any trail.
        PointsOfInterest start = new PointsOfInterest("Start", "Starting point.",
                latitudeStart, longitudeStart, "-", 1, "start");
        poiArray.add(start);

        // Adds the start point as a marker on the map.
        Marker startMarker = mMap.addMarker(new MarkerOptions()
                .position(trailStart)
                .title("Start here.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        startMarker.setTag("start");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(trailStart));

        getPointsOfInterestFirebase(mapsPOICallback);

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
                        if (tempPlaceID == null) {
                            tempPlaceID = "nothing";
                        }
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

    /**
     * This method retrieves/gets the points of interests from the Firebase database.
     * @param mapsPOICallback The callback that will be performed when this method is called.
     */
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

    /**
     * This method creates a marker for the point of interest on the map.
     * @param poi The point of interest to be marked on to the map.
     */
    protected void createPoints (PointsOfInterest poi) {
        String type = poi.getType();
        Drawable icon = null;

        if (type.equals("culture")) {
            icon = getResources().getDrawable(R.drawable.ic_culture_24dp);
        } else if (type.equals("park")) {
            icon = getResources().getDrawable(R.drawable.ic_park_24dp);
        } else if (type.equals("attraction")) {
            icon = getResources().getDrawable(R.drawable.ic_attraction_24dp);
        } else if (type.equals("photography")) {
            icon = getResources().getDrawable(R.drawable.ic_photography_24dp);
        } else if (type.equals("service")) {
            icon = getResources().getDrawable(R.drawable.ic_services_24dp);
        }

        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(icon);

        Marker mark = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
                .icon(markerIcon));
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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
            case R.id.rate:
                DialogFragment newFragment = RatingFragment.newInstance();
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("title", trailTitle);
                newFragment.setArguments(bundleTitle);
                newFragment.show(getFragmentManager(), "dialog");
                return true;
            case R.id.share_tips:
                Intent intent = new Intent(MapsActivity.this, TipsInputActivity.class);
                startActivity(intent);
                return true;
            case R.id.read_tips:
                Intent intent2 = new Intent(MapsActivity.this, TipsListActivity.class);
                startActivity(intent2);
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


    /**
     * This method creates the URL that is to be used to retrieve the JSON data regarding directions.
     * @param origin Origin LatLng (i.e. start)
     * @param dest Destination LatLng (i.e. end)
     * @param key ApiKey
     * @return URL string
     */
    private String getUrl(LatLng origin, LatLng dest, String key) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // Mode of navigation is walking
        String mode = "mode=walking";

        String apiKey = key;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + apiKey;

        return url;
    }

    /**
     * A method to download JSON direction data from the URL generated earlier.
     * @param strUrl The URL generated to request for directions between two points
     * @return The string data generated.
     * @throws IOException Input-Output exception
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Fetches the data from the download URL link and then executes the parsing once all is retrieved.
     */
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format using the DirectionsParser class.
     * Following this, the trail line is drawn out in the map.
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DirectionsParser parser = new DirectionsParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(R.color.purpleHM);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        inflater.inflate(R.menu.user_input_menu, menu);
        return true;
    }


}
