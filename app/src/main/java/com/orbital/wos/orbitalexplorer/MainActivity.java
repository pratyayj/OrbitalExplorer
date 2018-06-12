package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signout = findViewById(R.id.buttonSignout);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignout();
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
