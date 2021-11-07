package com.example.opscpoe_sem2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class savedRoutes extends AppCompatActivity {
    private ListView savedRoutesListView;
    DatabaseReference dbRef;
    DatabaseReference fRef;
    String emailLocally;
    String DestinationSavedTitle[];
    ArrayList<String> arrayLstRoutes = new ArrayList<>();
    ArrayAdapter<String> arrayAdptRoutes;
    TextView inputSearch;
    ProgressBar progressBarSaved;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_route);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Saved Routes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBarSaved = (ProgressBar) findViewById(R.id.progressBarSaved);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarSaved.setVisibility(RelativeLayout.VISIBLE);

        SharedPreferences settingsMail = getApplicationContext().getSharedPreferences("Email", 0);
        emailLocally = settingsMail.getString("Email", "email");

        inputSearch = (EditText) findViewById(R.id.itemSearchSavedRoutes);
        if(arrayLstRoutes != null) {
            inputSearch.setVisibility(View.VISIBLE);
            arrayAdptRoutes = new ArrayAdapter<String>(savedRoutes.this, R.layout.savedroutes_listsearch, R.id.routesSaved_name, arrayLstRoutes);
            savedRoutesListView = (ListView) findViewById(R.id.editListViewSavedRoutes);
            savedRoutesListView.setAdapter(arrayAdptRoutes);
        }
        else {
            inputSearch.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "No saved routes!", Toast.LENGTH_LONG).show();
        }

        //Pushing CatCode to wbf_stock_cat_m class
        savedRoutesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settingsTitle = getApplicationContext().getSharedPreferences("Title", 0);
                SharedPreferences.Editor editorTitle = settingsTitle.edit();
                editorTitle.putString("Title", DestinationSavedTitle[position]);
                editorTitle.apply();

                startActivity(new Intent(savedRoutes.this, goToSavedMap.class));
            }
        });


        dbRef = FirebaseDatabase.getInstance().getReference();
        fRef = dbRef.child("SavedMap");
        Query queryAttr = fRef.orderByChild("Email").equalTo(emailLocally);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DestinationSavedTitle = new String[(int)dataSnapshot.getChildrenCount()];
                int index = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map mapStock = new HashMap();
                    mapStock = (HashMap) ds.getValue();
                    DestinationSavedTitle[index] = mapStock.get("DestinationTitle").toString();

                    arrayLstRoutes.add(DestinationSavedTitle[index]);
                    index++;
                }
                progressBarSaved.setVisibility(RelativeLayout.INVISIBLE);
                arrayAdptRoutes.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        queryAttr.addListenerForSingleValueEvent(valueEventListener);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                savedRoutes.this.arrayAdptRoutes.getFilter().filter(cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

    }
}

