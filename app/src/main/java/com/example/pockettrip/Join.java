package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Join extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);
        Intent intent = getIntent();


        Button joinBtn = (Button)findViewById(R.id.joinBtn);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idText = (EditText)findViewById((R.id.idText));
                EditText pwText = (EditText)findViewById((R.id.pwText));
                EditText nameText = (EditText)findViewById((R.id.nameText));

                String id = idText.getText().toString();
                String pw = pwText.getText().toString();
                String name = nameText.getText().toString();

                Intent myintent = new Intent(Join.this,MainActivity.class);
                startActivity(myintent);
                finish();
            }
        });
    }
}
