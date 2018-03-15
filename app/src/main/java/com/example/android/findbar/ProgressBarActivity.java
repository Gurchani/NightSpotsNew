package com.example.android.findbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class ProgressBarActivity extends AppCompatActivity {

    //Choices made by user through tick boxes
    boolean SingleGirlsTicked;
    boolean PintPriceTicked;
    boolean LessCrowdedTicked;
    boolean SimilartoMeTicked;

    //Choices made by users through seekbars
    int GirlsOrBoys;
    boolean Singlenes;
    int CrowdLevel;
    int SimilarityLevel;
    int Age;
    int HappyHour;
    int Cheapest;

    //Information about user
    String User_Gender;
    int User_Age;

    //UI Stuff
    ProgressBar pb;


    //For Running the Async Task
    putDataFromServer putDatafromServer = new putDataFromServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        readGlobals();
        pb = (ProgressBar) findViewById(R.id.progressBar2);


        Intent intent = this.getIntent();

        /*SingleGirlsTicked = intent.getBooleanExtra("SingleGirls", true);
        PintPriceTicked = intent.getBooleanExtra("PintPrice", true);
        LessCrowdedTicked = intent.getBooleanExtra("LessCrowded", true);
        SimilartoMeTicked = intent.getBooleanExtra("SimilarToMe", false);*/

        getUserChoices();
        String sqlStatement = createSqlStatement();
        sendTheStatement(sqlStatement);
    }

    private void getUserChoices() {
        CheckBoxDatabase dbHelper = new CheckBoxDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                FeederClass.FeedEntry.LessCrowdedChecked,
                FeederClass.FeedEntry.SimilarChecked,
                FeederClass.FeedEntry.SingleGirlsChecked,
                FeederClass.FeedEntry.mGirlsmBoys,
                FeederClass.FeedEntry.Singleness,
                FeederClass.FeedEntry.PintPriceChecked,
        };

        Cursor cursor = db.query(
                FeederClass.FeedEntry.CheckBox,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (cursor.moveToNext()) {
            //SingleGirlsTicked = convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SingleGirlsChecked)));
            PintPriceTicked = convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.PintPriceChecked)));
            //LessCrowdedTicked = convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.LessCrowdedChecked)));
            SimilartoMeTicked = convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SimilarChecked)));
            GirlsOrBoys = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.mGirlsmBoys));
            Singlenes = convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.Singleness)));
            CrowdLevel = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.CrowdLevel));
        }

        cursor.close();
    }

    //Send the Sql Statement to the server and put the data in local sqlite database
    private void sendTheStatement(String sqlStatement) {
        String type = "GetSortedBars";
        putDatafromServer.execute(type, sqlStatement);
    }

    private String createSqlStatement() {
        String SGT = "";
        String PPT = "";
        String SMT = "";
        String LCT = "";

        /*if(SingleGirlsTicked){
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
        }*/
        double ImportanceMultiplier = GirlsOrBoys / 10;
        if (GirlsOrBoys <= 50) {
            if (Singlenes) {
                SGT = "(" + ImportanceMultiplier + "* SingleBoys/(TotalBoys + TotalGirls))";
            } else {
                SGT = "(" + ImportanceMultiplier + "* TotalBoys/(TotalBoys + TotalGirls))";
            }
        } else {
            if (Singlenes) {
                SGT = "(" + ImportanceMultiplier + "* SingleGirls/(TotalBoys + TotalGirls))";
            } else {
                SGT = "(" + ImportanceMultiplier + "* TotalGirls/(TotalBoys + TotalGirls))";
            }
        }
        if (PintPriceTicked || LessCrowdedTicked || SimilartoMeTicked) {
            SGT = SGT + " + ";
        }


        if (PintPriceTicked) {
            PPT = "(-0.2 * PintPrice)";
            if (LessCrowdedTicked || SimilartoMeTicked) {
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

        int crowdImportanceMultiplier = CrowdLevel / 10;
        if (CrowdLevel <= 50) {
            LCT = "+(" + crowdImportanceMultiplier + "* (TotalGirls + TotalBoys))";
            if (SimilartoMeTicked) {
                LCT = LCT + " + ";
            }
        } else {
            LCT = "+( (-1)* " + crowdImportanceMultiplier + "* (TotalGirls + TotalBoys))";
        }

        String SqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge, PintPrice FROM barlivedata" + " ORDER BY " + SGT + PPT + LCT + SMT + " DESC";
        //  + PPT + SMT + LCT + ")

        return SqlStatement;

    }

    //If needed
    public void moveToNextActivity() {
        Intent intent = new Intent(this, MapListFragment.class);
        startActivity(intent);
    }

    public void moveToSettingsActivity(View view) {
        Intent intent = new Intent(this, SingleOrNot.class);
        startActivity(intent);
    }

    //For Async Task to Insert Data in the Local Database
    public boolean insertData(int Rank, int id, int TotalBoys, int TotalGirls, int SingleBoys, int SingleGirls, int AvAge, double PintPrice) {
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
        return result != -1;

    }

    //Read Global Variables
    public void readGlobals() {
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

    private boolean convertIntToBool(int n) {
        return n == 1;
    }

    class putDataFromServer extends AsyncTask<String, Integer, String> {
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
}
