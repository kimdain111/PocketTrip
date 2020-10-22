package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ExchangeMoney extends Activity {

    Button exchangeBtn, submitBtn, cancelBtn;
    EditText otherMoney;
    TextView koreaMoney, otherCountry;
    String rate, country;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exchange_money);

        Intent intent = getIntent();
        rate = intent.getExtras().getString("rate");
        country = intent.getExtras().getString("country");

        exchangeBtn = (Button) findViewById(R.id.exchange);
        submitBtn = (Button) findViewById(R.id.ex_submit);
        cancelBtn = (Button) findViewById(R.id.cancel);
        otherMoney = (EditText) findViewById(R.id.otherMoney);
        koreaMoney = (TextView) findViewById(R.id.koreaMoney);
        otherCountry = (TextView) findViewById(R.id.otherCountry);

        otherCountry.setText(country);

        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float other = Float.parseFloat(otherMoney.getText().toString());
                koreaMoney.setText(Float.toString(other*Float.parseFloat(rate)));
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", koreaMoney.getText().toString());
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
