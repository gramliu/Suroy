package com.dyip.suroy.rider.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dyip.suroy.rider.Constants;
import com.dyip.suroy.rider.MainActivity;
import com.dyip.suroy.rider.R;
import com.dyip.suroy.rider.SettingsActivity;
import com.dyip.suroy.rider.communication.PlaceQuery;
import com.dyip.suroy.rider.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Utility_Map {

    public static void loadMarkers(final Activity activity) {

        int flags = 0b00; // sites, restaurants
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        flags |= pref.getBoolean("restaurants", true) ? 0b10 : 0;
        flags |= pref.getBoolean("attractions", true) ? 0b01 : 0;

        final int flag = flags;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Marker marker : Constants.oldMarkers) {
                    marker.remove();
                }
                Constants.oldMarkers.clear();
                for (Place place : Constants.places) {
                    if ((flag & 0b10) == 0 && place.getType() == Constants.PLACE_TYPE_RESTAURANT) {
                        continue;
                    } else if ((flag & 0b01) == 0 && place.getType() == Constants.PLACE_TYPE_POI) {
                        continue;
                    }
                    LatLng coords = place.getCoords();
                    Marker marker = Constants.map.addMarker(new MarkerOptions()
                            .position(coords)
                            .title(place.getName())
                            .icon(place.getType() == Constants.PLACE_TYPE_POI
                            ? BitmapDescriptorFactory.fromResource(R.mipmap.marker_sights)
                            : BitmapDescriptorFactory.fromResource(R.mipmap.marker_restaurants))
                    );
                    Constants.oldMarkers.add(marker);
                }
            }
        });

    }

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

    @SuppressLint("MissingPermission")
    public static void attachLocationListener(final Activity activity) {
        if (Constants.locationManager == null) {
            Constants.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }
        if (Constants.provider == null) {
            getLocation(activity);
        }
        Constants.locationManager.requestLocationUpdates(Constants.provider, 1000, 25, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                PlaceQuery.getNearbyPlaces(activity);
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
    public static void initMap(final Activity activity) {

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
                Location updated = getLocation(activity);
                LatLng lUp = new LatLng(updated.getLatitude(), updated.getLongitude());
                Utility.log("Maps", "LatLng: " + lUp.toString());
                Constants.map.animateCamera(CameraUpdateFactory.newLatLngZoom(lUp, 15.0f), null);

                PlaceQuery.getNearbyPlaces(activity);
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
                Constants.clickedNavItem = item.getItemId();
                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, R.string.accessibility_open_nav, R.string.accessibility_open_nav) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (Constants.clickedNavItem == R.id.menu_sign_out) {
                    Utility_Auth.logout();
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                } else if (Constants.clickedNavItem == R.id.menu_settings) {
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    activity.startActivity(intent);
                }

            }
        };
        drawer.addDrawerListener(toggle);

        View header = navView.getHeaderView(0);
        TextView textView = header.findViewById(R.id.nav_display_name);
        textView.setText(Constants.user.getDisplayName());

        Constants.visited = header.findViewById(R.id.nav_places_visited);
        Constants.visited.setText("Places visited: " + Constants.user.getVisitCount());

    }

}
