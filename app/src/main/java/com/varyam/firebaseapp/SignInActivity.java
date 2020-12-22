package com.varyam.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SignInActivity";
    public static final int RC_SIGN_IN = 101;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "onCreate: started............");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: started............");
        FirebaseUser account = mAuth.getCurrentUser();
        updateUI(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            firebaseAuthWithGoogle(account.getIdToken());

        } catch (ApiException e) {
            Log.d(TAG, "handleSignInResult: called  " + e.getMessage());
            Toast.makeText(this, "Error while logging you in. Try Again.", Toast.LENGTH_LONG).show();
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(String token) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.d(TAG, "onComplete: called");
                            Toast.makeText(getParent(), "Error while logging you in. Try Again.", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser account) {
        SignInButton sign = findViewById(R.id.sign_in_button);
        if (account != null) {
            Log.d(TAG, "updateUI: starting mainActivity");
            sign.setVisibility(View.INVISIBLE);

            user user = new user(account.getDisplayName(), account.getUid(), String.valueOf(account.getPhotoUrl()));
            com.varyam.firebaseapp.FireCloud cloud = new com.varyam.firebaseapp.FireCloud();
            cloud.addUser(user);

            Intent mainActivityLaunch = new Intent(this, com.varyam.firebaseapp.MainActivity.class);
            startActivity(mainActivityLaunch);
        } else {
            Log.d(TAG, "updateUI: sign in");
            sign.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent signIn = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIn, RC_SIGN_IN);
    }
}