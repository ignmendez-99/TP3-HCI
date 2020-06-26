package com.example.ultrahome;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppBarConfiguration appBarConfiguration;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        if(navView == null) {
            // we are in tablet mode
            navView = findViewById(R.id.nav_view_tablet);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_homes_tablet, R.id.navigation_routines_tablet, R.id.navigation_profile_tablet)
                    .build();
        } else {
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_homes, R.id.navigation_routines, R.id.navigation_profile)
                    .build();
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
