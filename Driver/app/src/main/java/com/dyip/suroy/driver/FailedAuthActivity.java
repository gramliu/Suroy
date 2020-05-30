package com.dyip.suroy.driver;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FailedAuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_auth);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
