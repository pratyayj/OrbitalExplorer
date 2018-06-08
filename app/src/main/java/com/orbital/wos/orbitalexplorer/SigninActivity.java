package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SigninActivity extends AppCompatActivity {

    private TextView mGoSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mGoSignup = findViewById(R.id.textViewGoSignup);
        mGoSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goSignup(v);
            }
        });
    }

    /**
     * Method that takes the user to the Signup page if they do not
     * have an account.
     * @param view The view it is coming from.
     */
    public void goSignup(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
