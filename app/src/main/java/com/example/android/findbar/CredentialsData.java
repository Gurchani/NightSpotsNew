package com.example.android.findbar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gurchani on 2/19/2018.
 */

public class CredentialsData extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Credentials";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeederClass.FeedEntry.RememerCheckBox
            + " (" +
            FeederClass.FeedEntry.primary + " INTEGER PRIMARY KEY," +
            FeederClass.FeedEntry.emailId + " TEXT," +
            FeederClass.FeedEntry.password + " TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeederClass.FeedEntry.RememerCheckBox;

    public CredentialsData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);

    }
}
