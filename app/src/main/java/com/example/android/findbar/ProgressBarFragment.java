package com.example.android.findbar;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgressBarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgressBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressBarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SharedPreferences UsersChoices;
    SharedPreferences.Editor editor;

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
    int AgePreferance;
    int HappyHour;
    int Cheapest;

    //Information about user
    String User_Gender;
    int User_Age;

    //UI Stuff
    ProgressBar pb;

    //Get User Choices (27-4-2018)
    String AgeChosen;
    String GenderChosen;
    Boolean pintPriceChoice;
    Boolean singlesnessChoice;



    //For Running the Async Task
    putDataFromServer putDatafromServer = new putDataFromServer();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProgressBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressBarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressBarFragment newInstance(String param1, String param2) {
        ProgressBarFragment fragment = new ProgressBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        super.onCreate(savedInstanceState);

        UsersChoices = getApplicationContext().getSharedPreferences("UserChoices", MODE_PRIVATE);
        AgeChosen = UsersChoices.getString("AgePref", "18 à 25");
        GenderChosen = UsersChoices.getString("GenderPref", "Plus de filles");
        pintPriceChoice = UsersChoices.getBoolean("PriceChosen", true);
        singlesnessChoice = UsersChoices.getBoolean("SinglenessChosen", true);


        readGlobals();

        /*SingleGirlsTicked = intent.getBooleanExtra("SingleGirls", true);
        PintPriceTicked = intent.getBooleanExtra("PintPrice", true);
        LessCrowdedTicked = intent.getBooleanExtra("LessCrowded", true);
        SimilartoMeTicked = intent.getBooleanExtra("SimilarToMe", false);*/
        deletepreviousDatafromLiveBarDatabase();
        //getUserChoices();
        String sqlStatement = makeSqlStatement();
        sendTheStatement(sqlStatement);
    }

    private void deletepreviousDatafromLiveBarDatabase() {
        LiveBarDatabase dbHelper = new LiveBarDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM LiveBarData");
        db.close();
    }

    /*private void getUserChoices() {
        CheckBoxDatabase dbHelper = new CheckBoxDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        String[] projection = {
                FeederClass.FeedEntry.LessCrowdedChecked,
                FeederClass.FeedEntry.SimilarChecked,
                FeederClass.FeedEntry.SingleGirlsChecked,
                FeederClass.FeedEntry.mGirlsmBoys,
                FeederClass.FeedEntry.Singleness,
                FeederClass.FeedEntry.PintPriceChecked,
                FeederClass.FeedEntry.CrowdLevel,
                FeederClass.FeedEntry.AvAge
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
            AgePreferance = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.AvAge));

        }
    }*/

    private void sendTheStatement(String sqlStatement) {
        String type = "GetSortedBars";
        putDatafromServer.execute(type, sqlStatement);
    }

    //This is the newest Sql Statement (TimeStamp: 27-4-2018)
    private String makeSqlStatement() {


        String AgeCondition = " AvAge = " + "\"" + TranslatetoEnglish(AgeChosen) + "\"" + " AND";
        String GenderCondition = "";
        String PriceCondition = "";
        String SinglenessCondition = "";


        if (TranslatetoEnglish(GenderChosen).matches("More Boys")) {
            GenderCondition = " 100 * (TotalBoys/(TotalGirls + TotalBoys)) > 50 AND";
        } else {
            GenderCondition = " 100 * (TotalGirls/(TotalGirls + TotalBoys)) > 50 AND";
        }

        if (pintPriceChoice) {
            PriceCondition = " PintPrice < 5";
        } else PriceCondition = " PintPrice > 1";

        if (singlesnessChoice) {
            if (TranslatetoEnglish(GenderChosen).matches("More Boys")) {
                SinglenessCondition = " AND 100 * SingleBoys/TotalBoys > 50 ";
            } else {
                SinglenessCondition = " AND 100 * SingleGirls/TotalGirls > 50 ";
            }
        } else {
            SinglenessCondition = "";
        }

        String sqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge, PintPrice FROM barlivedata where " + AgeCondition + GenderCondition + PriceCondition + SinglenessCondition;
        return sqlStatement;
    }


    /*private String createSqlStatement() {
        String SGT = "";
        String PPT = "";
        String SMT = "";
        String LCT = "";

        *//*if(SingleGirlsTicked){
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
        }*//*
        double ImportanceMultiplier = GirlsOrBoys / 10;
        if (GirlsOrBoys <= 50) {
            if (Singlenes) {
                SGT = "(" + ImportanceMultiplier + "* SingleBoys/(TotalBoys + TotalGirls)) DESC";
            } else {
                SGT = "(" + ImportanceMultiplier + "* TotalBoys/(TotalBoys + TotalGirls)) DESC";
            }
        } else {
            if (Singlenes) {
                SGT = "(" + ImportanceMultiplier + "* SingleGirls/(TotalBoys + TotalGirls)) DESC";
            } else {
                SGT = "(" + ImportanceMultiplier + "* TotalGirls/(TotalBoys + TotalGirls)) DESC";
            }
        }
        if (PintPriceTicked) {
            SGT = SGT + " + ";
        }


        if (PintPriceTicked) {
            PPT = "(-0.2 * PintPrice)";
            if (LessCrowdedTicked) {
                PPT = PPT + " + ";
            }
            *//*if (SingleGirlsTicked){
                PPT = "+(0.2 * PintPrice)";
            } else {
                PPT = "(0.2 * PintPrice)";
            }*//*


        } else {
            PPT = "";
        }

        int crowdImportanceMultiplier = CrowdLevel / 10;
        if (CrowdLevel <= 50) {
            LCT = ",(" + crowdImportanceMultiplier + "* (TotalGirls + TotalBoys))";
            *//*if (SimilartoMeTicked) {
                LCT = LCT + " + ";
            }*//*
        } else {
            LCT = "+( (-1)* " + crowdImportanceMultiplier + "* (TotalGirls + TotalBoys))";
        }

        String SqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge, PintPrice FROM barlivedata" + " ORDER BY " + SGT + PPT + LCT + SMT + " DESC";
        //  + PPT + SMT + LCT + ")

        return SqlStatement;

    }
*/
    public void moveToNextActivity() {
        MapListFragment activity = (MapListFragment) getActivity();
        activity.changeViewType();
    }

    //For Async Task to Insert Data in the Local Database
    public boolean insertData(int Rank, int id, int TotalBoys, int TotalGirls, int SingleBoys, int SingleGirls, String AvAge, double PintPrice) {
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

    private String TranslatetoEnglish(String s) {
        String EnglishTranslation = "";
        if (s.matches("Plus de garçons")) {
            return "More Boys";
        }
        if (s.matches("Plus de filles")) {
            return "More Girls";
        }
        if (s.matches("18 à 25")) {
            return "18 to 25";
        }
        if (s.matches("25 à 30")) {
            return "25 to 30";
        }
        if (s.matches("30 à 40")) {
            return "30 to 40";
        }
        if (s.matches("40 à 50")) {
            return "40 to 50";
        }
        if (s.matches("Plus de 50")) {
            return "More than 50";
        }

        return EnglishTranslation;


    }

    //Read Global Variables
    public void readGlobals() {
        GlobalVariableDatabase dbHelperLive = new GlobalVariableDatabase(getApplicationContext());
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_bar, container, false);
        pb = view.findViewById(R.id.progressBar4);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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


                        boolean insetData = insertData(i + 1, Integer.valueOf(id), Integer.valueOf(TotalBoys), Integer.valueOf(TotalGirls), Integer.valueOf(SingleBoys), Integer.valueOf(SingleGirls), AvAge, Double.valueOf(PintPrice));
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
