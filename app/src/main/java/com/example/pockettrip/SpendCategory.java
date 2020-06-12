package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SpendCategory extends Activity {
    Button eatBtn, trafficBtn, lifeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.category_menu);

        eatBtn = (Button)findViewById(R.id.eat);
        trafficBtn = (Button)findViewById(R.id.traffic);
        lifeBtn = (Button)findViewById(R.id.life);

        eatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "식비");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        trafficBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "교통");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        lifeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "생활");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }
}
