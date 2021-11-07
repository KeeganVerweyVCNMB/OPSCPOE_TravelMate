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
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import java.io.IOException;
import java.util.List;

public class servicesMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView searchView;
    ProgressBar progressBarMapServices;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    List<Address> addressList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_map);

        progressBarMapServices = (ProgressBar) findViewById(R.id.progressBarMapService);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarMapServices.setVisibility(RelativeLayout.VISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Services Centres");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = findViewById(R.id.idSearchViewService);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                if (location != null || location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(servicesMap.this);
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
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Service Centre not Found", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Service Centre not Found", Toast.LENGTH_LONG).show();
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
                    supportMapFragment.getMapAsync(servicesMap.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBarMapServices.setVisibility(RelativeLayout.INVISIBLE);
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
        PlacesSearchResult[] repairSearchResults = new servicesMap.nearbyCarRepair().run().results;

        if (repairSearchResults.length > 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            mMap.addMarker(markerOptions);

            for (int i = 0; i < repairSearchResults.length; i++) {
                double lat1 = repairSearchResults[i].geometry.location.lat;
                double lng1 = repairSearchResults[i].geometry.location.lng;

                mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(repairSearchResults[i].name));
                mMap.setMinZoomPreference(14.0f);
            }

            Toast.makeText(getApplicationContext(), "Nearest Service Centre: " + repairSearchResults[0].name, Toast.LENGTH_LONG).show();
            LatLng latLngCarRep = new LatLng(repairSearchResults[0].geometry.location.lat, repairSearchResults[0].geometry.location.lng);

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngCarRep));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCarRep, 5));

        }
        else {
            Toast.makeText(getApplicationContext(), "No Nearby Service Centres \n Feel free to manual search", Toast.LENGTH_LONG).show();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            mMap.addMarker(markerOptions);
        }

    }

    public class nearbyCarRepair {
        public PlacesSearchResponse run() {

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.CAR_REPAIR)
                        .await();
            } catch (ApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                return request;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.services_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

