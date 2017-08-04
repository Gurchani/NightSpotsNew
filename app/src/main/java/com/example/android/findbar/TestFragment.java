package com.example.android.findbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.util.concurrent.ExecutionException;

import static com.example.android.findbar.R.id.map;
import static com.example.android.findbar.R.id.textView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap mMap;
    MapView mapView;
    View mView;

    //My own Variables

    boolean SingleGirlsTicked;
    boolean PintPriceTicked;
    boolean LessCrowdedTicked;
    boolean SimilartoMeTicked;

    //Info about User
    int User_Age;
    String User_Gender;

    //Info about Bar
    String BarName;
    double pintPrice;
    double avAge;
    int SingleGirls;
    int SingleBoys;

    String GenderRatio;

    JSONArray sqlBarFullData;
    ForegroundWorker foregroundWorker = new ForegroundWorker(getActivity());
    ForegroundWorker foregroundUpdater = new ForegroundWorker(getActivity());

    private OnFragmentInteractionListener mListener;

    public TestFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
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
        Intent intent = getActivity().getIntent();

        SingleGirlsTicked = intent.getBooleanExtra("SingleGirlsTicked", true);
        PintPriceTicked = intent.getBooleanExtra("PintPriceTicked", true);
        LessCrowdedTicked = intent.getBooleanExtra("LessCrowdedTicked", true);
        SimilartoMeTicked = intent.getBooleanExtra("SimilarToMeTicked", true);

        User_Age = intent.getIntExtra("UserAge", 25);
        User_Gender = intent.getStringExtra("UserGender");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_test, container, false);
        return mView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        // Add a marker in Sydney and move the camera
        LatLng paris = new LatLng(48.897622, 2.344038);
        mMap.setMinZoomPreference(11.5f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));

        LatLngBounds parisBounds = new LatLngBounds(
                new LatLng (48.839738, 2.272453),
                new LatLng(48.898003, 2.413902));
// Constrain the camera target to the Paris bounds.
        mMap.setLatLngBoundsForCameraTarget(parisBounds);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return; have to un-comment
        }
//        mMap.setMyLocationEnabled(true);



        populateMap();
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



    public void populateMap() {
        readData();
        updateData();

    }

    //Reads both live and static data for bars
    public void readData(){
        localDatabase dbHelper = new localDatabase(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                FeederClass.FeedEntry.barId,
                FeederClass.FeedEntry.barLatitude,
                FeederClass.FeedEntry.barLongitude,
                FeederClass.FeedEntry.PricePint,
                FeederClass.FeedEntry.barName,
                FeederClass.FeedEntry.barAddress
        };
        String selection = FeederClass.FeedEntry.barCityCountry + " = ?";
        String[] selectionArgs = { "Paris,France" };

        Cursor cursor = db.query(
                FeederClass.FeedEntry.tableName,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );






        int GBU = 2; //Determine if the bar is Good(0)/Bad(1)/Ugly(2)

        int totalBars = cursor.getCount() ;
        int barRank = 0;

        while(cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barId));
            double Latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barLatitude));
            double Longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barLongitude));
            BarName = cursor.getString(cursor.getColumnIndexOrThrow(FeederClass.FeedEntry.barName));
            barRank = determineRank(id);

            GBU = DetermineGBU(barRank, totalBars);
            addMarkers(mMap, Latitude, Longitude, GBU);
        }
        cursor.close();
    }

    private int determineRank(int id) {


        LiveBarDatabase dbHelperLive = new LiveBarDatabase(getActivity());
        SQLiteDatabase dbLive = dbHelperLive.getReadableDatabase();


        String[] projectionLive = {
                FeederClass.FeedEntry.Ranking,
                FeederClass.FeedEntry.SingleBoys,
                FeederClass.FeedEntry.SingleGirls,
                FeederClass.FeedEntry.PintPrice

        };
        String selectionLive = FeederClass.FeedEntry.barId + " = ?";
        String[] selectionArgsLive = {String.valueOf(id)};

        Cursor cursorLive = dbLive.query(
                FeederClass.FeedEntry.LiveTableName,                     // The table to query
                projectionLive,                               // The columns to return
                selectionLive,                                // The columns for the WHERE clause
                selectionArgsLive,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        int Rank = 0;
        while (cursorLive.moveToNext()) {

            Rank = cursorLive.getInt(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.Ranking));
            SingleGirls = cursorLive.getInt(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.SingleGirls));
            SingleBoys =  cursorLive.getInt(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.SingleBoys));
            pintPrice = cursorLive.getDouble(cursorLive.getColumnIndexOrThrow(FeederClass.FeedEntry.PintPrice));

        }
        cursorLive.close();
        return Rank;

    }


    public void updateData(){
        String sqlStatement = "SELECT id, TotalBoys, TotalGirls, SingleBoys, SingleGirls, AvAge FROM barlivedata";
        String type = "UpdateData";
        if (foregroundUpdater.getStatus() != AsyncTask.Status.RUNNING && foregroundUpdater.getStatus() != AsyncTask.Status.PENDING ){
            String abc = String.valueOf(foregroundUpdater.getStatus());
            if (foregroundUpdater.getStatus() == AsyncTask.Status.FINISHED){
                foregroundUpdater.cancel(true);
            }
            foregroundUpdater.execute(type, sqlStatement);
        }
    }

    private void addMarkers(GoogleMap mMap, double lat, double lng, int GBU){
        mMap.setInfoWindowAdapter(this);
        LatLng paris = new LatLng(lat, lng);

        //Invert the values
        String snippet = "Single Boys:      " + SingleBoys
                + System.getProperty ("line.separator")
                + "Single Girls:      " + SingleGirls
                + System.getProperty ("line.separator")
                + "Pint Price:      " + pintPrice
                ;

        MarkerOptions markerOptions = new MarkerOptions().position(paris).title(BarName).snippet(snippet);


        if (GBU == 0){
            mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hotmarker));

            /*mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.infowindow, null);

                    TextView barName = (TextView) view.findViewById(R.id.barName);
                    barName.setText(BarName);

                    TextView sb = (TextView) view.findViewById(R.id.SingleBoys);
                    sb.setText(marker.getSnippet());

                    TextView sg = (TextView) view.findViewById(R.id.SingleGirls);
                    sg.setText("Single Girls: " + SingleGirls);
                    return view;

                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });*/
        }
        if(GBU == 1){
            mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.coldmarker));
        }
        if(GBU==2){
            mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.deadmarker));
        }
    }

    //DetemineGBU stands for finding out if a bar is Good, Bad or Ugly
    //If returned 0 its Good
    //if returned 1 its Bad
    //if returned 2 its Ugly

    private int DetermineGBU(int barRank, int TotalBars) {
        if (barRank <= TotalBars / 3) {
            return 0;
        } else
        if (barRank > TotalBars / 3 && barRank <= TotalBars * 2 / 3) {
            return 1;
        } else
        if (barRank > TotalBars * 2 / 3 && barRank <= TotalBars) {
            return 2;
        } else return 3;
    }



    private LatLng getCurrentLocation(){
        service s = new service();
        LatLng currentLocation = new LatLng(s.UserLat , s.UserLong);
        return currentLocation;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.infowindow, null);

        TextView barName = (TextView) view.findViewById(R.id.barName);
        barName.setText(marker.getTitle());

        TextView barStats = (TextView) view.findViewById(R.id.SingleGirls);
        barStats.setText(marker.getSnippet());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

}
