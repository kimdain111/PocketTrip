package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleMain extends Activity {
    private String no, id, selectDate = "A";
    private RecyclerView listview;
    private Diary_Adapter adapter;
    ImageButton addScheduleBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");
        id = intent.getExtras().getString("id");

        //1.execute메소드를 통해 AsyncTask실행
        //상단 날짜 리스트뷰 띄우기
        selectDate task = new selectDate();
        task.execute(no);

        //다이어리 조회
        ScheduleMainData task2 = new ScheduleMainData();
        task2.execute(no, selectDate);

        addScheduleBtn = (ImageButton)findViewById(R.id.addSchedule); //다이어리 추가 버튼
        addScheduleBtn.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() { //뒤로가기
        Intent intent = new Intent(ScheduleMain.this,TravelDetail.class);
        intent.putExtra("id", id);
        intent.putExtra("no", no);
        startActivity(intent);
        finish();
    }

    //일정 추가 버튼
    public void schedulePlus(View view)
    {
        Intent myIntent = new Intent(ScheduleMain.this, SchedulePlus.class);
        myIntent.putExtra("id", id);
        myIntent.putExtra("no", no);
        myIntent.putExtra("selectDate", selectDate);
        startActivity(myIntent);
        finish();
    }

    //날짜 버튼 클릭
    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectDate = (String) v.getTag();
            if(selectDate.equals("A")) addScheduleBtn.setVisibility(View.GONE);
            else addScheduleBtn.setVisibility(View.VISIBLE);

            ScheduleMainData task3 = new ScheduleMainData();
            task3.execute(no, selectDate);
        }
    };


    //여행기간 날짜 조회
    class selectDate extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ScheduleMain.this, "Please Wait", null, true, true);
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

                String link = "http://cs2020tv.dongyangmirae.kr/dateList.php";
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

    //상단 리스트뷰에 날짜 보여주기
    private void init(String d, String a) {
        listview = findViewById(R.id.date_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);

        //년 추출
        int d_year = Integer.parseInt(d.substring(0,4));
        int a_year = Integer.parseInt(a.substring(0,4));

        //월 추출
        int d_month = Integer.parseInt(d.substring(5,7));
        int a_month = Integer.parseInt(a.substring(5,7));

        //일 추출
        int d_day = Integer.parseInt(d.substring(d.length()-2));
        int a_day = Integer.parseInt(a.substring(a.length()-2));

        Calendar dCal = new GregorianCalendar(d_year, d_month-1, d_day); //출발
        Calendar aCal = new GregorianCalendar(a_year, a_month-1, a_day); //도착
        //말일 날짜 구하기
        int maxDay = dCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //두 날짜의 차이 구하기
        long diffSec = (aCal.getTimeInMillis() - dCal.getTimeInMillis())/1000;
        long diffDays = diffSec / (24*60*60);

        ArrayList<String> itemList = new ArrayList<>();
        ArrayList<String> itemPrintList = new ArrayList<>();
        itemList.add("A");
        itemPrintList.add("A");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date;

        while(diffDays >= 0){
            date = sdf.format(dCal.getTime());
            String day = date.substring(date.length()-2);
            if(Integer.parseInt(day) > maxDay){ //말일을 넘어가면
                dCal.add(Calendar.MONTH, 1); //월+1
                dCal.set(Calendar.DAY_OF_MONTH, 1); //1일로 설정
                date = sdf.format(dCal.getTime());
            }

            itemList.add(date);
            itemPrintList.add(date.substring(date.length()-2));
            dCal.add(Calendar.DAY_OF_MONTH, 1);
            diffDays--;
        }

        adapter = new Diary_Adapter(this, itemList,itemPrintList, onClickItem);
        listview.setAdapter(adapter);

        Diary_Date_Decoration decoration = new Diary_Date_Decoration();
        listview.addItemDecoration(decoration);
    }

    //해당 날짜의 일정 조회
    class ScheduleMainData extends AsyncTask<String, Void, String>{
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ScheduleMain.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");
            final String[] no = new String[arr.length/2]; //2컬럼이 한묶음

            TableLayout table = findViewById(R.id.table); //일정 테이블
            table.removeAllViews();
            TextView text = findViewById(R.id.noSchedule); //다이어리 없음 텍스트
            final TableRow tr[] = new TableRow[arr.length/2];

            if(s.equals("no data")){
                text.setVisibility(View.VISIBLE);
                table.setVisibility(View.GONE);
            }
            else{
                text.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);

                int cnt = 0;

                for(int i=0; i<arr.length; i+=2)
                {
                    tr[cnt] = new TableRow(ScheduleMain.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    tr[cnt].setLayoutParams(lp);



                    TextView tText = new TextView(ScheduleMain.this);
                    tText.setText(arr[i].substring(0,5)+"       "+arr[i+1]); //시간, 내용
                    tText.setTextSize(20);
                    tText.setGravity(Gravity.LEFT);
                    tText.setPadding(0,60,0,0);

                    tr[cnt].addView(tText); //시간, 내용 행에 삽입
                    tr[cnt].setPadding(0,5,0,20);
                    tr[cnt].setClickable(true);

                    //no[cnt] = arr[i];
                    table.addView(tr[cnt],lp);
                    cnt++;
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String date = (String) params[1];

                String link = "http://cs2020tv.dongyangmirae.kr/schedule_main.php";
                String data = "no=" + no + "&date=" + date;

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

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
}
