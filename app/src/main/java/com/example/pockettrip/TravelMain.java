package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TravelMain extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_main);

        //Intent intent = getIntent();

        ImageButton choiceBtn = (ImageButton)findViewById(R.id.addTravel);
        choiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(TravelMain.this, TravelChoice.class);
                startActivity(myintent);
                finish();
            }
        });
    }
}
