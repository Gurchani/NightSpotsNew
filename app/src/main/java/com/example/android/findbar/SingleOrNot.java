/**
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.example.android.findbar.R.styleable.View;

public class SingleOrNot extends AppCompatActivity {

    //Choices made by user through checkboxes
    boolean SingleGirls = false;
    boolean lessCrowded = false;
    boolean pintPrice = false;
    boolean similarity = false;

    //Values from Seekbars. Set the default value of girl boy choice here
    int ChooseByGirlsOrBoys = 0; //values close to 0 means user prefers girl and close 100 means boys
    int Crowdedness = 0;

    //Values Chosen Toggle Buttons
    int Singleness;

    String relationshipStatus = "true" ;


    //Information about user
    String User_Gender ;
    int User_Age;
    String User_id;

    //Checkbox
    private CheckBox LowPrice, WithSingleGirls, SimilarToMe, LessCrowded ;
    private Button done;
    private Button slidingDrawer;

    //SeekBars
    private SeekBar mGirlsmBoys ;
    private SeekBar CrowdedOrNot ;

    //Toggle Buttons
    private ToggleButton SearchSingles;

    private AppDrawer appDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_or_not);
        Button done = (Button)findViewById(R.id.Done);
        slidingDrawer = (Button)findViewById(R.id.open_drawer);

        //CheckBoxes
        LowPrice = (CheckBox) findViewById(R.id.pintPrice);
        WithSingleGirls = (CheckBox) findViewById(R.id.SingleGirls);
        SimilarToMe = (CheckBox) findViewById(R.id.SimilarToMe);

        //Read the values from the previously collected facebook data
        Intent SecondIntent = getIntent();
        User_Gender = SecondIntent.getStringExtra("User_Gender");
        User_Age = SecondIntent.getIntExtra("User_Age", 0);
        User_id = SecondIntent.getStringExtra("User_id");

        //Seekbar
        mGirlsmBoys =(SeekBar)findViewById(R.id.MoreGirlsMoreBoys);
        CrowdedOrNot = (SeekBar) findViewById(R.id.Crowded);
        SearchSingles = (ToggleButton) findViewById(R.id.SinglesButton);


        if (CheckerDataAlreadyExists()){
            ChangeCheckBoxDefaultValues();
        }
        addListenerOnButton();

        addListenerOnSlider();

        appDrawer = new AppDrawer(this);



        mGirlsmBoys.setOnSeekBarChangeListener(
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
        CrowdedOrNot.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener(){

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Crowdedness = progress;
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
                    Singleness = 1;
                } else {
                    Singleness = 0;
                }
            }
        });
    }



    public void addListenerOnSlider(){
        slidingDrawer = (Button)findViewById(R.id.open_drawer);
        slidingDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appDrawer.switchDrawer(1);
            }
        });
    }

    /**
     * Sets the values of the variables by reading the choices in the checkboxes
     */
    public void addListenerOnButton(){
        done = (Button) findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(LowPrice.isChecked()){
                    pintPrice = true;
                } else {
                    pintPrice = false;
                }
                if(WithSingleGirls.isChecked()){
                    SingleGirls = true;
                } else {
                    SingleGirls = false;
                }
                if(SimilarToMe.isChecked()){
                    similarity = true ;
                } else {
                    similarity = false;
                }
                if(LessCrowded.isChecked()){
                    lessCrowded = true;
                } else {
                    lessCrowded = false;
                }

                CheckBoxDatabase dbHelperWriter = new CheckBoxDatabase(getApplicationContext());
                SQLiteDatabase db = dbHelperWriter.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(FeederClass.FeedEntry.PintPriceChecked, convertToInt(pintPrice));
                contentValues.put(FeederClass.FeedEntry.LessCrowdedChecked, convertToInt(lessCrowded));
                contentValues.put(FeederClass.FeedEntry.SimilarChecked, convertToInt(similarity));
                contentValues.put(FeederClass.FeedEntry.mGirlsmBoys, ChooseByGirlsOrBoys);
                contentValues.put(FeederClass.FeedEntry.SingleGirlsChecked, convertToInt(SingleGirls));
                contentValues.put(FeederClass.FeedEntry.Singleness, Singleness);
                contentValues.put(FeederClass.FeedEntry.CrowdLevel, Crowdedness);


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

        Intent intent = new Intent(this, ProgressBarActivity.class);
        intent.putExtra("SingleGirls",SingleGirls);
        intent.putExtra("PintPrice",pintPrice);
        intent.putExtra("LessCrowded",lessCrowded);
        intent.putExtra("SimilarToMe",similarity);

        intent.putExtra("UserGender",User_Gender);
        intent.putExtra("UserAge", User_Age);
        startActivity(intent);

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
     * @return
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
                FeederClass.FeedEntry.CrowdLevel

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
                FeederClass.FeedEntry.CrowdLevel

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

//        while (cursor.moveToNext()) {
//            LowPrice.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.PintPriceChecked))));
//            LessCrowded.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.LessCrowdedChecked))));
//            WithSingleGirls.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SingleGirlsChecked))));
//            mGirlsmBoys.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.mGirlsmBoys)));
//            SimilarToMe.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SimilarChecked))));
//            SearchSingles.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.Singleness))));
//            CrowdedOrNot.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.CrowdLevel)));
//
//        }

    }
    /**
     * Converts 1 or 0 into booleans
     * @param n
     * @return returns true or falso
     */
    private boolean convertIntToBool(int n){
        if (n == 1) {
            return true;
        } else
            return false;
    }

}


