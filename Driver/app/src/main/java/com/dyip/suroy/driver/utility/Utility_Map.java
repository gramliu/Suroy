package com.dyip.suroy.driver.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.MainActivity;
import com.dyip.suroy.driver.R;
import com.dyip.suroy.driver.communication.CubeCommunicator;
import com.dyip.suroy.driver.communication.DatabaseManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Locale;

public class Utility_Map {

    @SuppressLint("MissingPermission")
    public static Location getLocation(Activity activity) {
        if (Constants.locationManager == null) {
            Constants.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }
        List<String> providers = Constants.locationManager.getProviders(true);
        Location loc = null;
        if (Constants.provider == null) {
            for (String provider : providers) {
                Location l = Constants.locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (loc == null || l.getAccuracy() < loc.getAccuracy()) {
                    loc = l;
                    Constants.provider = provider;
                }
            }
        } else {
            loc = Constants.locationManager.getLastKnownLocation(Constants.provider);
        }
        return loc;
    }

    public static void updateCloudLocation(Location location) {
        String str = String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());

        DatabaseReference ref = Constants.firebase.getReference().child("users").child("drivers").child(Constants.user.getHash()).child("location");
        ref.setValue(str);
        Utility.log("UpdateLocation", "Updated location: " + str);
    }

    @SuppressLint("MissingPermission")
    public static void attachLocationListener(Activity activity) {
        if (Constants.locationManager == null) {
            Constants.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }
        if (Constants.provider == null) {
            getLocation(activity);
        }
        Constants.locationManager.requestLocationUpdates(Constants.provider, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCloudLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        });

    }

    @SuppressLint("MissingPermission")
    public static void initMap(Activity activity) {

        Location loc = getLocation(activity);

        final LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
        Constants.map.moveCamera(CameraUpdateFactory.newLatLng(current));
        Constants.map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
        Constants.map.setMyLocationEnabled(true);
        Constants.map.getUiSettings().setMyLocationButtonEnabled(false);

        Constants.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style));

        Constants.center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.log("Maps", "LatLng: " + current.toString());
                Constants.map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f), null);
            }
        });

    }

    public static void initMapGUI(final AppCompatActivity activity) {

        Constants.center_button = activity.findViewById(R.id.center_button);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        } else {
            Utility.log("Utility_Map::initGUI", "ActionBar is null!");
        }

        NavigationView navView = activity.findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);

        final DrawerLayout drawer = activity.findViewById(R.id.maps_drawer);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(Gravity.START);
                int id = item.getItemId();
                if (id == R.id.menu_sign_out) {
                    Utility_Auth.logout();
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                } else if (id == R.id.menu_bluetooth) {
                    CubeCommunicator.displayPairedDevicesList(activity);
                }
                return true;
            }
        });

        View header = navView.getHeaderView(0);
        TextView textView = header.findViewById(R.id.nav_display_name);
        textView.setText(Constants.user.getDisplayName());

        TextView plate = header.findViewById(R.id.nav_plate_number);
        plate.setText(Constants.user.getPlateNumber());

    }

    public static void updatePassengerCount(final Activity activity, final int i) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView passengerCount = activity.findViewById(R.id.labelPassengerCount);
                passengerCount.setText(Utility.padInt(i, 2));
            }
        });

        DatabaseManager.updatePassengerCount(i);

    }

}
