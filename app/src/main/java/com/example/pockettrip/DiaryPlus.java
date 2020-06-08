package com.example.pockettrip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class DiaryPlus extends Activity {

    final int PICTURE_REQUEST_CODE = 100;
    private int mYear, mMonth, mDay;
    private Button chDate;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    ImageView imgList1, imgList2, imgList3;
    private EditText titleText, contentText;
    private RadioGroup weatherGroup, emotionGroup;
    private String weather="weather1", emotion="emotion1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_plus);

        imgList1 = (ImageView)findViewById(R.id.imgList1);
        imgList2 = (ImageView)findViewById(R.id.imgList2);
        imgList3 = (ImageView)findViewById(R.id.imgList3);

        titleText = (EditText)findViewById(R.id.titleText);
        contentText = (EditText)findViewById(R.id.contentText);

        weatherGroup = (RadioGroup)findViewById(R.id.weatherGroup);
        emotionGroup = (RadioGroup)findViewById(R.id.emotionGroup);

        this.InitializeView();
        this.InitializeListener();

        //라디오버튼 값 가져오기
        weatherGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.weather1:
                        weather = "weather1";
                        break;
                    case R.id.weather2:
                        weather = "weather2";
                        break;
                    case R.id.weather3:
                        weather = "weather3";
                        break;
                    case R.id.weather4:
                        weather = "weather4";
                        break;
                    case R.id.weather5:
                        weather = "weather5";
                        break;
                }
            }
        });

        emotionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.emotion1:
                        emotion = "emotion1";
                        break;
                    case R.id.emotion2:
                        emotion = "emotion2";
                        break;
                    case R.id.emotion3:
                        emotion = "emotion3";
                        break;
                    case R.id.emotion4:
                        emotion = "emotion4";
                        break;
                    case R.id.emotion5:
                        emotion = "emotion5";
                        break;
                }
            }
        });

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

    }

    public void InitializeView(){
        chDate = (Button)findViewById(R.id.chDate);
    }

    public void InitializeListener(){
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int datOfMonth) {
                chDate.setText(year + "년"+(monthOfYear+1)+"월"+datOfMonth+"일");
            }
        };
    }

    //취소버튼 눌렀을 때
    public void cancel(View view){
        Intent myintent = new Intent(DiaryPlus.this,DiaryMain.class);
        startActivity(myintent);
        finish();
    }

    //날짜버튼 눌렀을 때
    public void OnClickHandler(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, mYear, mMonth , mDay);
        dialog.show();
    }

    //확인버튼 눌렀을 때
    public void insert(View view){
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();

        if(title.equals(""))
            Toast.makeText(DiaryPlus.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(content.equals(""))
            Toast.makeText(DiaryPlus.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            //1.execute메소드를 통해 AsyncTask실행
            DiaryPlus.InsertData task = new DiaryPlus.InsertData();
            task.execute(title, content, weather, emotion);
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(DiaryPlus.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("diary plus success")){
                Toast.makeText(getApplicationContext(),"다이어리가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(DiaryPlus.this,DiaryMain.class);
                startActivity(myintent);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }
        //3. 백그라운드작업 수행(execute메소드 호출할 때 사용된 파라미터 전달받음)
        @Override
        protected String doInBackground(String... params) {
            try{
                String title = (String) params[0];
                String content = (String) params[1];
                String weather = (String) params[2];
                String emotion = (String) params[3];

                String link = "http://cs2020tv.dongyangmirae.kr/diary_plus.php"; //=(String)params[0];
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "title=" + title + "&content=" + content + "&weather=" + weather + "&emotion=" + emotion;

                //HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                //수신되는 데이터 저장
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine())!= null){
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }
        }
    }

    //갤러리 통해서 사진 받아오기
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
