package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

public class PlanImportCategory extends Activity {
    Button exchangeBtn, etcBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plan_import_category);

        exchangeBtn = (Button)findViewById(R.id.plan_exchange);
        etcBtn = (Button)findViewById(R.id.plan_etc);

        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "환전");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        etcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "기타");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }
}
