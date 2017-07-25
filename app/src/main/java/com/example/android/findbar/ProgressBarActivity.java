package com.example.android.findbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProgressBarActivity extends AppCompatActivity {

    //Choices made by user
    boolean SingleGirlsTicked;
    boolean PintPriceTicked;
    boolean LessCrowdedTicked;
    boolean SimilartoMeTicked;


    //Information about user
    String User_Gender ;
    int User_Age;




    //UI Stuff
    ProgressBar pb;
    Button Setting;

    //For Running the Async Task
    putDataFromServer putDatafromServer = new putDataFromServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        readGlobals();
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        Setting = (Button) findViewById(R.id.button3);

        Intent intent = this.getIntent();

        SingleGirlsTicked = intent.getBooleanExtra("SingleGirls", true);
        PintPriceTicked = intent.getBooleanExtra("PintPrice", true);
        LessCrowdedTicked = intent.getBooleanExtra("LessCrowded", true);
        SimilartoMeTicked = intent.getBooleanExtra("SimilarToMe", true);

        /*User_Age = intent.getIntExtra("UserAge", 25);
        User_Gender = intent.getStringExtra("UserGender");*/


        String SGT = "";
        String PPT = "";
        String SMT = "";
        String LCT = "";

        if(SingleGirlsTicked){
            if (User_Gender.equalsIgnoreCase("female")){
                SGT = "(.5 * SingleBoys)";
            } else {
                SGT = "(.5 * SingleGirls)";
            }
            if (PintPriceTicked || LessCrowdedTicked || SimilartoMeTicked){
                 SGT = SGT + " + ";
            }
        } else {
            SGT = "";
        }

        if(PintPriceTicked){
            PPT = "(-0.2 * PintPrice)";
            if (LessCrowdedTicked || SimilartoMeTicked){
                PPT = PPT + " + ";
            }
            /*if (SingleGirlsTicked){
                PPT = "+(0.2 * PintPrice)";
            } else {
                PPT = "(0.2 * PintPrice)";
            }*/


        } else {
            PPT = "";
        }
        if(LessCrowdedTicked){
            LCT = "(-0.2 * (TotalGirls + TotalBoys))";
            if (SimilartoMeTicked){
                LCT = LCT + " + ";
            }
        } else {
            LCT = "";
        }
        if(SimilartoMeTicked){

        } else {

        }

        String SqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge, PintPrice FROM barlivedata" + " ORDER BY "+ SGT + PPT + LCT + SMT +" DESC";
        //  + PPT + SMT + LCT + ")
        sendTheStatement(SqlStatement);
    }

    //Send the Sql Statement to the server and put the data in local sqlite database
    private void sendTheStatement(String sqlStatement) {
        String type = "GetSortedBars";
        putDatafromServer.execute(type, sqlStatement);
    }

    //If needed
    public void moveToNextActivity(){
        Intent intent = new Intent(this, MapListFragment.class);
//        intent.putExtra("SingleGirlsTicked",SingleGirlsTicked);
//        intent.putExtra("PintPriceTicked",PintPriceTicked);
//        intent.putExtra("LessCrowdedTicked",LessCrowdedTicked);
//        intent.putExtra("SimilarToMeTicked",SimilartoMeTicked);
//
//        intent.putExtra("UserGender",User_Gender);
//        intent.putExtra("UserAge", User_Age);
        startActivity(intent);


    }

    public void moveToSettingsActivity(View view){
        Intent intent = new Intent(this, SingleOrNot.class);
        startActivity(intent);
    }


    class putDataFromServer extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            moveToNextActivity();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String ip = "http://barfinder.website/";
            String GetSortedBars = ip + "GetSortedBars.php";

            String inserterResult = null;
            if (type.equals("GetSortedBars")) {

                try {
                    String sqlQry = params[1];
                    URL url = new URL(GetSortedBars);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("sqlQry", "UTF-8") + "=" + URLEncoder.encode(sqlQry, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += " " + line;
                    }
                    JSONArray jsonArray = new JSONArray(result);

                    inserterResult = "";

                    //noinspection WrongThread
                    pb.setMax(jsonArray.length() - 1);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String TotalBoys = jsonobject.getString("TotalBoys");
                        String TotalGirls = jsonobject.getString("TotalGirls");
                        String SingleBoys = jsonobject.getString("SingleBoys");
                        String SingleGirls = jsonobject.getString("SingleGirls");
                        String AvAge = jsonobject.getString("AvAge");
                        String PintPrice = jsonobject.getString("PintPrice");

                        publishProgress(i);


                        boolean insetData = insertData(i + 1, Integer.valueOf(id), Integer.valueOf(TotalBoys), Integer.valueOf(TotalGirls), Integer.valueOf(SingleBoys), Integer.valueOf(SingleGirls), Integer.valueOf(AvAge), Double.valueOf(PintPrice));
                        if (insetData) {
                            inserterResult = inserterResult + "true ";
                        } else {
                            inserterResult = inserterResult + "false ";
                        }
                    }


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    // MapsActivity mapsActivity = new MapsActivity();
                    // mapsActivity.searchResult = result;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "failed";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "failed again";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return inserterResult;
        }
    }

    //For Async Task to Insert Data in the Local Database
    public boolean insertData(int Rank, int id, int TotalBoys , int TotalGirls ,int SingleBoys, int SingleGirls, int AvAge, double PintPrice){
        LiveBarDatabase dbHelper = new LiveBarDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.barId, id);
        contentValues.put(FeederClass.FeedEntry.Ranking, Rank);
        contentValues.put(FeederClass.FeedEntry.TotalBoys, TotalBoys);
        contentValues.put(FeederClass.FeedEntry.TotalGirls, TotalGirls);
        contentValues.put(FeederClass.FeedEntry.SingleBoys, SingleBoys);
        contentValues.put(FeederClass.FeedEntry.SingleGirls, SingleGirls);
        contentValues.put(FeederClass.FeedEntry.AvAge, AvAge);
        contentValues.put(FeederClass.FeedEntry.PintPrice, PintPrice);

        long result = db.replace(FeederClass.FeedEntry.LiveTableName, null, contentValues);
        if (result == -1){
            return false;
        } else {
            return true;
        }

    }

    //Read Global Variables
    public void readGlobals(){
        GlobalVariableDatabase dbHelperLive = new GlobalVariableDatabase(this);
        SQLiteDatabase dbLive = dbHelperLive.getReadableDatabase();


        String[] projectionLive = {
                FeederClass.FeedEntry.UserGender,
                FeederClass.FeedEntry.UserAge

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
            User_Gender = cursorLive.getString(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.UserGender));
            User_Age = cursorLive.getInt(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.UserAge));
        }
    }
}
