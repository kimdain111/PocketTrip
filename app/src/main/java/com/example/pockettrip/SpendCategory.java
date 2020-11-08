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
    Button eatBtn, trafficBtn, lifeBtn, culBtn, souBtn, fasBtn, etcBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.category_menu);

        eatBtn = (Button)findViewById(R.id.eat);
        trafficBtn = (Button)findViewById(R.id.traffic);
        lifeBtn = (Button)findViewById(R.id.life);
        culBtn = (Button)findViewById(R.id.culture);
        souBtn = (Button)findViewById(R.id.souvenir);
        fasBtn = (Button)findViewById(R.id.fashion);
        etcBtn = (Button)findViewById(R.id.etc);

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

        culBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "문화");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        souBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "기념품");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        fasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "패션");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        etcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "지출기타");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }
}
