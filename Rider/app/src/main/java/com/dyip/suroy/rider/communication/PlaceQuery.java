package com.dyip.suroy.rider.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import com.dyip.suroy.rider.Constants;
import com.dyip.suroy.rider.model.Place;
import com.dyip.suroy.rider.utility.Utility;
import com.dyip.suroy.rider.utility.Utility_Map;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PlaceQuery {

    @SuppressLint("DefaultLocale")
    public static void getNearbyPlaces(final Activity activity) {
        if (Constants.getThread != null) {
            Constants.getThread.interrupt();
        }
        Constants.getThread = new Thread() {
            @Override
            public void run() {

                try {
                    Utility.log("PlaceQuery", "Running GET thread!");
                    Location loc = Utility_Map.getLocation(activity);
                    URL restoUrl = new URL(String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=1500&type=restaurant&key=AIzaSyCvC1Z1tc2FjoGDa7FUyzON6LsGoW1i0Rw", loc.getLatitude(), loc.getLongitude()));
                    URL poiUrl = new URL(String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=1500&type=museum&key=AIzaSyCvC1Z1tc2FjoGDa7FUyzON6LsGoW1i0Rw", loc.getLatitude(), loc.getLongitude()));
                    URL proxUrl = new URL(String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=100&type=point_of_interest&key=AIzaSyCvC1Z1tc2FjoGDa7FUyzON6LsGoW1i0Rw", loc.getLatitude(), loc.getLongitude()));

                    String restoData = Utility.sendGET(restoUrl);
                    String poiData = Utility.sendGET(poiUrl);
                    String proxData = Utility.sendGET(proxUrl);

                    Constants.places = loadPlaces(restoData);
                    ArrayList<Place> pois = loadPlaces(poiData);
                    Constants.places.addAll(pois);

                    ArrayList<Place> prox = loadPlaces(proxData);
                    Constants.user.visitPlace(prox);

                    Utility_Map.loadMarkers(activity);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        Constants.getThread.start();

    }

    public static ArrayList<Place> loadPlaces(String data) {

        ArrayList<Place> places = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(data);
            JSONArray results = root.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject rawPlace = results.getJSONObject(i);
                JSONObject viewport = rawPlace.getJSONObject("geometry").getJSONObject("viewport");

                JSONObject jne = viewport.getJSONObject("northeast");
                JSONObject jsw = viewport.getJSONObject("southwest");

                String name = rawPlace.getString("name");

                LatLng ne = new LatLng(jne.getDouble("lat"), jne.getDouble("lng"));
                LatLng sw = new LatLng(jsw.getDouble("lat"), jne.getDouble("lng"));

                JSONObject jloc = rawPlace.getJSONObject("geometry").getJSONObject("location");
                LatLng coord = new LatLng(jloc.getDouble("lat"), jloc.getDouble("lng"));

                String id = rawPlace.getString("place_id");
                String photo_id = null;
                if (rawPlace.has("photos")) {
                    photo_id = rawPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                }

                JSONArray types = rawPlace.getJSONArray("types");
                int flag = 0b00;
                for (int j = 0; j < types.length(); j++) {
                    String type = types.getString(j);
                    if (type.equals("museum")) {
                        flag |= 0b01;
                    } else if (type.equals("restaurant")) {
                        flag |= 0b10;
                    }
                }
                if (flag == 1) {
                    Place place = new Place(name, id, photo_id, ne, sw, coord, Constants.PLACE_TYPE_POI);
                    places.add(place);
                } else if (flag > 1) {
                    Place place = new Place(name, id, photo_id, ne, sw, coord, Constants.PLACE_TYPE_RESTAURANT);
                    places.add(place);
                }

            }
        } catch (JSONException e) {
            Utility.log("PlaceQuery", "Error occured parsing json\n" + data);
            e.printStackTrace();
        }
        return places;

    }

}
