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
import android.widget.Toast;

import static com.example.android.findbar.R.styleable.View;

public class SingleOrNot extends AppCompatActivity {

    //Choices made by user through checkboxes
    boolean SingleGirls = false;
    boolean lessCrowded = false;
    boolean pintPrice = false;
    boolean similarity = false;

    String relationshipStatus = "true" ;


    //Information about user
    String User_Gender ;
    int User_Age;
    String User_id;

    //Checkbox
    private CheckBox LowPrice, WithSingleGirls, SimilarToMe, LessCrowded ;
    private Button done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_or_not);
        Button done = (Button)findViewById(R.id.Done);

        //CheckBoxes
        LowPrice = (CheckBox) findViewById(R.id.pintPrice);
        WithSingleGirls = (CheckBox) findViewById(R.id.SingleGirls);
        SimilarToMe = (CheckBox) findViewById(R.id.SimilarToMe);
        LessCrowded = (CheckBox) findViewById(R.id.LessCrowded);

        //Read the values from the previously collected facebook data
        Intent SecondIntent = getIntent();
        User_Gender = SecondIntent.getStringExtra("User_Gender");
        User_Age = SecondIntent.getIntExtra("User_Age", 0);
        User_id = SecondIntent.getStringExtra("User_id");

        if (CheckerDataAlreadyExists()){
            ChangeCheckBoxDefaultValues();
        }
        addListenerOnButton();
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
                contentValues.put(FeederClass.FeedEntry.SingleGirlsChecked, convertToInt(SingleGirls));


                long result = db.replace(FeederClass.FeedEntry.CheckBox, null, contentValues);

                moveToNextActivity();
            }
        });
    }
    /**
     * Moves to the next activity where the map is visible
     */
    public void moveToNextActivity(){

        if (User_id != null ){
         String FbID = User_id;
         String type = "insertUserDetails";

        BackgroundWorker background = new BackgroundWorker(this);
         background.execute(type, FbID , User_Gender , relationshipStatus );
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
                FeederClass.FeedEntry.PintPriceChecked

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
                FeederClass.FeedEntry.PintPriceChecked

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
            LessCrowded.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.LessCrowdedChecked))));
            WithSingleGirls.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SingleGirlsChecked))));
            SimilarToMe.setChecked(convertIntToBool(cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.SimilarChecked))));

        }

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
