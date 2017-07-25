package com.example.android.findbar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gurchani on 6/20/2017.
 */

public class GlobalVariableDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Globals";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeederClass.FeedEntry.Globals
            + " (" +
            FeederClass.FeedEntry.UserFbid + " INTEGER PRIMARY KEY," +
            FeederClass.FeedEntry.UserGender + " TEXT, " +
            FeederClass.FeedEntry.UserAge + " INTEGER);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeederClass.FeedEntry.Globals;

    public GlobalVariableDatabase(Context context) {
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
