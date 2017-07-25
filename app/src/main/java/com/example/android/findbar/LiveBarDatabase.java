package com.example.android.findbar;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
 * Created by Gurchani on 5/8/2017.
 */

public class LiveBarDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "barLiveData";
    SQLiteDatabase localData;


    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeederClass.FeedEntry.LiveTableName
            + " (" +
            FeederClass.FeedEntry.barId + " INTEGER PRIMARY KEY," +
            FeederClass.FeedEntry.Ranking + " INTEGER, " +
            FeederClass.FeedEntry.barName + " TEXT, " +
            FeederClass.FeedEntry.barAddress + " TEXT, " +
            FeederClass.FeedEntry.TotalBoys + " INTEGER, " +
            FeederClass.FeedEntry.TotalGirls + " INTEGER, "+
            FeederClass.FeedEntry.SingleGirls + " INTEGER, " +
            FeederClass.FeedEntry.SingleBoys + " INTEGER, " +
            FeederClass.FeedEntry.AvAge + " INTEGER, " +
            FeederClass.FeedEntry.PintPrice + " REAL);";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeederClass.FeedEntry.LiveTableName;

    public LiveBarDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
