package com.dyip.suroy.driver.communication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.R;
import com.dyip.suroy.driver.utility.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConnectThread extends Thread {

    private final BluetoothSocket socket;

    private Activity activity;

    public ConnectThread(BluetoothDevice device, Activity activity) {
        BluetoothSocket tmp = null;

        try {
            Utility.log("ConnectThread", "Starting device creation");
            tmp = device.createRfcommSocketToServiceRecord(Constants.uuid);
            Utility.log("ConnectThread", "Device created!");
        } catch (IOException e) {
            Utility.log("ConnectThread", "Socket's create() method failed");
            e.printStackTrace();
        }
        socket = tmp;
        this.activity = activity;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        Utility.log("ConnectThread", "ConnectThread started!");
        Constants.adapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Utility.log("ConnectThread", "Connecting to socket.");
            socket.connect();
            Utility.log("ConnectThread", "Socket connected!");
        } catch (IOException e) {
            // Unable to connect; close the socket and return.
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Could not connect to device!", Toast.LENGTH_SHORT).show();
                }
            });
            Utility.log("ConnectThread","IOException e: Could not connect to device!");
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                Utility.log("ConnectThread", "IOException e1: Could not close the client socket");
                e1.printStackTrace();
            }
            return;
        }

        try {
            Constants.btReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(activity.getString(R.string.pref_address_cache), socket.getRemoteDevice().getAddress());
            editor.apply();
            CubeCommunicator.startListening(activity);
            Utility.log("ConnectThread", "Successfully established BT connection!");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Connected to Cube!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Utility.log("ConnectThread", "Could not close the client socket");
            e.printStackTrace();
        }
    }

}
