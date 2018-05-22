package com.barfinder.android.findbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Prices extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GreenAdapter greenAdapter;
    private LinearLayoutManager mLayoutManager;
    private int itemQuantity = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices);

        DisplayMetrics dw = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dw);
        int width = dw.widthPixels;
        int height = dw.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));
        WindowManager.LayoutParams l = getWindow().getAttributes();
        l.dimAmount = 0.6f;
        getWindow().setAttributes(l);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

       /* mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        greenAdapter = new GreenAdapter(itemQuantity, this);
        mRecyclerView.setAdapter(greenAdapter);*/

        String[] foods = {"Beer", "Tap", "Tuna", "Candy", "Meatball", "Potato", "asd" , "asdwe", "qwe", "drftg"};
        ListAdapter buckysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);
        ListView buckysListView = findViewById(R.id.listViewer);
        buckysListView.setAdapter(buckysAdapter);

        buckysListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String food = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(Prices.this, food, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void setTheScreenSize() {


       /* Button cancelButton = findViewById(R.id.cancelButton);
        Button doneButton = findViewById(R.id.doneButton);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width2 = displaymetrics.widthPixels / 2;
        doneButton.setWidth(width2);
        cancelButton.setWidth(width2);*/
    }
    public void onListItemClick(int clickIndex){


    }
}
