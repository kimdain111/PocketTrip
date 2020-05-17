package com.example.pockettrip;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import java.io.File;

public class DiaryPlus extends Activity {

    final int PICTURE_REQUEST_CODE = 100;

    ImageView imgList1, imgList2, imgList3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_plus);

        imgList1 = (ImageView)findViewById(R.id.imgList1);
        imgList2 = (ImageView)findViewById(R.id.imgList2);
        imgList3 = (ImageView)findViewById(R.id.imgList3);

        ImageButton galleryBtn = (ImageButton)findViewById(R.id.galleryBtn); //사진 버튼
        galleryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true); //사진 여러장 선택
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICTURE_REQUEST_CODE);
            }
        });

        Button okBtn = (Button)findViewById(R.id.okBtn); //확인 버튼
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DB에 넣기

                Intent myintent = new Intent(DiaryPlus.this,DiaryMain.class);
                startActivity(myintent);
                finish();
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.cancelBtn); //취소 버튼
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(DiaryPlus.this,DiaryMain.class);
                startActivity(myintent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICTURE_REQUEST_CODE){
            if(resultCode == RESULT_OK){

                //기존 이미지 지우기
                imgList1.setImageResource(0);
                imgList2.setImageResource(0);
                imgList3.setImageResource(0);

                //Uri 또는 ClipData를 가져옴
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                //이미지 URI를 이용하여 이미지뷰에 순서대로 세팅
                if(clipData!=null){
                    if(clipData.getItemCount() > 3){ //3장 초과로 선택하면
                        Toast.makeText(this, "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i=0;i<3; i++){
                            if(i<clipData.getItemCount()){
                                Uri uriOne = clipData.getItemAt(i).getUri();
                                switch (i){
                                    case 0:
                                        imgList1.setImageURI(uriOne);
                                        break;
                                    case 1:
                                        imgList2.setImageURI(uriOne);
                                        break;
                                    case 2:
                                        imgList3.setImageURI(uriOne);
                                        break;
                                }
                            }
                        }
                    }
                }else if(uri != null){
                    imgList1.setImageURI(uri);
                }

            }
        }
    }
}
