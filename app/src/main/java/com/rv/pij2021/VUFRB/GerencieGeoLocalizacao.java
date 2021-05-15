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

import java.util.ArrayList;
import java.util.List;

/*
Gerencia os dados de localização de direção do celular (para onde a câmera está apondnando) .
 */
public class GerencieGeoLocalizacao implements SensorEventListener {
    private final Activity contextActivity;
    private LocationManager locationManager;
    private LocationListener locationListenerGPS, locationListenerNet;

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private List<Float> orientationAngles;

    public double latitude, longitude;

    public GerencieGeoLocalizacao(Activity contextActivity){
        this.contextActivity = contextActivity;
        sensorManager = (SensorManager) contextActivity.getSystemService(Context.SENSOR_SERVICE);
        orientationAngles = new ArrayList<>();
        onResume();
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

        // percebi que raramente se consegue obter dados GPS
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
            if (locationListenerGPS != null){
                locationManager.removeUpdates(locationListenerGPS);
            }

            if(locationListenerNet != null){
                locationManager.removeUpdates(locationListenerNet);
            }
        }

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    public void onResume() {

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {//melhor: TYPE_GEOMAGNETIC_ROTATION_VECTOR
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }

        updateOrientationAngles();
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public float updateOrientationAngles() {

        Log.i("RAUFRB", magnetometerReading[0]+" "+magnetometerReading[1]+" "+magnetometerReading[2]);
        return (float) magnetometerReading[0];
    }

    private float overageMoving(float orientationAngle){

        if (orientationAngles.size() >= 20){
            orientationAngles.set(0,orientationAngle);
        }else{
            orientationAngles.add(orientationAngle);
        }

        //float[] movingAverage = new float[9];
        float[] cumsum = new float[orientationAngles.size()+1];

        cumsum[0] = 0;
        for(int i = 0; i < orientationAngles.size(); i++){

            cumsum[i+1] = cumsum[i] + orientationAngles.get(i);

            // 5 é o tamanho do moving average
            if (i >= 5){
                float value = (cumsum[i] - cumsum[i-5])/5;
                orientationAngles.set(i,value);
            }
        }

        return orientationAngles.get(orientationAngles.size()-1);
    }

}
