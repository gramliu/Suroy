package com.dyip.suroy.rider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dyip.suroy.rider.utility.Utility;
import com.dyip.suroy.rider.utility.Utility_Animation;
import com.dyip.suroy.rider.utility.Utility_Auth;
import com.dyip.suroy.rider.utility.Utility_Startup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Utility_Startup.initPermissions(this);
        Utility_Startup.initStartup(this);
        Utility_Animation.animateStartup(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Constants.fireUser = Constants.auth.getCurrentUser();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == Constants.PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utility.log("MainActivity", "Successfully granted permissions!");
            } else {
                Toast.makeText(this, "Could not retrieve permissions!", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SIGN_IN_CODE) {
            Utility_Auth.login(this, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (Constants.exitPrompt) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            Constants.exitPrompt = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit app?")
                    .setMessage("Are you sure you want to exit the app?")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Constants.exitPrompt = false;
                }
            }).show();
        }
    }

}
