package com.example.android.findbar;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class MapListFragment extends AppCompatActivity implements BarMapView.OnFragmentInteractionListener ,TestFragment.OnFragmentInteractionListener {
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list_fragment);

        //Default View
        fragment = new TestFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mapOrList, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
    public void changeViewType(View view){

        boolean checked = ((ToggleButton)view).isChecked();

        if(checked){
            //List View
            fragment = new ListViewFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.mapOrList, fragment);
            ft.addToBackStack(null);
            ft.commit();

        }
        else {
            //Map View
            fragment = new TestFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.mapOrList, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    public void changeSettings(View view){
        Intent ThirdIntent = new Intent(MapListFragment.this, SingleOrNot.class);
        startActivity(ThirdIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
