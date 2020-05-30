package com.dyip.suroy.driver.utility;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.communication.DatabaseManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Utility class for handling authentication requests
 */
public class Utility_Auth {

    public static void login(Activity activity, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(activity, account);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Utility.log("Sign in", "Failed in login!");
            Toast.makeText(activity, "Failed to login!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void logout() {

        Constants.auth.signOut();
        Constants.client.signOut();
        Constants.fireUser = null;

    }

    public static void validateLoginFirebase(Activity activity) {
        DatabaseManager.validateUID(activity, Constants.fireUser.getEmail());
    }

    private static void firebaseAuthWithGoogle(final Activity activity, final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Constants.auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Utility.log("Sign in", "Success!");
                            Constants.fireUser = Constants.auth.getCurrentUser();
                            validateLoginFirebase(activity);
                            Utility.log("Login", "ID: " + Constants.fireUser.getUid());
                        } else {
                            Utility.log("Sign in", "Failed!");
                            Toast.makeText(activity, "Failed to sign in!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
