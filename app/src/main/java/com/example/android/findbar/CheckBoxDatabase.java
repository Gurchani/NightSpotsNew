package com.example.android.findbar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Gurchani on 6/30/2017.
 */

public class CheckBoxDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Checkers";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeederClass.FeedEntry.CheckBox
            + " (" +
            FeederClass.FeedEntry.SingleGirlsChecked + " INTEGER," +
            FeederClass.FeedEntry.PintPriceChecked + " INTEGER, " +
            FeederClass.FeedEntry.LessCrowdedChecked + " INTEGER, " +
            FeederClass.FeedEntry.mGirlsmBoys + " INTEGER DEFAULT 1, " +
            FeederClass.FeedEntry.Singleness + " INTEGER DEFAULT 1, " +
            FeederClass.FeedEntry.CrowdLevel + " INTEGER DEFAULT 1, " +
            FeederClass.FeedEntry.SimilarChecked + " INTEGER);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeederClass.FeedEntry.CheckBox;

    public CheckBoxDatabase(Context context) {
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
