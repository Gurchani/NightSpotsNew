package com.barfinder.android.findbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.io.InputStream;
import java.net.URL;

public class UserProfile extends AppCompatActivity {

    Button logoutbutton;
    String profilePicture;
    ImageView imageView;
    ProgressDialog progressDialog;
    Drawable myPicture;
    SharedPreferences spc;

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        spc = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        readGlobals();
        imageView = findViewById(R.id.profilePicture);
        logoutbutton = findViewById(R.id.logout);
        getImage getImage = new getImage();
        getImage.execute(profilePicture);

        logoutbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Image Button", "button Clicked");
                FacebookLogout();
                moveToMainActivity();
            }
        });
        readGlobals();
    }

    private void FacebookLogout() {


        LoginManager.getInstance().logOut();
    }

    private void moveToMainActivity() {
        SharedPreferences.Editor editor = spc.edit();
        boolean value = spc.getBoolean("LoginStatus", false);
        editor.putBoolean("LoginStatus", false);

        editor.commit();
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);

    }

    //Read Global Variables
    public void readGlobals() {
        GlobalVariableDatabase dbHelperLive = new GlobalVariableDatabase(getApplicationContext());
        SQLiteDatabase dbLive = dbHelperLive.getReadableDatabase();


        String[] projectionLive = {
                FeederClass.FeedEntry.profilePicture
        };

        Cursor cursorLive = dbLive.query(
                FeederClass.FeedEntry.Globals,                     // The table to query
                projectionLive,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (cursorLive.moveToNext()) {
            profilePicture = cursorLive.getString(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.profilePicture));

        }
    }

    class getImage extends AsyncTask<String, Void, Drawable> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UserProfile.this);
            progressDialog.show();
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            return LoadImageFromWebOperations(strings[0]);

        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            myPicture = drawable;
            progressDialog.dismiss();
            if (myPicture != null) {
                imageView.setImageDrawable(myPicture);
            }
        }
    }
}
