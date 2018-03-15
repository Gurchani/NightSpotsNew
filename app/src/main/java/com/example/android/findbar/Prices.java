package com.example.android.findbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;

public class Prices extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GreenAdapter greenAdapter;
    private LinearLayoutManager mLayoutManager;
    private int itemQuantity = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices);
        setTheScreenSize();

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        greenAdapter = new GreenAdapter(itemQuantity);
        mRecyclerView.setAdapter(greenAdapter);
    }

    private void setTheScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Button cancelButton = findViewById(R.id.cancelButton);
        Button doneButton = findViewById(R.id.doneButton);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width2 = displaymetrics.widthPixels / 2;
        doneButton.setWidth(width2);
        cancelButton.setWidth(width2);
    }
}
