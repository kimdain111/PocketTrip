package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class DiaryMain extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main);

        ImageButton addDiaryBtn = (ImageButton)findViewById(R.id.addDiary); //다이어리 추가 버튼
        addDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(DiaryMain.this,DiaryPlus.class);
                startActivity(myintent);
                finish();
            }
        });
    }
}
