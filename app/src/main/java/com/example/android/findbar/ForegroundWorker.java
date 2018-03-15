package com.example.android.findbar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

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

/**
 * Created by Gurchani on 5/1/2017.
 */

public class ForegroundWorker extends AsyncTask<String,Integer,String>{
    Context context;
    AlertDialog alertDialog;
    String resul = "";
    ProgressBarActivity progressBarActivity;
    ForegroundWorker (Context ctx) {
        context = ctx;
    }

    @Override


    protected String doInBackground(String... params) {
        String type = params[0];
        String ip = "http://barfinder.website/";
        String GetSortedBars = ip + "GetSortedBars.php";

        if(type.equals("GetSortedBars")) {

            try {
                String sqlQry = params[1];
                URL url = new URL(GetSortedBars);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("sqlQry", "UTF-8")+"="+ URLEncoder.encode(sqlQry, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line ="";
                while((line = bufferedReader.readLine())!= null) {
                    result += " " + line;
                }
                JSONArray jsonArray = new JSONArray(result);

                String inserterResult = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    String id = jsonobject.getString("id");
                    String TotalBoys = jsonobject.getString("TotalBoys");
                    String TotalGirls = jsonobject.getString("TotalGirls");
                    String SingleBoys = jsonobject.getString("SingleBoys");
                    String SingleGirls = jsonobject.getString("SingleGirls");
                    String AvAge = jsonobject.getString("AvAge");

                    publishProgress(i);


                    boolean insetData =  insertData(i+1, Integer.valueOf(id) , Integer.valueOf(TotalBoys), Integer.valueOf(TotalGirls), Integer.valueOf(SingleBoys), Integer.valueOf(SingleGirls), Integer.valueOf(AvAge));
                    if (insetData)
                    {
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

                return inserterResult;

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

        if(type.equals("UpdateData")) {

            try {
                String sqlQry = params[1];
                URL url = new URL(GetSortedBars);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("sqlQry", "UTF-8")+"="+ URLEncoder.encode(sqlQry, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line ="";
                while((line = bufferedReader.readLine())!= null) {
                    result += " " + line;
                }
                JSONArray jsonArray = new JSONArray(result);

                String inserterResult = "";
                //int length = jsonArray.length();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    String id = jsonobject.getString("id");
                    String TotalBoys = jsonobject.getString("TotalBoys");
                    String TotalGirls = jsonobject.getString("TotalGirls");
                    String SingleBoys = jsonobject.getString("SingleBoys");
                    String SingleGirls = jsonobject.getString("SingleGirls");
                    String AvAge = jsonobject.getString("AvAge");


                    boolean updateData =  updateData(Integer.valueOf(id) , Integer.valueOf(TotalBoys), Integer.valueOf(TotalGirls), Integer.valueOf(SingleBoys), Integer.valueOf(SingleGirls), Integer.valueOf(AvAge));
                    if (updateData)
                    {
                        inserterResult = "true";
                    } else {
                        inserterResult = "false";
                    }
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // MapsActivity mapsActivity = new MapsActivity();
                // mapsActivity.searchResult = result;

                return inserterResult;

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
        return null;
    }

    @Override
    protected void onPreExecute() {
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        resul = "success";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    public boolean insertData(int Rank, int id, int TotalBoys , int TotalGirls ,int SingleBoys, int SingleGirls, int AvAge){
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

        long result = db.insert(FeederClass.FeedEntry.LiveTableName, null, contentValues);
        return result != -1;

    }

    public boolean updateData(int id, int TotalBoys , int TotalGirls ,int SingleBoys, int SingleGirls, int AvAge){
        LiveBarDatabase dbHelper = new LiveBarDatabase(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.TotalBoys, TotalBoys);
        contentValues.put(FeederClass.FeedEntry.TotalGirls, TotalGirls);
        contentValues.put(FeederClass.FeedEntry.SingleBoys, SingleBoys);
        contentValues.put(FeederClass.FeedEntry.SingleGirls, SingleGirls);
        contentValues.put(FeederClass.FeedEntry.AvAge, AvAge);

        String selection = FeederClass.FeedEntry.barId + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(
                FeederClass.FeedEntry.LiveTableName,
                contentValues,
                selection,
                selectionArgs);


        return count != -1;

    }
}
