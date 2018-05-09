/**
 * The main goal of this service is to determine if a perticular user is inside any bar in the city or not. and it will tell which user is in which bar
 */

package com.example.android.findbar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gurchani on 4/21/2017.
 */

public class service  extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public double UserLong;
    public double UserLat;
    public String userDistinctId = "";
    int oldValue = 0;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;
    private localDatabase database;

    /**
     * Finds Distance between two location-points. It is used to determine if the distance of the user is within the location radius of the bar
     *
     * @param lat1 Lat of User
     * @param lon1 Lng of User
     * @param lat2 Lat of Bar
     * @param lon2 Lng of Bar
     * @param unit Km or Miles
     * @return distance between the points in the chosen unit
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /**
     * Converts Radian to Degrees for measuring the distance
     *
     * @param rad
     * @return
     */
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    /**
     * Converts degrees to radiians
     *
     * @param deg Degrees
     * @return radian
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * In case if the user gets out of the bar, it is represented inside the Mysql database
     * @param barid is the ID of the bar from which the user has gotten out
     */
    private void takeDataFromServer(int barid) {
        String type = "takeOutLiveData";
        String bar = String.valueOf(barid);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, userDistinctId, bar);
    }

    /**
     * In case if the user gets in the bar, it is represented inside the Mysql database LiveStats    *
     * @param i
     */
    private void sendDatatoServer(int i) {
        String type = "insertLiveData";
        String bar = String.valueOf(i);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, userDistinctId, bar);
    }

    /**
     * Just for the purpose of showing that the service has started and what is the Id of the bar in which the user has entered
     * @param text
     */
    private void makeToast(String text) {
        Toast.makeText(this, text + " " , Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        userDistinctId = (String) intent.getExtras().get("Email");
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        //android.os.Debug.waitForDebugger();
        String s = " ";
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        database = new localDatabase(this);
        makeToast("Its opened");

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /**
     * Detemines if a user is inside a bar any of the bars or not by literally checking every bar in the city
     * @param UserLat
     * @param UserLong
     * @return Returns the ID of the Bar in which the user is
     */
    private int ifinsideBar(double UserLat, double UserLong){
        localDatabase dbHelper = new localDatabase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                FeederClass.FeedEntry.barId,
                FeederClass.FeedEntry.barLatitude,
                FeederClass.FeedEntry.barLongitude,
                FeederClass.FeedEntry.barRadius
        };
        String selection = FeederClass.FeedEntry.barCityCountry + " = ?";
        String[] selectionArgs = { "Paris,France" };

        Cursor cursor = db.query(
                FeederClass.FeedEntry.tableName,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        int whichBar = 0;
        int counter = 0;
        while(cursor.moveToNext()) {
            int barId =  cursor.getInt(0);
            double barLatitude =  cursor.getDouble(1);
            double barLongitude = cursor.getDouble(2);
            double barRadius = cursor.getInt(3);
            double distance = distance(UserLat, UserLong, barLatitude , barLongitude, "K");
            counter++;

        if ((distance*1000) < barRadius) {
            whichBar = barId;
            break;
        }

        }
        cursor.close();
        return whichBar;
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            UserLat = mLastLocation.getLatitude();
            UserLong = mLastLocation.getLongitude();
            makeToast(String.valueOf(ifinsideBar(UserLat, UserLong)));

            if ((ifinsideBar(UserLat, UserLong) != 0) && (oldValue != ifinsideBar(UserLat, UserLong))) {
                makeToast("Sending data to server");
                sendDatatoServer(ifinsideBar(UserLat, UserLong));
            }
            if ((ifinsideBar(UserLat, UserLong) == 0) && (oldValue != ifinsideBar(UserLat, UserLong))) {
                makeToast("taking data from server");
                takeDataFromServer(ifinsideBar(UserLat, UserLong));
            }

            oldValue = ifinsideBar(UserLat, UserLong);
            // String text = String.valueOf(database.doSQLQuery(UserLat, UserLong));
            // makeToast(text);

            //   doSQLQuery();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}
