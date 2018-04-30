/*
 * SingleOrNot lets the users choose the options they want to chose for recomending them bars and clubs
 * The options include Bars with Single Girls
 * Bars with Single Guys
 * Less Crowded Bars
 * Pint Price
 * Similar to Me
 */
package com.example.android.findbar;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class SingleOrNot extends AppCompatActivity {

    //Choices made by user through checkboxes
    boolean SingleGirls = false;
    boolean lessCrowded = false;
    boolean pintPrice = false;
    boolean similarity = false;

    //Values from Seekbars. Set the default value of girl boy choice here
    int ChooseByGirlsOrBoys = 0; //values close to 0 means user prefers girl and close 100 means boys    
    String AgePreferance = "";

    //Values Chosen Toggle Buttons
    Boolean Singleness;
    String relationshipStatus = "true" ;

    //Gender Preferance
    String moreGirlsOrMoreBoys;


    //Information about user
    String User_Gender ;
    int User_Age;
    String User_id;
    //Changes on 26-4-2018
    String[] GenderSpinner = {"Plus de garçons", "Plus de filles"};
    String[] CrowdSpinner = {"Encombré", "Normal", "Pas bondé"};
    String[] AgeSpinner = {"18 à 25", "25 à 30", "30 à 40", "40 à 50", "Plus de 50"};


    //SeekBars
    SharedPreferences ChoiceTracker;
    SharedPreferences.Editor editor;
    //Checkbox
    private CheckBox LowPrice, WithSingleGirls, SimilarToMe, LessCrowded ;
    private CheckBox SearchSingles;
    private Button done;
    private SeekBar mGirlsmBoys ;
    private SeekBar CrowdedOrNot ;
    private SeekBar AgeSeekBar;


    //Toggle Buttons
    //private ToggleButton SearchSingles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_or_not);
        Button done = findViewById(R.id.Done);

        ChoiceTracker = getApplicationContext().getSharedPreferences("UserChoices", MODE_PRIVATE);
        String AgeChosen = ChoiceTracker.getString("AgePref", "18 à 25");
        String GenderChosen = ChoiceTracker.getString("GenderPref", "Plus de filles");
        Boolean pintPriceChoice = ChoiceTracker.getBoolean("PriceChosen", true);
        Boolean singlesnessChoice = ChoiceTracker.getBoolean("SinglenessChosen", true);


        editor = ChoiceTracker.edit();




        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.6f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //Gender Spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, GenderSpinner);
        final MaterialBetterSpinner GenderSpinner = findViewById(R.id.GenderSelection);
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
                moreGirlsOrMoreBoys = TranslatetoEnglish(GenderSpinner.getText().toString());
                Log.d("value", moreGirlsOrMoreBoys);
                editor.putString("GenderPref", GenderSpinner.getText().toString());
                editor.commit();


            }
        });

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, AgeSpinner);
        final MaterialBetterSpinner AgeSpinner = findViewById(R.id.AgeChoice);
        AgeSpinner.setAdapter(ageAdapter);
        AgeSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AgePreferance = TranslatetoEnglish(AgeSpinner.getText().toString());
                Log.d("value", AgePreferance);
                editor.putString("AgePref", AgeSpinner.getText().toString());
                editor.commit();

            }
        });

       

        //CheckBoxes
        LowPrice = findViewById(R.id.pintPrice);




        //Read the values from the previously collected facebook data
        Intent SecondIntent = getIntent();
        User_Gender = SecondIntent.getStringExtra("User_Gender");
        User_Age = SecondIntent.getIntExtra("User_Age", 0);
        User_id = SecondIntent.getStringExtra("User_id");

        //Seekbar
        SearchSingles = findViewById(R.id.SinglesButton);


        AgeSpinner.setText(AgeChosen);
        GenderSpinner.setText(GenderChosen);
        LowPrice.setChecked(pintPriceChoice);
        SearchSingles.setChecked(singlesnessChoice);



        if (CheckerDataAlreadyExists()){
            ChangeCheckBoxDefaultValues();
        }
        addListenerOnButton();


       /* mGirlsmBoys.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    ChooseByGirlsOrBoys = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            }
        );
        AgeSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener(){

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //AgePreferance = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        CrowdedOrNot.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener(){

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                       // Crowdedness = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        SearchSingles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Singleness = true;
                } else {
                    Singleness = false;
                }
            }
        });*/


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



    /**
     * Sets the values of the variables by reading the choices in the checkboxes
     */
    public void addListenerOnButton(){
        done = findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pintPrice = LowPrice.isChecked();
                Singleness = SearchSingles.isChecked();

                editor = ChoiceTracker.edit();


                editor.putBoolean("PriceChosen", pintPrice);
                editor.putBoolean("SinglenessChosen", Singleness);

                editor.commit();



               /* if(WithSingleGirls.isChecked()){
                    SingleGirls = true;
                } else {
                    SingleGirls = false;
                }*/
                //similarity = SimilarToMe.isChecked();
                /*if(LessCrowded.isChecked()){
                    lessCrowded = true;
                } else {
                    lessCrowded = false;
                }*/

                CheckBoxDatabase dbHelperWriter = new CheckBoxDatabase(getApplicationContext());
                SQLiteDatabase db = dbHelperWriter.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(FeederClass.FeedEntry.PintPriceChecked, convertToInt(pintPrice));
                contentValues.put(FeederClass.FeedEntry.LessCrowdedChecked, convertToInt(lessCrowded));
                contentValues.put(FeederClass.FeedEntry.SimilarChecked, convertToInt(similarity));
                contentValues.put(FeederClass.FeedEntry.mGirlsmBoys, ChooseByGirlsOrBoys);
                contentValues.put(FeederClass.FeedEntry.SingleGirlsChecked, convertToInt(SingleGirls));
                contentValues.put(FeederClass.FeedEntry.Singleness, Singleness);
                //contentValues.put(FeederClass.FeedEntry.CrowdLevel, Crowdedness);
                contentValues.put(FeederClass.FeedEntry.AvAge, AgePreferance);


                long result = db.replace(FeederClass.FeedEntry.CheckBox, null, contentValues);

                moveToNextActivity();
            }
        });
    }

    /**
     * Moves to the progress bar activity
     */
    public void moveToNextActivity(){

        if (User_id != null ){
         String FbID = User_id;
         String type = "insertUserDetails";

        BackgroundWorker background = new BackgroundWorker(this);
         background.execute(type, FbID , User_Gender , relationshipStatus, String.valueOf(User_Age));
        }

        this.finish();


       /* Intent intent = new Intent(this, MapListFragment.class);
        intent.putExtra("SingleGirls",SingleGirls);
        intent.putExtra("PintPrice",pintPrice);
        intent.putExtra("LessCrowded",lessCrowded);
        intent.putExtra("SimilarToMe",similarity);

        intent.putExtra("UserGender",User_Gender);
        intent.putExtra("UserAge", User_Age);
        startActivity(intent);*/

    }

    /**
     * Converts Boolean values to int, 0 = 'False' and 1 = 'True'
     * @param value true , false
     * @return int, 0 = 'False' and 1 = 'True'
     */
    private int convertToInt(boolean value){
        if (value){
            return 1;
        } else return 0;
    }

    /**
     * Checks if there is already some data in the checkbox database or not. So that we dont get an error if we try to read empty database
     * true if data already exists @return
     */
    private boolean CheckerDataAlreadyExists(){
        CheckBoxDatabase dbHelper = new CheckBoxDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                FeederClass.FeedEntry.LessCrowdedChecked,
                FeederClass.FeedEntry.SimilarChecked,
                FeederClass.FeedEntry.SingleGirlsChecked,
                FeederClass.FeedEntry.mGirlsmBoys,
                FeederClass.FeedEntry.PintPriceChecked,
                FeederClass.FeedEntry.Singleness,
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

        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;}

    }

    /**
     * Saved the choices of the user into an sqlite databse so that if he returns to the same window, he still sees the same
     * choices as he made before
     */
    private void ChangeCheckBoxDefaultValues(){
        CheckBoxDatabase dbHelper = new CheckBoxDatabase(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                FeederClass.FeedEntry.LessCrowdedChecked,
                FeederClass.FeedEntry.SimilarChecked,
                FeederClass.FeedEntry.SingleGirlsChecked,
                FeederClass.FeedEntry.mGirlsmBoys,
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
            LowPrice.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.PintPriceChecked))));
