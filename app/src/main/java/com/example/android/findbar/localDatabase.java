package com.example.android.findbar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Gurchani on 3/30/2017.
 */

  public class localDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "barLocalData";
    SQLiteDatabase localData;


    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeederClass.FeedEntry.tableName
            + " (" +
            FeederClass.FeedEntry.barId + " INTEGER PRIMARY KEY," +
            FeederClass.FeedEntry.barName + " TEXT, " +
            FeederClass.FeedEntry.barAddress + " TEXT, " +
            FeederClass.FeedEntry.PricePint + " REAL, " +
            FeederClass.FeedEntry.barLatitude + " REAL," +
            FeederClass.FeedEntry.barLongitude + " REAL, " +
            FeederClass.FeedEntry.barRadius + " INTEGER, " +
            FeederClass.FeedEntry.barCityCountry+" TEXT);";

  private static final String SQL_DELETE_ENTRIES =
          "DROP TABLE IF EXISTS " + FeederClass.FeedEntry.tableName;

    public localDatabase(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // This database is only a cache for online data, so its upgrade policy is
      // to simply to discard the data and start over
      db.execSQL(SQL_DELETE_ENTRIES);
      onCreate(db);
    }

  }

  //This function will do an sql query which service will user for getting to know if a user entered a bar
  /*public int doSQLQuery(double UserLat, double UserLong) {
    double distance = 50;

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] projection = {
            FeederClass.FeedEntry.barId,
    };

    // Filter results WHERE "title" = 'My Title'
    Cursor c = dis(String.valueOf(Math.cos((double) distance / (double) 6380)), Math.cos(deg2rad(UserLat)), Math.sin(deg2rad(UserLat)), Math.cos(deg2rad(UserLong)), Math.sin(deg2rad(UserLong)), UserLat, UserLong);

    return c.getCount();

  }
  public Cursor dis(String dis, double cos_lat_rad, double sin_lat_rad, double cos_lon_rad, double sin_lon_rad, double UserLat, double UserLong) {
    localData = this.getReadableDatabase();
    double acosCosLat = Math.acos(cos_lat_rad);
    //Cursor cursor2 = localData.rawQuery("SELECT * ,(" + sin_lat_rad + "* "sin_lat_rad "+ " + cos_lat_rad + "* "cos_lat_rad "*(" + sin_lon_rad + "*"sin_lon_rad"+" + cos_lon_rad + "*"cos_lon_rad")) AS "distance_acos" FROM parish WHERE ("+sin_lat_rad+" * "sin_lat_rad" +"+ cos_lat_rad +"* "cos_lat_rad" * (+"+sin_lon_rad +"* "sin_lon_rad" + "+cos_lon_rad +"* "cos_lon_rad")) >"+dis+ " ORDER BY "distance_acos" DESC ", null);
    Cursor cursor1 = localData.rawQuery("SELECT `barID` FROM `BarDetails` WHERE `barCityCountry` = 'Paris,France'", null);
    Cursor cursor = localData.rawQuery("SELECT `barID` AS distance FROM `BarDetials` ORDER BY ((barLatitude- "+ UserLat + ")*(barLatitude- "+ UserLat + ")) + ((barLongitude - "+ UserLong+")*(barLongitude - "+ UserLong+")) ASC", null);
    return cursor1;

  }*/




