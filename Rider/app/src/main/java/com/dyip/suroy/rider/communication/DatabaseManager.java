package com.dyip.suroy.rider.communication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dyip.suroy.rider.Constants;
import com.dyip.suroy.rider.model.SuroyUser;
import com.dyip.suroy.rider.utility.Utility;
import com.dyip.suroy.rider.utility.Utility_Animation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;

/**
 * Utility class for handling incoming/outgoing connections to Firebase
 */
public class DatabaseManager {

    public static void validateUID(final Activity activity, String email) {

        final String hash = Utility.md5(email);
        Utility.log("DatabaseManager", "MD5: " + hash);
        DatabaseReference ref = Constants.firebase.getReference("users").child("riders").child(hash);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Only run on startup
                if (Constants.user == null) {
                    if (dataSnapshot.exists()) {
                        Constants.newUser = false;
                        String name = dataSnapshot.child("display_name").getValue(String.class);
                        DataSnapshot visited = dataSnapshot.child("visited");
                        HashSet<String> set = new HashSet<>();
                        for (DataSnapshot child : visited.getChildren()) {
                            set.add(child.getValue(String.class));
                        }
                        Constants.user = new SuroyUser(hash, name, set);
                        Utility.log("DBManager", "Name: " + name);
                    } else {
//                        TODO: Registration for display name
                        Constants.user = new SuroyUser(Utility.md5(Constants.fireUser.getEmail()), Constants.fireUser.getDisplayName(), new HashSet<String>());
                        Constants.newUser = true;
                    }
                    Utility_Animation.animateLoginSuccess(activity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
