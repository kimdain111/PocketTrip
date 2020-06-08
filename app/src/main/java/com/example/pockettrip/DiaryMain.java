package com.example.pockettrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryMain extends Activity {
    private RecyclerView listview;
    private Diary_Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main);

        init();

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

    private void init() {

        listview = findViewById(R.id.date_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("A");
        itemList.add("1");
        itemList.add("2");
        itemList.add("3");
        itemList.add("4");
        itemList.add("5");

        adapter = new Diary_Adapter(this, itemList, onClickItem);
        listview.setAdapter(adapter);

        Diary_Date_Decoration decoration = new Diary_Date_Decoration();
        listview.addItemDecoration(decoration);
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() { //날짜 선택
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            Toast.makeText(DiaryMain.this, str, Toast.LENGTH_SHORT).show();
        }
    };
}
