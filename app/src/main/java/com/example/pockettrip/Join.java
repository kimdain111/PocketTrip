package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                EditText idText = (EditText)findViewById((R.id.idText));
                EditText pwText = (EditText)findViewById((R.id.pwText));
                EditText nameText = (EditText)findViewById((R.id.nameText));

                String id = idText.getText().toString();
                String pw = pwText.getText().toString();
                String name = nameText.getText().toString();
                HashMap user = new HashMap<>();
                user.put("id", id);
                user.put("pw", pw);
                user.put("name", name);

                Intent myintent = new Intent(Join.this,MainActivity.class);
                startActivity(myintent);
                finish();
            }
        });
    }
}
