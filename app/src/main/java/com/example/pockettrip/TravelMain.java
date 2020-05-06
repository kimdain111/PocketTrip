package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TravelMain extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_main);

        Intent intent = getIntent();
    }
}
