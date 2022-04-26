package com.example.chatapp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class LocationServiceTask {
    public static Boolean isLocationServiceEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void displayEnabledLocationServiceDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Please Enable Location Services To Detect Your Location.";

        builder.setMessage(message).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(action));
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geos = new Geocoder(context);
        List<Address> addresses;
        String addressText = "";

        try {
            addresses = geos.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    addressText += (i == 0 ? "" : "\n") + address.getAddressLine(i);

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("GET_LOCATION_ERROR", e.getLocalizedMessage());
        }
        return addressText;
    }

    public static LatLng getLatLngFromAddress(Context context, String strAddress) {
        Geocoder geos = new Geocoder(context);
        List<Address> addresses;

        try {
            addresses = geos.getFromLocationName(strAddress, 5);

            if(addresses.isEmpty()) return null;

            Address address = addresses.get(0);

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            return latLng;
        } catch (IOException e) {
            Log.d("GET_LOCATION_ERROR", e.getLocalizedMessage());
        }
        return null;
    }
}
