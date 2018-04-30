package com.example.android.findbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DescriptionActivity extends AppCompatActivity {
    SharedPreferences LoginStatusTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        getSupportActionBar().hide();

    }

    public void getStarted(View view) {
        LoginStatusTracker = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = LoginStatusTracker.edit();
        editor.putBoolean("DescriptionPage1", false);
        editor.commit();

        Intent toMainWindow = new Intent(DescriptionActivity.this, MainActivity.class);
        startActivity(toMainWindow);
    }
}
