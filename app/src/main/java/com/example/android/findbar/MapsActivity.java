package com.example.android.findbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import static com.example.android.findbar.R.id.map;

/**
 * This is the class version of TestFragment
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public String searchResult;
    boolean SingleGirlsTicked;
    boolean PintPriceTicked;
    boolean LessCrowdedTicked;
    boolean SimilartoMeTicked;
    int User_Age;
    String User_Gender;
    JSONArray sqlBarFullData;
    ForegroundWorker foregroundWorker = new ForegroundWorker(this);
    ForegroundWorker foregroundUpdater = new ForegroundWorker(this);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        SingleGirlsTicked = intent.getBooleanExtra("SingleGirlsTicked", true);
        PintPriceTicked = intent.getBooleanExtra("PintPriceTicked", true);
        LessCrowdedTicked = intent.getBooleanExtra("LessCrowdedTicked", true);
        SimilartoMeTicked = intent.getBooleanExtra("SimilarToMeTicked", true);
        User_Age = intent.getIntExtra("UserAge", 25);
        User_Gender = intent.getStringExtra("UserGender");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng paris = new LatLng(48.856614, 2.352222);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 12));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        String SGT = "";
        String PPT = "";
        String SMT = "";
        String LCT = "";

        if(SingleGirlsTicked){
            if (User_Gender.equalsIgnoreCase("0")){
                SGT = "(.3 * SingleBoys)";
            } else {
            SGT = "(.3 * SingleGirls)";
            }
            if (PintPriceTicked || LessCrowdedTicked || SimilartoMeTicked){
                SGT = SGT + " + ";
            }
        } else {
            SGT = "";
        }

        if(PintPriceTicked){} else {

        }

        if(LessCrowdedTicked){
            LCT = "(.2 * (TotalGirls + TotalBoys)";
            if (SimilartoMeTicked){
                LCT = LCT + " + ";
            }
        } else {
            LCT = "";
        }
        if(SimilartoMeTicked){} else {}

        String SqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge FROM barlivedata";
               // " ORDER BY "+ SGT + PPT + SMT + LCT + ") DESC";
        sendTheStatement(SqlStatement);

        populateMap();
    }

    private void sendTheStatement(String sqlStatement) {

        String type = "GetSortedBars";
        foregroundWorker.execute(type, sqlStatement);
    }

    public void populateMap() {
            readData();
            updateData();

    }
    public void readData(){
        localDatabase dbHelper = new localDatabase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                FeederClass.FeedEntry.barId,
                FeederClass.FeedEntry.barLatitude,
                FeederClass.FeedEntry.barLongitude,
                FeederClass.FeedEntry.PricePint,
                FeederClass.FeedEntry.barName,
                FeederClass.FeedEntry.barAddress
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

        int GBU = 2; //Determine if the bar is Good(0)/Bad(1)/Ugly(2)

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barId));
            double Latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barLatitude));
            double Longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barLongitude));

            GBU = DetermineGBU();
            addMarkers(mMap, Latitude, Longitude, GBU);
        }
        cursor.close();
    }

    public void updateData(){
        String sqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge FROM barlivedata";
        String type = "UpdateData";
        if (foregroundUpdater.getStatus() != AsyncTask.Status.RUNNING && foregroundUpdater.getStatus() != AsyncTask.Status.PENDING ){
            String abc = String.valueOf(foregroundUpdater.getStatus());
            if (foregroundUpdater.getStatus() == AsyncTask.Status.FINISHED){
                foregroundUpdater.cancel(true);
            }
            foregroundUpdater.execute(type, sqlStatement);
        }
    }

    private void addMarkers(GoogleMap mMap, double lat, double lng, int GBU){
        LatLng paris = new LatLng(lat, lng);




        if (GBU == 0){
         mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hotmarker));
        }
        if(GBU == 1){
            mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.coldmarker));
        }
        if(GBU==2){
            mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.deadmarker));
        }


    }

    //DetemineGBU stands for finding out if a bar is Good, Bad or Ugly
    //If returned 0 its Good
    //if returned 1 its Bad
    //if returned 2 its Ugly
    private int DetermineGBU(){
        return 0;
    }

    private LatLng getCurrentLocation(){
        service s = new service();
        LatLng currentLocation = new LatLng(s.UserLat , s.UserLong);
        return currentLocation;

    }



}
