package com.example.android.findbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;


    String[] SPINNERLIST = {"Male", "Female"};
    String[] AgeRange = {"18 to 25", "25 to 30", "30 to 40", "40 to 50", "Above 50"};

    String UserAge = "";
    String UserGender = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Gender
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        final MaterialBetterSpinner GenderSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
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


        //Age Range
        ArrayAdapter<String> Ages = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, AgeRange);
        final MaterialBetterSpinner Age = (MaterialBetterSpinner)
                findViewById(R.id.AgeRange);
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

        _emailText = (EditText) findViewById(R.id.emailSignup);
        _passwordText = (EditText) findViewById(R.id.passwordSignup);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        putSignupInServer putSignupInServer = new putSignupInServer();
        putSignupInServer.execute(email, password, UserGender, UserAge);


        // TODO: Implement your own signup logic here.

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
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

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    class putSignupInServer extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            String Email = params[0];
            String Password = params[1];
            String Gender = params[2];
            String Age = params[3];

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
                        + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8") + "&"
                        + URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(Gender, "UTF-8") + "&"
                        + URLEncoder.encode("Age", "UTF-8") + "=" + URLEncoder.encode(Age, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                progressDialog.setMax(4 - 1);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += " " + line;
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
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }
    }
}
