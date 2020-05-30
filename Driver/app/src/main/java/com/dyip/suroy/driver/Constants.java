package com.dyip.suroy.driver;

import android.bluetooth.BluetoothAdapter;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;

import com.dyip.suroy.driver.model.SuroyUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.util.UUID;

public class Constants {

    public static final int PERMISSION_CODE = 1;
    public static final int SIGN_IN_CODE = 2;
    public static final int REQUEST_BT = 2;

    public static FirebaseDatabase firebase;
    public static FirebaseAuth auth;
    public static FirebaseUser fireUser;
    public static SuroyUser user;

    public static GoogleSignInClient client;
    public static boolean validatedUID;

    public static GoogleMap map;
    public static FloatingActionButton center_button;
    public static String provider = null;
    public static LocationManager locationManager;

    public static BluetoothAdapter adapter;
    public static BufferedReader btReader;
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String address;
    public static Thread listenThread;

    public static int passengerCount = 0;

    public static boolean exitPrompt = false;
    public static boolean updatingLocation = false;

}
