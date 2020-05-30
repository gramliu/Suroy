package com.dyip.suroy.driver.communication;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.R;
import com.dyip.suroy.driver.utility.Utility;
import com.dyip.suroy.driver.utility.Utility_Map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Utility class for communicating with the Suroy Cube device on the jeepney
 */
public class CubeCommunicator {

    public static void setupBT(Activity activity) {

        Constants.adapter = BluetoothAdapter.getDefaultAdapter();
        if (Constants.adapter == null) {
            Toast.makeText(activity, "Bluetooth device not available!", Toast.LENGTH_SHORT).show();
        } else {
            if (!Constants.adapter.isEnabled()) {
                Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enable, Constants.REQUEST_BT);
            }
        }

    }

    public static void connectCache(Activity activity) {
//        TODO: Fix connection on startup of cached device
//        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
//        String addressCache = sharedPref.getString(activity.getString(R.string.pref_address_cache), "null");
//        if (!addressCache.equals("null")) {
//            Utility.log("CubeCommunicator", "connectCache Address: " + addressCache);
//            try {
//                Toast.makeText(activity, "Connecting...", Toast.LENGTH_LONG).show();
//                new ConnectThread(Constants.adapter.getRemoteDevice(Constants.address), activity).start();
//            } catch (Exception e) {
//                System.out.println("Exception occured");
//                e.printStackTrace();
//            }
//        }
    }

    public static void displayPairedDevicesList(final Activity activity) {
        Set<BluetoothDevice> pairedDevices = Constants.adapter.getBondedDevices();
        final ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(activity, "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, list);

        new AlertDialog.Builder(activity)
                .setAdapter(aa, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String info = list.get(which);
                        String[] tok = info.split("\n");
                        Utility.log("CubeCommunicator", String.format("Name: '%s', Address: '%s'", tok[0], tok[1]));
                        Constants.address = tok[1];
                        try {
                            Toast.makeText(activity, "Connecting...", Toast.LENGTH_LONG).show();
                            Utility.log("CubeCommunicator", "Starting ConnectThread");
                            new ConnectThread(Constants.adapter.getRemoteDevice(Constants.address), activity).start();
                            Utility.log("CubeCommunicator", "Started ConnectThread");
                        } catch (Exception e) {
                            System.out.println("Exception occured");
                            e.printStackTrace();
                        }
                    }
                })
                .setTitle("Select device")
                .show();

    }

    public static void startListening(final Activity activity) {

        if (Constants.listenThread != null) {
            Constants.listenThread.interrupt();
        }

        Constants.listenThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String line = Constants.btReader.readLine();
                        Utility.log("CubeCommunicator", line);
                        int current = Integer.parseInt(line);
                        if (current != Constants.passengerCount) {
                            Constants.passengerCount = current;
                            Utility_Map.updatePassengerCount(activity, current);
                        }
                        Thread.sleep(100);
                    } catch (IOException | InterruptedException | NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Disconnected!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
        Constants.listenThread.start();

    }

}
