package com.dyip.suroy.rider.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;

import com.dyip.suroy.rider.Constants;
import com.dyip.suroy.rider.utility.Utility;
import com.dyip.suroy.rider.utility.Utility_Map;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class JeepQuery {

    public static void initJeepListener(final Activity activity) {

        DatabaseReference ref = Constants.firebase.getReference("users").child("drivers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String raw = child.child("location").getValue(String.class);
                    if (raw == null) {
                        continue;
                    }
                    String[] rloc = raw.split(",");
                    final double lat = Double.parseDouble(rloc[0]);
                    final double lng = Double.parseDouble(rloc[1]);

                    final int passenger_count = child.child("passenger_count").getValue(int.class);
                    final String plate = child.child("plate_number").getValue(String.class);
                    final int route_id = child.child("route_id").getValue(int.class);

                    final String uid = child.getKey();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            LatLng loc = new LatLng(lat, lng);
                            if (Constants.jeeps.containsKey(uid)) {
                                Utility.log("JQ", "Existing jeep");
                                Marker marker = Constants.jeeps.get(uid);
                                marker.setIcon(
                                        BitmapDescriptorFactory.fromBitmap(Utility.createStoreMarker(activity, passenger_count))
                                );
                                Utility.animateMarker(marker, loc, false);
                                Utility.log("JQ", "Done Existing jeep");
                            } else {
                                Utility.log("JQ", "New jeep");
                                Marker marker = Constants.map.addMarker(new MarkerOptions()
                                        .position(loc)
                                        .title(Utility.translateRouteCode(route_id))
                                        .snippet(plate)
                                        .icon(BitmapDescriptorFactory.fromBitmap(Utility.createStoreMarker(activity, passenger_count)))
                                );
                                Constants.jeeps.put(uid, marker);
                                Utility.log("JQ", "Done New jeep");
                            }

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("DefaultLocale")
    public static void queryRoute(final Activity activity, final String destination) {

        if (Constants.routeThread != null) {
            Constants.routeThread.interrupt();
        }

        Utility.log("JeepQuery", "Going to: " + destination);
        Constants.routeThread = new Thread() {
            @Override
            public void run() {
                try {
                    Location loc = Utility_Map.getLocation(activity);
                    URL url = new URL(String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%s&key=AIzaSyCvC1Z1tc2FjoGDa7FUyzON6LsGoW1i0Rw&transit_mode=rail", loc.getLatitude(), loc.getLongitude(), destination));
                    String data = Utility.sendGET(url);

                    JSONObject root = new JSONObject(data);
                    String enc = root.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                    Constants.polyCoords = Utility.decodePoly(enc);

                    final PolylineOptions lineOptions = new PolylineOptions();

                    lineOptions.addAll(Constants.polyCoords);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Constants.polyline != null) {
                                Constants.polyline.remove();
                            }
                            Constants.polyline = Constants.map.addPolyline(lineOptions);
                            new AlertDialog.Builder(activity).setTitle("Route")
                                    .setMessage("Take a jeep from Blumentritt to Baclaran")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null).show();
                        }
                    });

                } catch (MalformedURLException | JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Constants.routeThread.start();


    }


}
