package com.saigopal.imagemarker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.saigopal.imagemarker.utils.DetectConnection;
import com.saigopal.imagemarker.databinding.ActivitySignInBinding;


public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 234;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in);
        binding.setLifecycleOwner(this);


        progressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.signIn.setOnClickListener(view -> checkSignIn());

    }

    private void checkSignIn(){
        if (DetectConnection.isInternetAvailable(SignInActivity.this)) {
            signIn();
            progressDialog.setMessage("Signing In...");
            progressDialog.show();
        } else {
            Snackbar s =Snackbar.make(binding.getRoot(), "No Internet connection", 10000);
            s.show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(SignInActivity.this, "ERROR CODE "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(SignInActivity.this,"Login failed\nPlease try again",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        finishAffinity();
    }


}