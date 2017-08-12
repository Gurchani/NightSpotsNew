package com.example.android.findbar;

import android.provider.BaseColumns;

/**
 * Created by Gurchani on 3/31/2017.
 */
public class FeederClass {
    public FeederClass(){}

    public static abstract class FeedEntry implements BaseColumns

    {   //Table Names
        public static final String tableName = "BarDetails";
        public static final String LiveTableName = "LiveBarData";
        public static final String Globals = "Globals";
        public static final String CheckBox = "CheckBox";

        //Unique to local database
        public static final String barName = "Name";
        public static final String barAddress = "Address";
        public static final String PricePint = "PricePint";
        public static final String barLongitude = "barLongitude";
        public static final String barLatitude = "barLatitude";
        public static final String barRadius = "barRadius";
        public static final String barCityCountry = "barCityCountry";

        //Unique to Live Database

        public static final String Ranking = "Ranking";
        public static final String TotalBoys = "TotalBoys";
        public static final String TotalGirls = "TotalGirls";
        public static final String SingleGirls = "SingleGirls";
        public static final String SingleBoys = "SingleBoys";
        public static final String AvAge = "AvAge";
        public static final String PintPrice = "PintPrice";

        //Common to both tables
        public static final String barId = "barID";

        //Variables to Put into Gloabls
        public static final String UserGender = "UserGender";
        public static final String  UserAge= "UserAge";
        public static final String UserFbid = "UserFbid";

        //Variables for Checker Box
        public static final String SingleGirlsChecked = "SingleGirlsChecked";
        public static final String PintPriceChecked= "PintPriceChecked";
        public static final String LessCrowdedChecked = "LessCrowdedChecked";
        public static final String SimilarChecked = "SimilarChecked";

        //Variables for Seekbars
        public static final String mGirlsmBoys = "MoreGirlsMoreBoys";
        public static final String Similarity = "SimilarToMe";
        public static final String CrowdLevel = "CrowdedOrNot";
        public static final String Age = "Age";

        //Variable through Buttons
        public static final String Singleness = "Singularity";
        public static final String cheapness = "Cheapest";
        public static final String Happyhour = "Happyhour";

    }
}
