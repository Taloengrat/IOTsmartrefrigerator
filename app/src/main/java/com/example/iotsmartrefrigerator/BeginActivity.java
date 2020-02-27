package com.example.iotsmartrefrigerator;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Collections;

public class BeginActivity extends AppCompatActivity {



    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    FirebaseUser user;
    ImageView imLogo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        imLogo = findViewById(R.id.logo);




        mAuth = FirebaseAuth.getInstance();


        user = mAuth.getCurrentUser();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(BeginActivity.this, gso);

        signIn();




//        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("accoutId", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        progressDialog = ProgressDialog.show(BeginActivity.this, "เข้าสู่ระบบ", "กำลังเข้าสู่ระบบ...", true, false);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("result", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("result", "signInWithCredential:failure", task.getException());

                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        progressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        progressDialog.dismiss();
        if (user!= null) {


            for (int i = 0;i<6;i++) {
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +"/" + "egg1_" + i).setValue(980);
            }

            for (int i = 0;i<6;i++) {
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +"/" + "egg2_" + i).setValue(980);
            }
            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/ml").setValue(600);

            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/device/led_control").setValue(1);
            AnimationFade();



        } else {

            signIn();
            Toast.makeText(getApplicationContext(), "User result : null",Toast.LENGTH_LONG).show();

        }
    }


    public boolean CheckInternet() {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }
        return connected;
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void AnimationFade() {

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(imLogo, View.ALPHA, 0f);
        anim2.setDuration(3400);
        anim2.start();


        //Delay
        Runnable Delay = new Runnable() {
            @Override
            public void run() {

               startActivity(new Intent(BeginActivity.this, MainActivity.class));
               finish();

            }
        };

        Handler pd = new Handler();
        pd.postDelayed(Delay, 3000);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "ออกจากระบบแล้ว", Toast.LENGTH_LONG).show();
                    }
                });
    }


}