//            LessCrowded.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.LessCrowdedChecked))));
//            WithSingleGirls.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SingleGirlsChecked))));
            //mGirlsmBoys.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.mGirlsmBoys)));
            ChooseByGirlsOrBoys = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.mGirlsmBoys));
            // SimilarToMe.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SimilarChecked))));
            SearchSingles.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.Singleness))));

            //CrowdedOrNot.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.CrowdLevel)));
            //Crowdedness = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.CrowdLevel));

            //AgeSeekBar.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.AvAge)));
            AgePreferance = cursor.getString(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.AvAge));

        }

    }

    /**
     * Converts 1 or 0 into booleans
     * @param n
     * @return returns true or falso
     */
    private boolean convertIntToBool(int n){
        return n == 1;
    }

    private int convertToDropDownIndex(String text) {

        int index = 0;

        if (text.matches("Plus de garçons")) {
            return 0;
        } else if (text.matches("Plus de filles")) {
            return 1;
        }
        if (text.matches("18 à 25")) {
            return 0;
        }
        if (text.matches("25 à 30")) {
            return 1;
        }
        if (text.matches("30 à 40")) {
            return 2;
        }
        if (text.matches("40 à 50")) {
            return 3;
        }
        if (text.matches("Plus de 50")) {
            return 4;
        }

        return index;


    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

}



