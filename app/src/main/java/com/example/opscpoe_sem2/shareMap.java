package com.example.opscpoe_sem2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class shareMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView searchView;
    ProgressBar progressBarMapShare;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    List<Address> addressList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_map);

        progressBarMapShare = (ProgressBar) findViewById(R.id.progressBarMapShare);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarMapShare.setVisibility(RelativeLayout.VISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Share Waypoint");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = findViewById(R.id.idSearchViewShare);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                if (location != null || location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(shareMap.this);
                    try
                    {
                        addressList = geocoder.getFromLocationName(location, 1);

                        if(addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);

                            if(address != null)
                            {
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                String uri = "http://maps.google.com/maps?saddr=" + address.getLatitude() +"," + address.getLongitude();
                                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                sharingIntent.setType("text/plain");
                                                String ShareSub = "Here is my location - Travel Mate";
                                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, ShareSub);
                                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri );
                                                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(shareMap.this);
                                builder.setMessage("Share this location?").setPositiveButton("Confirm", dialogClickListener)
                                        .setNegativeButton("Cancel", dialogClickListener).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Location not Found", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Location not Found", Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }

        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapShare);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(shareMap.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBarMapShare.setVisibility(RelativeLayout.INVISIBLE);
        mMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btnShareThisMap) {

            String uri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() +"," + currentLocation.getLongitude();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = "Here is my location - Travel Mate";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri );
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }
}
