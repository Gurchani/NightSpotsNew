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

/**
 * Created by Gurchani on 1/5/2017.
 */
public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String[][] dataResult;
    String result;
    BackgroundWorker(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        String serverIP = "http://barfinder.website/";

        String login_url = serverIP + "login.php";
        String localDataUpdataURL = serverIP + "getBarLocations.php";
        String enterPageDataURL = serverIP + "enterFbData.php";
        String enterUserDetailsURL = serverIP + "enterUserDetails.php";
        String enterBarDetails = serverIP + "cityBarData.php";
        String insertLiveData = serverIP + "insertLiveData.php";
        String takeOutLiveData = serverIP + "takeOutLiveData.php";
        String GetSortedBars = serverIP + "GetSortedBars.php";

        if(type.equals("Locator")) {
            try {
                String Longi = params[1];
                String Lati = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                /*
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("User_Lat", "UTF-8")+"="+ URLEncoder.encode(Lati, "UTF-8")+"&"
                        + URLEncoder.encode("User_Long", "UTF-8")+"="+ URLEncoder.encode(Longi, "UTF-8");
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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                     dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close(); */
                httpURLConnection.disconnect();
                String result = " j ";
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
                return "fuck";
            }
        }

        if(type.equals("getBarLocations")) {
            try {
                String City = params[1];
                URL url = new URL(localDataUpdataURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("City", "UTF-8")+"="+ URLEncoder.encode(City, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line ="";
                String result = "";
                String inserterResult = "";

                while((line = bufferedReader.readLine())!= null) {
                    result += " " + line;
                }
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    String id = jsonobject.getString("id");
                    String Name = jsonobject.getString("Name");
                    String Address = jsonobject.getString("Address");
                    String PricePint = jsonobject.getString("PricePint");
                    String Latitude = jsonobject.getString("Latitude");
                    String Longitude = jsonobject.getString("Longitude");
                    String Radius = jsonobject.getString("Radius");
                   boolean insetData =  insertData(Integer.valueOf(id) , Name, Address, Double.valueOf(PricePint), Double.valueOf(Latitude) , Double.valueOf(Longitude), Integer.valueOf(Radius), City);
                    if (insetData)
                    {
                        inserterResult = id + " " + Double.valueOf(Latitude) + " " + Double.valueOf(Longitude);
                    } else {
                        inserterResult = inserterResult + "false ";
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return inserterResult;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(type.equals("insertLikedPages")) {

            try {
                String FbID = params[1];
                String JsonPage = params[2];
                URL url = new URL(enterPageDataURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();



                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String post_data = URLEncoder.encode("FbID", "UTF-8")+"="+ URLEncoder.encode(FbID, "UTF-8")+"&"
                        + URLEncoder.encode("JsonPage", "UTF-8")+"="+ URLEncoder.encode(JsonPage, "UTF-8");
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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                    dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
        }

        if(type.equals("insertUserDetails")) {

            try {
                String FbID = params[1];
                String User_Gender = params[2];
                String relationshipStatus = params[3];
                String UserAge= params[4];

                URL url = new URL(enterUserDetailsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();



                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String post_data = URLEncoder.encode("FbID", "UTF-8")+"="+ URLEncoder.encode(FbID, "UTF-8")+"&"
                        + URLEncoder.encode("User_Gender", "UTF-8")+"="+ URLEncoder.encode(User_Gender, "UTF-8")+"&"
                        + URLEncoder.encode("UserRS", "UTF-8")+"="+ URLEncoder.encode(relationshipStatus, "UTF-8")+"&"
                        + URLEncoder.encode("UserAge", "UTF-8")+"="+ URLEncoder.encode(UserAge, "UTF-8");
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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                    dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
        }

        if(type.equals("barDetails")) {

            try {
                String cityCountry = params[1];

                URL url = new URL(enterBarDetails);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String post_data = URLEncoder.encode("CityCountry", "UTF-8")+"="+ URLEncoder.encode(cityCountry, "UTF-8");
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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                    dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
        }

        if(type.equals("insertLiveData")) {

            try {
                String email = params[1];
                String bar = params[2];
                URL url = new URL(insertLiveData);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String post_data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("bar", "UTF-8")+"="+ URLEncoder.encode(bar, "UTF-8");
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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                    dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
        }

        if(type.equals("takeOutLiveData")) {

            try {
                String FbID = params[1];
                String bar = params[2];
                URL url = new URL(takeOutLiveData);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String post_data = URLEncoder.encode("FbID", "UTF-8")+"="+ URLEncoder.encode(FbID, "UTF-8")+"&"
                        + URLEncoder.encode("bar", "UTF-8")+"="+ URLEncoder.encode(bar, "UTF-8");
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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                    dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
        }

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
                int counterV = 0;
                int counterH = 0;

                while((line = bufferedReader.readLine())!= null) {
                    result += line ;
                    dataResult[counterH][counterV] = line;
                    if (counterV < 2){
                        counterV++;
                    } else {
                        counterV = 0;
                        counterH++;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
               // MapsActivity mapsActivity = new MapsActivity();
               // mapsActivity.searchResult = result;
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "failed again";
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Bars in 1 Mile Range");
    }

    @Override
    protected void onPostExecute(String result) {

      //  Toast.makeText(this.context, result + " " , Toast.LENGTH_LONG).show();
        /*
        if (dataResult != null){

        for (int i = 0; i < dataResult[0].length; i++ ){
            for (int j = 0; j < 2 ; j++){
                result = result + " " + dataResult[i][j];
            }
        }

        String Answer = "";

        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(result);

        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            String id = jsonobject.getString("id");
            String Longitude= jsonobject.getString("Longitude");
            String Latitude = jsonobject.getString("Latitude");
            Answer = Answer + " " + id + Longitude +Latitude ;
        }
        } catch (JSONException e) {
            alertDialog.setMessage("Fuck");
            alertDialog.show();
            e.printStackTrace();
        } */
           //   alertDialog.setMessage(result);
           //   alertDialog.show();



    }


    private String jsonParser(JSONObject jsonObject , String title){
        try {
            String value = jsonObject.getString(title);
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean convertToBoolean(String value) {
        return (value.equalsIgnoreCase("male")) | (value.equalsIgnoreCase("true"));
    }
    public boolean insertData(int id, String Name, String Address, double PricePint, double Latitude , double Longitude , int Radius, String cityCountry){
        localDatabase dbHelper = new localDatabase(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeederClass.FeedEntry.barId, id);
        contentValues.put(FeederClass.FeedEntry.barName, Name);
        contentValues.put(FeederClass.FeedEntry.barAddress, Address);
        contentValues.put(FeederClass.FeedEntry.PricePint, PricePint);
        contentValues.put(FeederClass.FeedEntry.barLatitude, Latitude);
        contentValues.put(FeederClass.FeedEntry.barLongitude, Longitude);
        contentValues.put(FeederClass.FeedEntry.barRadius, Radius);
        contentValues.put(FeederClass.FeedEntry.barCityCountry, cityCountry);

        long result = db.replace(FeederClass.FeedEntry.tableName, null, contentValues);
        return result != -1;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}