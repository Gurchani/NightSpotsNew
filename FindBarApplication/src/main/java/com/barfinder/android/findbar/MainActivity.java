package com.barfinder.android.findbar;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 * Main Activity Collects the following information about the user from his facebook account. 1)Facebook Id 2)Gender 3)Birthday (Used to convert it into age). and sends them to a database called Gloabl Variables
 * The main activity also retrieves the Location Coordinates and other information about bars in the city from the server-database and put them in the local sqlite database.
 * Main activity also starts a service, which runs in the background and tells the server database if a user is inside a bar or not. If user is inside a particular bar then the service updates the server-database about the number of people, Gender, Average age of people for that particular bar.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;
    String selfLoginResult = "";
    LoginButton loginButton; //Log in Button for Facebook
    CallbackManager callbackManager; //Call back Manager for Facebook
    String User_Gender; //Gender from Facebook
    String User_id; //determines the facebook Id of a person
    String profilePicUrl; //Profile Pic from Facebook
    int User_NumberOfFriends; //Number fo Facebook Friends
    String User_birthday; //Birthday of the user from Facebook
    int User_Age = 0; // Age of the User
    ArrayList<String> likes = new ArrayList<String>(); //What pages he likes
    String PagesInJson; //List of pagest in Json Format
    AccessToken accessToken;

    LocationManager locationManager;
    LocationListener locationListener;
    double User_Latitude;
    double User_Longitude;
    int counter = 0; //This counter helps in keeping track of the for-loop for getting Page Likes of users

    EditText emailText;
    EditText _passwordText;
    CheckBox RememberMeCheckBox;
    Button _loginButton;
    TextView _signupLink;

    //Progress Dialogue for signin authentication
    ProgressDialog progressDialog;

    String hashedPassword;
    String Salt;
    String emailVerified;
    String unhashedPassword;

    String email;
    String password;

    SharedPreferences LoginStatusTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //This is to remove the bar that has the name of the app
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
        //See if user is already logged-in from the sharedpreferance
        LoginStatusTracker = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        boolean userFirstTimeOpens = LoginStatusTracker.getBoolean("DescriptionPage1", true);
        boolean userFirstLogin = LoginStatusTracker.getBoolean("LoginStatus", false);
        //Splash Screen - This only opens the first time user logs in
        if (userFirstTimeOpens) {
            goToSplashScreenActivity();
        }
        //if user is logging in for the first time
        if (userFirstLogin) {
            updateData(getCityCountry());
            beginLocationService(); //Starts a service in the background which keeps telling the serverdatabase if this user is i
            goToSecondActivity();
        }


        //ButterKnife.bind(this);
        Get_hash_key();
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d("Failed", "Failed to Log in");
        } else {
            //getUserInfo(AccessToken.getCurrentAccessToken());
            //Actions to be taken after all the information about the user has been collected
            ///insertPageData();
            //putInGlobals();
            updateData(getCityCountry());
            beginLocationService(); //Starts a service in the background which keeps telling the serverdatabase if this user is i
            goToSecondActivity();
        }
        emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
        RememberMeCheckBox = findViewById(R.id.RememberCheckbox);

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


        readInCredentials();
        //loginWithFacebook();
    }

    /**
     * This shall take user to the splash screen if he is loggin in for the first time
     */
    public void goToSplashScreenActivity() {
        Intent TheIntent = new Intent(MainActivity.this, DescriptionActivity.class);
        startActivity(TheIntent);
    }

    /**
     * Method takes the GPS location of the user and returns the country
     *
     * @return
     */
    public String getCityCountry() {
        return "Paris,France";
    }

    /**
     * Gets the Bar-id, Name, Price of Pint, Radius, Longitude, Latitude
     * Will be more useful when app is introduced in more than one city and it will be necessary to customize the bar data according to location of user
     *
     * @param cityCountry
     */
    private void updateData(String cityCountry) {
        String type = "getBarLocations";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, cityCountry);
    }

    /**
     * Start service which tells the Mysql Database if the user is inside a bar or not
     */
    private void beginLocationService() {
        Intent ntent = new Intent(this, service.class);
        ntent.putExtra("FbID", User_id);
        this.startService(ntent);
    }


    //In Future you may consider to use the log in with facebook option
    private void loginWithFacebook() {
        //Facebook Button

        /*loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile",  //Get Permissions from Facebook
                "user_likes", "user_birthday"));
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                *//*Graph Request for getting the list of Pages that User has liked*//*

                FacebookUserInfo facebookUserInfo = new FacebookUserInfo();
                facebookUserInfo.execute(loginResult.getAccessToken());
                //Actions to be taken after all the information about the user has been collected
                //insertPageData();
                updateData(getCityCountry());
                beginLocationService(); //Starts a service in the background which keeps telling the serverdatabase if this user is inside a bar

            }

            @Override
            public void onCancel() {
                //textView.setText("LogIn Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                //textView.setText("Error Encoutered");
            }
        });*/


    }

    /**
     * All the Following methods and Class getSignInInfo are related to my own Login Logic
     *
     * @param view
     */
    public void login(View view) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        email = emailText.getText().toString();
        password = _passwordText.getText().toString();
        if (RememberMeCheckBox.isChecked()) {
            noteDownCredentials(email, password, 1);
        }
        verifyCapatchAndStartSignup();

    }

    private void doSomethingWithLoginResult() {
        if (selfLoginResult.equals("")) {
            Context context = getApplicationContext();
            CharSequence text = "Mauvais courriel ou mot de passe";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (selfLoginResult.equals("Success")) {
            if (validateTheLogin()) {
                updateData(getCityCountry());
                if (!isMyServiceRunning(service.class)) {
                    beginLocationService();
                }
                goToSecondActivity();
            } else {
                Context context = getApplicationContext();
                CharSequence text = "Mauvais courriel ou mot de passe";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    private boolean validateTheLogin() {
        unhashedPassword = _passwordText.getText().toString();
        String Salted = unhashedPassword + Salt;
        return HashPassword(Salted).equals(hashedPassword);

    }

    private String HashPassword(String pass) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update((pass).getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String encryptedPassword = (new BigInteger(messageDigest.digest())).toString(16);
        return encryptedPassword;
    }
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        goToSecondActivity();
    }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    public void Get_hash_key() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.barfinder.android.findbar", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    /**
     * Get Location of User (Only Useful for the cases when we move to multiple cities.
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
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /**
     * This inserts the details of pages liked by user into the mysql databse table "likedpages"
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
     * Leads you to Second Activty where user chooses wether a person is single or not
     */
    public void goToSecondActivity() {
        SharedPreferences.Editor editor = LoginStatusTracker.edit();
        editor.putBoolean("LoginStatus", true);
        editor.commit();

        Intent SecondIntent = new Intent(MainActivity.this, MapListFragment.class);
        SecondIntent.putExtra("User_Gender", User_Gender);
        SecondIntent.putExtra("User_Age", User_Age);
        SecondIntent.putExtra("User_id", User_id);
        startActivity(SecondIntent);
    }



    /**
     * Converts Birthday to Age
     *
     * @param birthday
     * @return
     */
    private int convertToAge(String birthday) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        LocalDate date = formatter.parseLocalDate(birthday);
        Years age = Years.yearsBetween(date, LocalDate.now());
        User_Age = age.getYears();
        return age.getYears();
    }

    private void verifyCapatchAndStartSignup() {
        final AsyncverificationCapatcha verificationCapatcha = new AsyncverificationCapatcha();

        SafetyNet.getClient(this).verifyWithRecaptcha("6LeFiUcUAAAAADFg2haec7Gb-LFnQnTNcWhIe0_l")
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            verificationCapatcha.execute("6LeFiUcUAAAAAEWOC3d4moRPoyHMx7b4Lus5I8d8", response.getTokenResult());

                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d(TAG, "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d(TAG, "Unknown type of error: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * Puts all the details about a users in local database so that it can be used in other activities
     */
    private void putInGlobals() {
        GlobalVariableDatabase dbHelper = new GlobalVariableDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.UserFbid, User_id);
        contentValues.put(FeederClass.FeedEntry.UserGender, User_Gender);
        contentValues.put(FeederClass.FeedEntry.profilePicture, profilePicUrl);
        contentValues.put(FeederClass.FeedEntry.UserAge, User_Age);
        long result = db.replace(FeederClass.FeedEntry.Globals, null, contentValues);
        db.close();
    }

    private void noteDownCredentials(String Email, String Password, int primaryKey) {
        CredentialsData dbHelper = new CredentialsData(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.primary, primaryKey);
        contentValues.put(FeederClass.FeedEntry.emailId, Email);
        contentValues.put(FeederClass.FeedEntry.password, Password);
        long result = database.replace(FeederClass.FeedEntry.RememerCheckBox, null, contentValues);
        database.close();
    }

    private void readInCredentials() {
        CredentialsData dbHelperLive = new CredentialsData(getApplicationContext());
        SQLiteDatabase dbLive = dbHelperLive.getReadableDatabase();


        String[] projectionLive = {
                FeederClass.FeedEntry.emailId,
                FeederClass.FeedEntry.password
        };

        String args = FeederClass.FeedEntry.primary + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = "1";

        Cursor mCursor = dbLive.rawQuery("SELECT * FROM " + FeederClass.FeedEntry.RememerCheckBox, null);
        int count = mCursor.getCount();
        if (mCursor.getCount() > 0) {

            Cursor cursorLive = dbLive.query(
                    FeederClass.FeedEntry.RememerCheckBox,                     // The table to query
                    projectionLive,                               // The columns to return
                    args,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (cursorLive.moveToNext()) {
                String email = cursorLive.getString(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.emailId));
                String password = cursorLive.getString(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.password));
                if (email != null && password != null) {
                    emailText.setText(email);
                    _passwordText.setText(password);
                }
            }
        }


    }

    /**
     * Graph Request for User's Name, Birthday, Profile Picture and other public information
     *
     */
    private void getUserInfo(AccessToken accessToken) {
        /*Graph Request for getting Gender, User-Id and Birthday*/
        GraphRequest request2 = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("Main", response.toString());
                        try {
                            User_Gender = object.getString("gender");

                        } catch (JSONException e) {
                            // textView.setText("No Gender Specified");
                            e.printStackTrace();
                        }

                        try {
                            User_id = object.getString("id");
                            //textView.setText(" " + User_id);

                        } catch (JSONException e) {
                            // textView.setText("No ID");
                            e.printStackTrace();
                        }
                        try {
                            User_birthday = object.getString("birthday");
                            User_Age = convertToAge(User_birthday);

                        } catch (JSONException e) {
                            // textView.setText("No Birthday");
                            e.printStackTrace();
                        }
                        try {
                            JSONObject data = response.getJSONObject();
                            profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");

                        } catch (JSONException e) {
                            // textView.setText("No Birthday");
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, gender, birthday, picture.type(large)");
        request2.setParameters(parameters);
        request2.executeAsync();
    }

    /**
     * Graph request to get all the liked pages of a perticular user
     *
     */
    private void getUserLikedPages(AccessToken accessToken) {
        final String[] afterString = {""};
        final Boolean[] noData = {true};
        do {
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    //AccessToken.getCurrentAccessToken();
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
    }

    private void startSigninProcess() {
        getSignInInfo getsignininfo = new getSignInInfo();
        getsignininfo.execute("Signin", email, password);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    class getSignInInfo extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            String type = params[0];
            String ip = "http://barfinder.website/";
            String Signup = ip + "Signin.php";

            String inserterResult = null;
            if (type.equals("Signin")) {

                try {
                    String email = params[1];
                    URL url = new URL(Signup);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    inserterResult = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        inserterResult += " " + line;
                    }
                    if (!inserterResult.equals("")) {
                        JSONArray jsonArray = new JSONArray(inserterResult);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            hashedPassword = jsonobject.getString("Password");
                            Salt = jsonobject.getString("Salt");
                            emailVerified = jsonobject.getString("EmailVerified");
                        }
                        inserterResult = "Success";
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "failed";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Internet Failure";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return inserterResult;
        }

        @Override
        protected void onPreExecute() {
            _loginButton.setEnabled(false);
            progressDialog.show();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
        }

        @Override
        protected void onPostExecute(String s) {
            _loginButton.setEnabled(true);
            super.onPostExecute(s);
            selfLoginResult = s;
            progressDialog.dismiss();
            doSomethingWithLoginResult();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);


        }
    }

    class FacebookUserInfo extends AsyncTask<AccessToken, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(AccessToken... accessTokens) {
            GraphRequest request2 = GraphRequest.newMeRequest(
                    accessTokens[0],
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("Main", response.toString());
                            try {
                                User_Gender = object.getString("gender");

                            } catch (JSONException e) {
                                // textView.setText("No Gender Specified");
                                e.printStackTrace();
                            }

                            try {
                                User_id = object.getString("id");
                                //textView.setText(" " + User_id);

                            } catch (JSONException e) {
                                // textView.setText("No ID");
                                e.printStackTrace();
                            }
                            try {
                                User_birthday = object.getString("birthday");
                                User_Age = convertToAge(User_birthday);

                            } catch (JSONException e) {
                                // textView.setText("No Birthday");
                                e.printStackTrace();
                            }
                            try {
                                JSONObject data = response.getJSONObject();
                                profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");

                            } catch (JSONException e) {
                                // textView.setText("No Birthday");
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, gender, birthday, picture.type(large)");
            request2.setParameters(parameters);
            request2.executeAndWait();
            //getUserLikedPages(accessTokens[0]);
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            putInGlobals();
            progressDialog.dismiss();
            goToSecondActivity();
        }
    }

    class AsyncverificationCapatcha extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String Url = "https://www.google.com/recaptcha/api/siteverify";

            String inserterResult = null;

            try {
                String secret = params[0];
                String response = params[1];
                URL url = new URL(Url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream osp = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(osp, "UTF-8"));
                String post_data = URLEncoder.encode("secret", "UTF-8") + "=" + URLEncoder.encode(secret, "UTF-8") + "&"
                        + URLEncoder.encode("response", "UTF-8") + "=" + URLEncoder.encode(response, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                osp.close();

                InputStream isp = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(isp, "iso-8859-1"));
                inserterResult = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    inserterResult += " " + line;
                }
                if (!inserterResult.equals("")) {
                    JSONObject jobj = new JSONObject(inserterResult);
                    inserterResult = jobj.getString("success");
                }
                bufferedReader.close();
                isp.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "Internet Failure";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return inserterResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                startSigninProcess();
            }
        }
    }
}
