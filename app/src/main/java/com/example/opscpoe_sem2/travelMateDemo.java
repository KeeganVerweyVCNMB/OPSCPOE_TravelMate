package com.example.opscpoe_sem2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class travelMateDemo extends TutorialActivity {

    //github.com. 2021. How to create a tutorials view in Android Studio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting fragment data and building screen for welcome user
        addFragment(new Step.Builder().setTitle("Welcome to Travel Mate")
                .setContent("View your local maps, share waypoints, find nearby locations and many more. \n\n" +
                        "Sign up and become a member \n\n Travel Mate got you covered.")
                .setBackgroundColor(Color.parseColor("#7fa2ff"))
                .setDrawable(R.drawable.welcome_travel_mate)
                .setSummary("Welcome to Travel Mate!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate Main Map")
                .setContent("Search and find nearby locations.")
                .setBackgroundColor(Color.parseColor("#319f4f"))
                .setDrawable(R.drawable.maps_view)
                .setSummary("See some awesome realtime locations!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate Plan Routes")
                .setContent("Plan new trips and save them to your favourite routes.")
                .setBackgroundColor(Color.parseColor("#eeb62b"))
                .setDrawable(R.drawable.maps_routes)
                .setSummary("Plan a new trip!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate Share Waypoints")
                .setContent("Search, find and share nearby locations with your friends.")
                .setBackgroundColor(Color.parseColor("#008ce2"))
                .setDrawable(R.drawable.map_share)
                .setSummary("Share waypoints with friends!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate Saved Routes")
                .setContent("Re-live your most favourite routes and waypoints.")
                .setBackgroundColor(Color.parseColor("#20a361"))
                .setDrawable(R.drawable.map_save)
                .setSummary("View your saved routes or take the same trip!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate SOS")
                .setContent("Request a SOS if you're in trouble. Travel Mate got you covered.")
                .setBackgroundColor(Color.parseColor("#df3528"))
                .setDrawable(R.drawable.map_sos)
                .setSummary("Request a SOS!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate Profile")
                .setContent("Update your profile and keep your account fresh.")
                .setBackgroundColor(Color.parseColor("#008ecc"))
                .setDrawable(R.drawable.profile_map)
                .setSummary("Keep your profile up to date!")
                .build());

        addFragment(new Step.Builder().setTitle("Travel Mate Settings")
                .setContent("Apply your favourite and preferred settings to your Travel Mate account.")
                .setBackgroundColor(Color.parseColor("#f16449"))
                .setDrawable(R.drawable.settings_map)
                .setSummary("Choose your preferred settings!")
                .build());
    }

    //Start new activity after ending user on-boarding
    @Override
    public void finishTutorial() {
        startActivity(new Intent(this, login.class));
    }

    @Override
    public void currentFragmentPosition(int position) {

    }
}
