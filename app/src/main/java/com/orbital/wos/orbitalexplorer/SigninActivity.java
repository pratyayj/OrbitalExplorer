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

public class SigninActivity extends AppCompatActivity {

    private TextView mGoSignup;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mButtonSignin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mGoSignup = findViewById(R.id.textViewGoSignup);
        mEditTextEmail = findViewById(R.id.editTextEmailSignin);
        mEditTextPw = findViewById(R.id.editTextPasswordSignin);
        mButtonSignin = findViewById(R.id.buttonSignin);

        auth = FirebaseAuth.getInstance();

        mButtonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();

                boolean checkEmail = TextUtils.isEmpty(email);
                boolean checkPw = TextUtils.isEmpty(password);

                if(checkEmail && checkPw) {
                    Toast.makeText(SigninActivity.this, "Enter both email address and password!", Toast.LENGTH_SHORT).show();
                } else if (checkEmail) {
                    Toast.makeText(SigninActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
                } else if (checkPw) {
                    Toast.makeText(SigninActivity.this, "Enter password!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SigninActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(SigninActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

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
