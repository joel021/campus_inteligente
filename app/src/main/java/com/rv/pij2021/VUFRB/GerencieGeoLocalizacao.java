package com.rv.pij2021.VUFRB;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;


public class GerencieGeoLocalizacao implements SensorEventListener {
    private final Activity contextActivity;
    private LocationManager locationManager;
    private LocationListener locationListenerGPS, locationListenerNet;

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public double latitude, longitude;

    public GerencieGeoLocalizacao(Activity contextActivity){
        this.contextActivity = contextActivity;
        sensorManager = (SensorManager) contextActivity.getSystemService(Context.SENSOR_SERVICE);
    }

    public void getPositionNetwork(){
        locationManager = (LocationManager) contextActivity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        }

        boolean gps_enabled = false, network_enabled = false;
        try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        if (!gps_enabled && !network_enabled){
            return;
        }

        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.i("RAUFRB", "GPS-> Lat: "+latitude+", Lon: "+longitude);
            }
        };

        locationListenerNet = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.i("RAUFRB", "NET-> Lat: "+latitude+", Lon: "+longitude);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNet);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);

    }

    private void getLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(contextActivity);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_sim, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(contextActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(contextActivity);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_olhar_perm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    contextActivity.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + contextActivity.getPackageName())));
                }
            }).setNegativeButton(R.string.btn_fechar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }

    public void onPause(){

        if (locationManager != null){
            locationManager.removeUpdates(locationListenerGPS);
            locationManager.removeUpdates(locationListenerNet);
        }

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }


    public void onResume() {

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }

}
