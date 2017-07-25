package com.example.android.findbar;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/*Facebook Imports*/
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

/*Other Imports*/
import com.google.gson.Gson;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Main Activity Collects the following information about the user from his facebook account. 1)Facebook Id 2)Gender 3)Birthday (Used to convert it into age) 4)List of Pages liked by User and sends them to the next activty
 * The main activity also retrieves the Location Coordinates and other information about bars in the city from the server-database and put them in the local sqlite database.
 * Main activity also starts a service, which runs in the background and tells the server database if a user is inside a bar or not. If user is inside a particular bar then the service updates the server-database about the number of people, Gender, Average age of people for that particular bar.
 */
public class MainActivity extends AppCompatActivity {

    /*Facebook Related Variables*/
    LoginButton loginButton;
    TextView textView; //Just for testing purpose only
    CallbackManager callbackManager;
    String User_Gender;
    int User_NumberOfFriends;
    public static String User_id;
    String User_birthday;
    int User_Age = 0;
    ArrayList<String> likes = new ArrayList<String>();
    String PagesInJson;

    /*Location Variables*/
    LocationManager locationManager;
    LocationListener locationListener;
    double User_Latitude;
    double User_Longitude;

   /*This counter helps in keeping track of the for-loop for getting Page Likes of users*/
    int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

         /*Method for getting Location of the user*/
         //getLocation();

        /*Facebook login related code*/
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile",  //Get Permissions from Facebook
                "user_likes", "user_birthday" ));
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final String[] afterString = {""};
                final Boolean[] noData = {true};
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                /*Graph Request for getting the list of Pages that User has liked*/
                do {
                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/likes",
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {

                                    try {
                                        JSONObject jsonObject = response.getJSONObject();


                                        if (jsonObject.length() > 1) {

                                            JSONObject jsonFacebook = (JSONObject) new JSONTokener(jsonObject.toString()).nextValue();
                                            JSONObject likes_paging = (JSONObject) new JSONTokener(jsonFacebook.getJSONObject("paging").toString()).nextValue();


                                            for (int i = 0; i < jsonFacebook.getJSONArray("data").length(); i++) {
                                                likes.add(jsonFacebook.getJSONArray("data").getJSONObject(i).getString("name"));
                                                counter++;
                                            }
                                            afterString[0] = (String) likes_paging.getJSONObject("cursors").get("after");

                                        } else {
                                            noData[0] = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("pretty", "0");
                    parameters.putString("limit", "100");
                    parameters.putString("after", afterString[0]);
                    request.setParameters(parameters);
                    request.executeAndWait();
                } while (noData[0] == true);

                /*Graph Request for getting Gender, User-Id and Birthday*/
                GraphRequest request2 = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                try {
                                    User_Gender = object.getString("gender");

                                } catch (JSONException e) {
                                    textView.setText("No Gender Specified");
                                    e.printStackTrace();
                                }

                                try {
                                    User_id = object.getString("id");
                                    textView.setText(" " + User_id);

                                } catch (JSONException e) {
                                    textView.setText("No ID");
                                    e.printStackTrace();
                                }
                                try {
                                    User_birthday = object.getString("birthday");
                                    User_Age = convertToAge(User_birthday);

                                } catch (JSONException e) {
                                    textView.setText("No Birthday");
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, gender, birthday");
                request2.setParameters(parameters);
                request2.executeAndWait();

                //Actions to be taken after all the information about the user has been collected
                insertPageData();
                putInGlobals();
                updateData(getCityCountry());
                beginLocationService(); //Starts a service in the background which keeps telling the serverdatabase if this user is inside a bar
                goToSecondActivity();
            }

            @Override
            public void onCancel() {
                textView.setText("LogIn Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                textView.setText("Error Encoutered");
            }
        });
    }

    /**
     * Get Location of User
     */
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                User_Latitude = location.getLatitude();
                User_Longitude = location.getLongitude();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        } else {
            configureButton();
        }
        configureButton();
    }

    /**
     * Google-maps Location Method
     */
    private void configureButton() {
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
        locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);
    }

    /**
     * Facebook Method
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureButton();
                }
        }
    }
    //My Methods

    /**
     * This inserts the detiails of pages liked by user into the mysql databse table "likedpages"
     */
    public void insertPageData() {
        String FbID = User_id;
        PagesInJson = new Gson().toJson(likes);
        String Likes = PagesInJson;
        String type = "insertLikedPages";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, FbID, Likes);
    }

    /**
     * Gets the Bar-id, Name, Price of Pint, Radius, Longitude, Latitude
     * Will be more useful when app is introduced in more than one city and it will be necessary to customize the bar data according to location of user
     * @param cityCountry
     */
    private void updateData(String cityCountry){
        String type = "getBarLocations";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, cityCountry );
    }

    /**
     * Method takes the GPS location of the user and returns the country
     * @return
     */
    public String getCityCountry(){
        return "Paris,France";

        /*Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(User_Latitude, User_Longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            return addresses.get(0).getLocality();
        } else return "no city";*/
    }

    /**
     * Leads you to Second Activty where user chooses wether a person is single or not
     */
    public void goToSecondActivity(){
        Intent SecondIntent = new Intent(MainActivity.this, SingleOrNot.class);
        SecondIntent.putExtra("User_Gender", User_Gender);
        SecondIntent.putExtra("User_Age", User_Age);
        SecondIntent.putExtra("User_id", User_id);
        startActivity(SecondIntent);
    }

    /**
     * Converts Birthday to Age
     * @param birthday
     * @return
     */
    private int convertToAge(String birthday){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        LocalDate date = formatter.parseLocalDate(birthday);
        Years age = Years.yearsBetween(date, LocalDate.now());
        User_Age = age.getYears();
        return age.getYears();
    }

    /**
     * Start service which tells the Mysql Database if the user is inside a bar or not
     */
    private void beginLocationService(){
        Intent ntent = new Intent(this , service.class);
        ntent.putExtra("FbID", User_id );
        this.startService(ntent);
    }

    /**
     * Puts all the details about a users in local database so that it can be used in other activities
     */
    private void putInGlobals(){
        GlobalVariableDatabase dbHelper = new GlobalVariableDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.UserFbid, User_id);
        contentValues.put(FeederClass.FeedEntry.UserGender, User_Gender);
        contentValues.put(FeederClass.FeedEntry.UserAge, User_Age);

        long result = db.insert(FeederClass.FeedEntry.Globals, null, contentValues);

    }

}
