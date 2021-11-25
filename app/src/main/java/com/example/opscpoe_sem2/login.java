package com.example.opscpoe_sem2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    //Setting Variables
    FirebaseAuth fireBAuth;
    EditText lgnEmail, lgnPassword;
    ProgressBar progressBarLogin, progressBarSOSTimer;
    TextView tvFogotPass, tvRegister, tvSOS;
    double longitude;
    double latitude;
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Forgot Password navigation
        tvFogotPass = (TextView) findViewById(R.id.tvForgotPassword);
        tvFogotPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(login.this, forgotPassword.class));
            }
        });

        //Register navigation
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(login.this, register.class));
            }
        });

        progressBarSOSTimer = (ProgressBar)findViewById(R.id.progressBarSOSTime);
        progressBarLogin = (ProgressBar)findViewById(R.id.progressBarLogin);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarLogin.setVisibility(RelativeLayout.INVISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();
        //Checking if device is connected to WIFI or Mobile Data
        //stackoverflow.com. 2021. How to check currently internet connection is available or not in android: Android Tutorial.
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            //Do Nothing
        }
        else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }

        fireBAuth = FirebaseAuth.getInstance();
        lgnEmail = findViewById(R.id.txtLgnEmail);
        lgnPassword = findViewById(R.id.txtLgnPass);

        //Button login validation
        final Button loginToDash = (Button) findViewById(R.id.btnLoginToDash);
        loginToDash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = lgnEmail.getText().toString();
                String password = lgnPassword.getText().toString();

                //Validation
                if(TextUtils.isEmpty(email)) {
                    lgnEmail.setError("Email is a required field");
                    return;
                }
                else if(lgnPassword.length() < 7) {
                    lgnPassword.setError("Password must be 8 or more characters");
                    return;
                }
                //If validation succeeds, call login user class and log user into system
                else {
                    //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
                    progressBarLogin.setVisibility(RelativeLayout.VISIBLE);
                    loginUser(email, password);
                }
            }
        });

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        tvSOS = (TextView) findViewById(R.id.tvRequestSOS);
        tvSOS.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //when button is pressed
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startTimer();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    progressBarSOSTimer.setProgress(0);
                    cancelTimer();
                }
                return true;
            }
        });
    }

    void startTimer() {
        cTimer = new CountDownTimer(2000, 200) {
            public void onTick(long millisUntilFinished) {
                progressBarSOSTimer.setProgress(progressBarSOSTimer.getProgress() + 10);
            }
            public void onFinish() {
                String number="066 545 6666";

                Intent intent4=new Intent(Intent.ACTION_CALL);
                intent4.setData(Uri.parse("tel:"+number));
                startActivity(intent4);

                progressBarSOSTimer.setProgress(0);
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    //Firebase class for signing user into system (Blog.mindorks.com. 2021.)
    private void loginUser(String email, String password) {
        try {
            fireBAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Logged in as: " + email.toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(login.this, dashboard.class);
                        Bundle bm = new Bundle();
                        bm.putString("Email", email);
                        bm.putString("Password", password);
                        intent.putExtras(bm);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(login.this, "Username or Password Incorrect", Toast.LENGTH_LONG).show();
                    }
                    //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
                    progressBarLogin.setVisibility(RelativeLayout.INVISIBLE);
                }
            });
        }
        catch (Exception e) {
            //Do Nothing
        }
    }

}
