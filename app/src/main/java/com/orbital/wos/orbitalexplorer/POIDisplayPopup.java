package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class POIDisplayPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidisplay_popup);

        // Setting StrictMode policy to permitted in order to access JSON on internet.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Retrieving data from Intent.
        Intent i = getIntent();
        String title = i.getStringExtra("poiTitle");
        String desc = i.getStringExtra("poiDescription");
        final String placeID = i.getStringExtra("poiPlaceID");

        // Finding the TextViews for Title and Description.
        TextView titleTV = findViewById(R.id.poi_title);
        TextView descTV = findViewById(R.id.poi_description);

        // Setting the title and the description from the retrieved bundled data.
        titleTV.setText(title);
        descTV.setText(desc);

        // Scaling of the pop-up window that displays the information of the point of interest.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        final String apiKey = getString(R.string.google_api_key);

        final String jsonurl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID +
                "&key=" + apiKey;

        // Construct a GeoDataClient.
        final GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);

        // Retrieves data via Google Maps API using the placeID.
        mGeoDataClient.getPlaceById(placeID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                boolean openNow = true;
                try {
                    JSONObject obj = new JSONObject(getJSON(jsonurl));

                    openNow = obj.getJSONObject("result").getJSONObject("opening_hours").getBoolean("open_now");

                    JSONArray photosArray = obj.getJSONArray("photo");
                    String photoRef = photosArray.getJSONObject(0).getString("photo_reference");

                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                }

                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    TextView temp = findViewById(R.id.poi_open_now);
                    if (openNow == true) {
                        temp.setText("Open now!");
                        temp.setTextColor(getResources().getColor(R.color.greenFade));
                        temp.setTypeface(null, Typeface.BOLD);
                    } else {
                        temp.setText("Closed.");
                        temp.setTextColor(Color.RED);
                    }
                    getPhotos(mGeoDataClient, placeID);
                    places.release();

                } else {
                    Toast.makeText(POIDisplayPopup.this, "Failed lmao", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method helps to get the JSON data from a particular url,
     * in this case, maps.googleapis.com.
     * @param url The url that leads to the JSON data.
     * @return Returns the JSON string.
     */
    public static String getJSON(String url) {
        HttpsURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpsURLConnection) u.openConnection();
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Request photos and metadata for the specified place. This method is adapated from Google.
     * @param mGeoDataClient The GeoDataClient that will obtain the data.
     * @param placeId PlaceID of location whose data will be obtained.
     */
    private void getPhotos(GeoDataClient mGeoDataClient, String placeId) {
        final GeoDataClient localGeoDataClient = mGeoDataClient;
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = localGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = localGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        Context context = getApplicationContext();
                        // Proceed to load obtained image to ImageView.
                        loadPhoto(bitmap);
                    }
                });
            }
        });
    }

    /**
     * This method loads the image into the imageView of the pop up display.
     * @param bp The bitmap image to be loaded.
     */
    private void loadPhoto(final Bitmap bp) {
        final ImageView poiImageView = findViewById(R.id.poi_image);
        poiImageView.setImageBitmap(bp);
    }
}
