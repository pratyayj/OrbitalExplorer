package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private TextView mGoSignin;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mButtonSignup;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mGoSignin = findViewById(R.id.textViewGoSignin);
        mEditTextEmail = findViewById(R.id.editTextEmailSignup);
        mEditTextPw = findViewById(R.id.editTextPasswordSignup);
        mButtonSignup = findViewById(R.id.buttonSignup);

        auth = FirebaseAuth.getInstance();

        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();

                boolean checkEmail = TextUtils.isEmpty(email);
                boolean checkPw = TextUtils.isEmpty(password);

                if (checkEmail && checkPw) {
                    Toast.makeText(SignupActivity.this, "Enter email address and password!", Toast.LENGTH_SHORT).show();
                } else if (checkEmail) {
                    Toast.makeText(SignupActivity.this, "Enter email address.", Toast.LENGTH_SHORT).show();
                } else if (checkPw) {
                    Toast.makeText(SignupActivity.this, "Enter password.", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Enter a password longer than 6 characters.", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                }

            }
        });

        mGoSignin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goSignin(v);
            }
        });
    }

    /**
     * Method that takes the user to the Signin page if they already
     * have an account.
     * @param view The view it is coming from.
     */
    public void goSignin(View view){
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }


}
