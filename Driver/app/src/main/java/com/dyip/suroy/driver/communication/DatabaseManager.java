package com.dyip.suroy.driver.communication;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.model.SuroyUser;
import com.dyip.suroy.driver.utility.Utility;
import com.dyip.suroy.driver.utility.Utility_Animation;
import com.dyip.suroy.driver.utility.Utility_Map;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * Utility class for handling incoming/outgoing connections to Firebase
 */
public class DatabaseManager {

    public static void validateUID(final Activity activity, final String email) {

        final String hash = Utility.md5(email);
        Utility.log("DatabaseManager", "Hash: " + hash);
        DatabaseReference ref = Constants.firebase.getReference("users").child("drivers").child(hash);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                only on startup
                if (Constants.user == null) {
                    if (dataSnapshot.exists()) {
                        Constants.validatedUID = true;
                        String fname = dataSnapshot.child("first_name").getValue(String.class);
                        String lname = dataSnapshot.child("last_name").getValue(String.class);

                        String plate_number = dataSnapshot.child("plate_number").getValue(String.class);
                        int route_code = dataSnapshot.child("route_id").getValue(int.class);

                        Constants.user = new SuroyUser(hash, fname + " " + lname, plate_number, route_code);
                    } else {
                        Constants.validatedUID = false;
                    }
                    Utility_Animation.animateLoginSuccess(activity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public static void updatePassengerCount(int i) {
        DatabaseReference ref = Constants.firebase.getReference("users").child("drivers").child(Constants.user.getHash()).child("passenger_count");
        ref.setValue(i);
    }

}
