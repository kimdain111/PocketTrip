package com.example.pockettrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText idText = (EditText)findViewById(R.id.idText);
        final EditText pwText = (EditText)findViewById(R.id.pwText);
        Button loginBtn = (Button)findViewById(R.id.loginBtn);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idText.getText().toString().equals(""))
                    Toast.makeText(MainActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                else if(pwText.getText().toString().equals(""))
                    Toast.makeText(MainActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                else{
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(idText.getText().toString()).exists()){ //아이디 일치
                                UserDTO user = dataSnapshot.child(idText.getText().toString()).getValue(UserDTO.class);
                                if(user.getPw().equals(pwText.getText().toString())){ //비번 확인
                                     Intent myintent = new Intent(MainActivity.this,TravelMain.class);
                                     startActivity(myintent);
                                     finish();
                                }
                                else
                                    Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "회원이 아닙니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }


            }
        });

        Button joinBtn = (Button)findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(MainActivity.this,Join.class);
                startActivity(myintent);
                finish();
            }
        });
    }
}
