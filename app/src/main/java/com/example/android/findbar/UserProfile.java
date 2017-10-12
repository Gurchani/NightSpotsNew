package com.example.android.findbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        readGlobals();
        imageView = (ImageView) findViewById(R.id.profilePicture);
        logoutbutton = (Button) findViewById(R.id.logout);

        if (profilePicture != null) {
            imageView.setImageDrawable(LoadImageFromWebOperations(profilePicture));
        }

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
}
