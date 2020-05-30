package com.dyip.suroy.driver.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.R;
import com.dyip.suroy.driver.communication.CubeCommunicator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling activity startup and initialization
 */
public class Utility_Startup {

    public static void initPermissions(Activity activity) {

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };

        List<String> perms = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                perms.add(permission);
            }
        }

        if (perms.size() > 0) {
            ActivityCompat.requestPermissions(activity, perms.toArray(new String[0]), Constants.PERMISSION_CODE);
        }

    }

    public static void initStartup(final Activity activity) {
        Constants.firebase = FirebaseDatabase.getInstance();
        Constants.auth = FirebaseAuth.getInstance();
        CubeCommunicator.setupBT(activity);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(activity.getString(R.string.default_web_client_id)).
                requestEmail().build();
        Constants.client = GoogleSignIn.getClient(activity, gso);

        activity.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Constants.client.getSignInIntent();
                activity.startActivityForResult(intent, Constants.SIGN_IN_CODE);
            }
        });

    }

}
