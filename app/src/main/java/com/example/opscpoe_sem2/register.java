package com.example.opscpoe_sem2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class register extends AppCompatActivity {
    //Setting Variables
    FirebaseAuth fireBAuth;
    EditText rgstName, rgstSurname, rgstEmail, rgstPassword, rgstAddress;
    TextView tvSignIn;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //Forgot Password navigation
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(register.this, login.class));
            }
        });

        //Setting UI into variables
        fireBAuth = FirebaseAuth.getInstance();
        rgstName = findViewById(R.id.txtName);
        rgstSurname = findViewById(R.id.txtSurname);
        rgstEmail = findViewById(R.id.txtEmail);
        rgstAddress = findViewById(R.id.txtAddress);
        rgstPassword = findViewById(R.id.txtPassword);

        final Button btnConfirmRegister = (Button) findViewById(R.id.btnConfirmRegister);
        btnConfirmRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = rgstName.getText().toString();
                String surname = rgstSurname.getText().toString();
                String email = rgstEmail.getText().toString();
                String address = rgstAddress.getText().toString();
                String password = rgstPassword.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                //Validation for registration
                if(TextUtils.isEmpty(name)) {
                    rgstName.setError("Name is a required field");
                    return;
                }
                else if(TextUtils.isEmpty(surname)) {
                    rgstSurname.setError("Surname is a required field");
                    return;
                }
                else if(TextUtils.isEmpty(email)) {
                    rgstEmail.setError("Email is a required field");
                    return;
                }
                else if(TextUtils.isEmpty(address)) {
                    rgstAddress.setError("Address is a required field");
                    return;
                }
                else if(!email.matches(emailPattern)) {
                    rgstEmail.setError("Please enter a valid email address");
                    return;
                }
                else if(password.length() < 7) {
                    rgstPassword.setError("Password must be 8 or more characters");
                    return;
                }
                else {
                    registerNewUser(email, password);

                    //Generating random number to match categories to items(CatID)
                    int max = 1000000;
                    int min = 0;

                    Random randomNum = new Random();
                    int generatedID = min + randomNum.nextInt(max);

                    String userID = "UserID-" + generatedID;

                    String passwordToHash = password;
                    String generatedPassword = null;
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        md.update(passwordToHash.getBytes());
                        byte[] bytes = md.digest();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i< bytes.length ;i++)
                        {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        generatedPassword = sb.toString();
                    }
                    catch (NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).setValue(userID);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Name").setValue(name);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Surname").setValue(surname);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Address").setValue(address);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Email").setValue(email);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Password").setValue(generatedPassword);
                }

            }
        });
    }

    //Firebase class for registering new user with Firebase Authentication (Blog.mindorks.com. 2021.)
    private void registerNewUser(String email, String password) {
        try {
            fireBAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(register.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(register.this, login.class));
                    }
                    else {
                        Toast.makeText(register.this, "Error Registering", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            //Do Nothing
        }
    }
}