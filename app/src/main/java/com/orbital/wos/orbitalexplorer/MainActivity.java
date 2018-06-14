package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button signout;
    private TextView sampleHeader;
    private DatabaseReference firebaseDatabase;
    private ImageView sampleImage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // signout = findViewById(R.id.buttonSignout);
        sampleHeader = findViewById(R.id.text);
        sampleImage = findViewById(R.id.photo);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://orbitalexplorer-206609.appspot.com/BostonSkyline.jpg");

        Glide.with(this)
                .load(storageReference)
                .into(sampleImage);


        /* signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignout();
            }
        }); */

        DatabaseReference test = firebaseDatabase.child("headers");
        test.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String header = dataSnapshot.getValue(String.class);
                sampleHeader.setText(header);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void userSignout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, SigninActivity.class);
        startActivity(intent);
        finish();
    }

}
