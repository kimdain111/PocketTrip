package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

public class PlanSpendCategory extends Activity {
    Button trafficBtn, hotelBtn, tourBtn, eatBtn, playBtn, etcBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plan_category_menu);

        hotelBtn = (Button)findViewById(R.id.hotel);
        trafficBtn = (Button)findViewById(R.id.plan_traffic);
        tourBtn = (Button)findViewById(R.id.tour);
        eatBtn = (Button)findViewById(R.id.eat);
        playBtn = (Button)findViewById(R.id.play);
        etcBtn = (Button)findViewById(R.id.etc);

        hotelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "숙소");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        trafficBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "교통계획");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        tourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "투어");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        eatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "식사");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "오락");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        etcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "계획소비기타");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }
}
