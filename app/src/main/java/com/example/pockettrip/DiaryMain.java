package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DiaryMain extends Activity {
    private RecyclerView listview;
    private Diary_Adapter adapter;
    private String no;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");

        //1.execute메소드를 통해 AsyncTask실행
        DiaryMain.selectDate task = new DiaryMain.selectDate();
        task.execute(no);

        ImageButton addDiaryBtn = (ImageButton)findViewById(R.id.addDiary); //다이어리 추가 버튼
        addDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(DiaryMain.this,DiaryPlus.class);
                myintent.putExtra("no", no);
                startActivity(myintent);
                finish();
            }
        });
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() { //날짜 선택
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            Toast.makeText(DiaryMain.this, str, Toast.LENGTH_SHORT).show();
        }
    };

    class selectDate extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(DiaryMain.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");
            init(arr[0], arr[1]);
        }
        //3. 백그라운드작업 수행(execute메소드 호출할 때 사용된 파라미터 전달받음)
        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/diary_main.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "no=" + no;

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

    private void init(String d_date, String a_date) { //상단 리스트뷰에 날짜 보여주기
        System.out.println("###init들어옴###");
        listview = findViewById(R.id.date_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);
        //마지막 2글자 추출(day, 일)
        String d_day = d_date.substring(d_date.length()-2, d_date.length());
        String a_day = a_date.substring(a_date.length()-2, a_date.length());

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("A");
        for(int i=Integer.parseInt(d_day); i<=Integer.parseInt(a_day);i++){
            String day = String.valueOf(i);
            itemList.add(day);
        }

        adapter = new Diary_Adapter(this, itemList, onClickItem);
        listview.setAdapter(adapter);

        Diary_Date_Decoration decoration = new Diary_Date_Decoration();
        listview.addItemDecoration(decoration);
        System.out.println("###init끝남###");
    }
}
