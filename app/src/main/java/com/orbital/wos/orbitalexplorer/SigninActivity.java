package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SigninActivity extends AppCompatActivity {

    private TextView mGoSignup;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mButtonSignin;
    private SignInButton mButtonGoogleSignin;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mGoSignup = findViewById(R.id.textViewGoSignup);
        mEditTextEmail = findViewById(R.id.editTextEmailSignin);
        mEditTextPw = findViewById(R.id.editTextPasswordSignin);
        mButtonSignin = findViewById(R.id.buttonSignin);
        mButtonGoogleSignin = findViewById(R.id.buttonGoogleSignin);

        auth = FirebaseAuth.getInstance();

        /*
         * This is to sign in a user using conventional e-mail and password.
         */
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
                                    }
                                }
                            });
                }
            }
        });

        /*
          This sets what the Google Sign In button does.
         */
        mButtonGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        /*
          This builds the GoogleSignInOptions object (which will then request for the
          specific e-mail to sign in with).
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Following which the GoogleSignInClient is created using the GoogleSignInOptions object.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // This directs the user to the sign up page for normal user e-mail sign up.
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

    /**
     * This method begins the sign in process with Google.
     */
    private void signInWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    /**
     * This method carries on the Google Sign In process upon receiving the
     * result from the previous Activity.
     * @param requestCode The request code from the previous Activity.
     * @param resultCode  The code produced as a result of the previous Activity.
     * @param data The data passed as an Intent to create the task for Google Sign In.
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("FailedGoogleSignin", "Google sign in failed", e);
            }
        }
    }

    /**
     * This method sets up the Google account used to sign in within the Firebase
     * database used to store all user accounts for the application.
     * @param acct GoogleSignInAccount used to sign in to the application.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("googleSigninStatus", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(SigninActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("googleSigninstatus", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication with Google failed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SigninActivity.this, SignupActivity.class));
                        }
                    }
                });
    }

    /**
     * This method checks upon the start of this Activity if there is any user currently logged in.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // User can be of any type - not necessarily Google login.
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        } else {
            updateUI();
        }
    }

    /**
     * This method updates the UI if there is a user logged in by switching to the MainActivity.
     */
    public void updateUI() {
        startActivity(new Intent(SigninActivity.this, MainActivity.class));
    }

}
