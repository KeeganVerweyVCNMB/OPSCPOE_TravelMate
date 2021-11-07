package com.example.opscpoe_sem2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {
    //Setting Variables
    Button sendEmail;
    EditText fpMail;
    FirebaseAuth fireBAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting UI into variables
        sendEmail = (Button) findViewById(R.id.btnForgotPassword);
        fpMail = (EditText) findViewById(R.id.txtFPEmail);
        fireBAuth = FirebaseAuth.getInstance();

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Reggex email validation
                String email = fpMail.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                //Validations
                if (TextUtils.isEmpty(email)) {
                    fpMail.setError("Email is a required field");
                    return;
                }
                else if(!email.matches(emailPattern)) {
                    fpMail.setError("Please enter a valid email address");
                    return;
                }

                //Firebase password reset method (Blog.mindorks.com. 2021.)
                fireBAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            fpMail.setText("");
                            Toast.makeText(forgotPassword.this, "Password reset instructions sent", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(forgotPassword.this, "Failed to send email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}


