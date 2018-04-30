package com.example.android.findbar;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.security.SecureRandom;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;

    String[] SPINNERLIST = {"Masculin", "Féminin"};
    String[] AgeRange = {"18 à 25", "25 à 30", "30 à 40", "40 à 50", "Plus de 50"};
    String[] relationShipStatus = {"Célibataire", "Pas célibataire"};

    String UserAge = "";
    String UserGender = "";
    String UserRelationshipStatus = "";
    String email;
    String password;

    ProgressDialog progressDialog;

    SharedPreferences LoginStatusTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();


        LoginStatusTracker = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        //Gender Spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        final MaterialBetterSpinner GenderSpinner = findViewById(R.id.Gender);
        GenderSpinner.setAdapter(arrayAdapter);
        GenderSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                UserGender = GenderSpinner.getText().toString();
                Log.d("value", UserGender);

            }
        });

        //Relationship Status Spinner
        ArrayAdapter<String> singleOrNot = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, relationShipStatus);
        final MaterialBetterSpinner rStatus = findViewById(R.id.relationshipStatus);
        rStatus.setAdapter(singleOrNot);
        rStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                UserRelationshipStatus = rStatus.getText().toString();
                Log.d("value", UserRelationshipStatus);

            }
        });


        //Age Range Spinner
        ArrayAdapter<String> Ages = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, AgeRange);
        final MaterialBetterSpinner Age = findViewById(R.id.AgeRange);
        Age.setAdapter(Ages);
        Age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                UserAge = Age.getText().toString();
                Log.d("value", UserGender);

            }
        });

        _emailText = findViewById(R.id.emailSignup);
        _passwordText = findViewById(R.id.passwordSignup);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    verifyCapatchAndStartSignup();
                } else {
                    _signupButton.setEnabled(true);
                }
            }
        });
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent g = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(g);
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Création de votre compte...");
        //The Progress dialogue will be closed by the backend process when the data is already sent
        progressDialog.show();

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        putSignupInServer putSignupInServer = new putSignupInServer();
        putInGlobals(email, UserGender, UserAge);
        putSignupInServer.execute(email, password, UserGender, UserAge, UserRelationshipStatus);
    }

    private void verifyCapatchAndStartSignup() {
        final verificationCapatcha verificationCapatcha = new verificationCapatcha();

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

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;


        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (UserGender.isEmpty() || UserRelationshipStatus.isEmpty() || UserAge.isEmpty()) {
            Context context = this;
            CharSequence text = "Un ou plusieurs champs sont laissés vides";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            valid = false;
        }
        if (UserRelationshipStatus.isEmpty()) {
        }
        if (UserAge.isEmpty()) {
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private String translateToEnglish(String s) {
        if (s.matches("Célibataire")) {
            return "Single";
        } else if (s.matches("Pas célibataire")) {
            return "Not Single";
        } else if (s.matches("Masculin")) {
            return "Male";
        } else if (s.matches("Féminin")) {
            return "Female";
        } else if (s.equals("Plus de 50")) {
            return "Over 50";
        }
        if (s.contains("à")) {
            return s.replaceAll("à", "to");
        } else return s;

    }

    //Globals are irrelevant for the moment but will be utilized in the future and modified accordingly
    private void putInGlobals(String Email, String Gender, String Age) {
        GlobalVariableDatabase dbHelper = new GlobalVariableDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.UserFbid, Email);
        contentValues.put(FeederClass.FeedEntry.UserGender, Gender);
        //contentValues.put(FeederClass.FeedEntry.profilePicture, profilePic);
        contentValues.put(FeederClass.FeedEntry.UserAge, Age);
        long result = db.replace(FeederClass.FeedEntry.Globals, null, contentValues);
        db.close();
    }

    private void askThemToVerifyEmail() {
        Context context = this;
        CharSequence text = "Compte créé";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToSecondActivity();
            }
        }, 3500);
    }

    private void goToSecondActivity() {
        SharedPreferences.Editor editor = LoginStatusTracker.edit();
        editor.putBoolean("LoginStatus", true);
        editor.commit();

        Intent SecondIntent = new Intent(this, MapListFragment.class);
        SecondIntent.putExtra("User_Gender", UserGender);
        SecondIntent.putExtra("User_Age", UserAge);
        SecondIntent.putExtra("User_id", email);
        startActivity(SecondIntent);
    }

    private String HashPassword(String pass, String salt) {
        //Add Salt
        String salted = pass + salt;

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update((salted).getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String encryptedPassword = (new BigInteger(messageDigest.digest())).toString(16);
        return encryptedPassword;
    }

    private String generateSalt() {
        StringBuilder buf = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        for (int i = 0; i < 6; i++) {// log2(52^6)=34.20... so, this is about 32bit strong.
            boolean upper = sr.nextBoolean();
            char ch = (char) (sr.nextInt(26) + 'a');
            if (upper) ch = Character.toUpperCase(ch);
            buf.append(ch);
        }
        return buf.toString();
    }

    class putSignupInServer extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            String Email = params[0];
            String Password = params[1];
            String Gender = translateToEnglish(params[2]);
            String Age = translateToEnglish(params[3]);
            String relationshipStatus = translateToEnglish(params[4]);

            String salt = generateSalt();
            String hashedPassword = HashPassword(Password, salt);

            String ip = "http://barfinder.website/";
            String Signup = ip + "Signup.php";

            String inserterResult = null;


            try {
                URL url = new URL(Signup);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(Email, "UTF-8") + "&"
                        + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(hashedPassword, "UTF-8") + "&"
                        + URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(Gender, "UTF-8") + "&"
                        + URLEncoder.encode("Age", "UTF-8") + "=" + URLEncoder.encode(Age, "UTF-8") + "&"
                        + URLEncoder.encode("RelationShipStatus", "UTF-8") + "=" + URLEncoder.encode(relationshipStatus, "UTF-8") + "&"
                        + URLEncoder.encode("Salt", "UTF-8") + "=" + URLEncoder.encode(salt, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                progressDialog.setMax(4 - 1);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                inserterResult = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    inserterResult += " " + line;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
            return inserterResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            askThemToVerifyEmail();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }
    }

    class verificationCapatcha extends AsyncTask<String, Void, String> {

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
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String post_data = URLEncoder.encode("secret", "UTF-8") + "=" + URLEncoder.encode(secret, "UTF-8") + "&"
                        + URLEncoder.encode("response", "UTF-8") + "=" + URLEncoder.encode(response, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
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
                is.close();
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
                signup();
            }

        }
    }
}
