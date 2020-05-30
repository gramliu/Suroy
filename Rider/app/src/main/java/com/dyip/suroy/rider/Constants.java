package com.dyip.suroy.rider;

import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

import com.dyip.suroy.rider.model.Place;
import com.dyip.suroy.rider.model.SuroyUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Constants {

    public static final int PERMISSION_CODE = 1;
    public static final int SIGN_IN_CODE = 2;

    public static FirebaseDatabase firebase;
    public static FirebaseAuth auth;
    public static FirebaseUser fireUser;
    public static SuroyUser user = null;

    public static GoogleSignInClient client;
    public static boolean newUser = true;

    public static GoogleMap map;
    public static FloatingActionButton center_button;
    public static String provider = null;
    public static LocationManager locationManager;

    public static Thread getThread;
    public static Thread routeThread;
    public static Polyline polyline;
    public static List<LatLng> polyCoords;

    public static ArrayList<Place> places = new ArrayList<>();
    public static HashMap<String, Marker> jeeps = new HashMap<>();
    public static ArrayList<Marker> oldMarkers = new ArrayList<>();

    public static TextView visited;

    public static boolean exitPrompt = false;
    public static int clickedNavItem = 0;

    public static final int PLACE_TYPE_POI = 1;
    public static final int PLACE_TYPE_RESTAURANT = 2;

    public static final boolean DEBUG = true;

}
