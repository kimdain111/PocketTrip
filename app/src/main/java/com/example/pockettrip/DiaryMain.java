package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;

public class DiaryMain extends Activity {
    private RecyclerView listview;
    private Diary_Adapter adapter;
    private String no, id;
    private String selectDate = "A";
    ImageButton addDiaryBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");
        id = intent.getExtras().getString("id");

        //1.execute메소드를 통해 AsyncTask실행
        //상단 날짜 리스트뷰 띄우기
        selectDate task = new selectDate();
        task.execute(no);

        //다이어리 조회
        DiaryMainData task2 = new DiaryMainData();
        task2.execute(no, selectDate);

        addDiaryBtn = (ImageButton)findViewById(R.id.addDiary); //다이어리 추가 버튼
        addDiaryBtn.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() { //뒤로가기
        Intent intent = new Intent(DiaryMain.this,TravelDetail.class);
        intent.putExtra("id", id);
        intent.putExtra("no", no);
        startActivity(intent);
        finish();
    }

    //마이페이지
    public void goMypage(View view)
    {
        Intent myIntent = new Intent(DiaryMain.this, MyPage.class);
        myIntent.putExtra("id", id);
        startActivity(myIntent);
        finish();
    }

    //다이어리 추가 버튼
    public void diaryPlus(View view)
    {
        Intent myIntent = new Intent(DiaryMain.this, DiaryPlus.class);
        myIntent.putExtra("id", id);
        myIntent.putExtra("no", no);
        myIntent.putExtra("selectDate", selectDate);
        myIntent.putExtra("flag", "false");
        startActivity(myIntent);
        finish();
    }

    //날짜 버튼 클릭
    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectDate = (String) v.getTag();
            if(selectDate.equals("A")) addDiaryBtn.setVisibility(View.GONE);
            else addDiaryBtn.setVisibility(View.VISIBLE);

            DiaryMainData task3 = new DiaryMainData();
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

    //해당 날짜의 다이어리 조회
    class DiaryMainData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(DiaryMain.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");

            TableLayout table = findViewById(R.id.table); //다이어리 테이블
            table.removeAllViews();
            TextView text = findViewById(R.id.noDiary); //다이어리 없음 텍스트
            final TableRow tr[] = new TableRow[(arr.length/6)*3];

            if(s.equals("no data")){
                text.setVisibility(View.VISIBLE);
                table.setVisibility(View.GONE);
            }
            else{
                text.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);

                int cnt = 0;

                for(int i=0; i<arr.length; i+=6)
                {
                    tr[cnt] = new TableRow(DiaryMain.this);
                    tr[cnt+1] = new TableRow(DiaryMain.this);
                    tr[cnt+2] = new TableRow(DiaryMain.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    tr[cnt].setLayoutParams(lp);
                    tr[cnt+1].setLayoutParams(lp);
                    tr[cnt+2].setLayoutParams(lp);

                    ImageView tImg = new ImageView(DiaryMain.this);
                    final Bitmap[] bitmap = new Bitmap[1];
                    final int finalI = i;
                    Thread uThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                URL url = new URL(arr[finalI +4]);
                                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                conn.setDoInput(true);
                                conn.connect();
                                InputStream is = conn.getInputStream();
                                bitmap[0] = BitmapFactory.decodeStream(is);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    uThread.start();
                    try{
                        uThread.join();
                        tImg.setImageBitmap(bitmap[0]);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    tImg.setLayoutParams(new TableRow.LayoutParams(300,400));

                    TextView tText = new TextView(DiaryMain.this);
                    tText.setText(arr[i+5]+"       "); //날짜
                    tText.setTextSize(20);
                    tText.setGravity(Gravity.LEFT);
                    tText.setPadding(0,60,0,0);

                    ImageView weatherImg = new ImageView(DiaryMain.this); //날씨 이모티콘으로 변환
                    int wImg = 0;
                    if (arr[i+2].equals("weather1")){
                        wImg = R.drawable.sunny;
                    }else if (arr[i+2].equals("weather2")){
                        wImg = R.drawable.cloudy;
                    }else if (arr[i+2].equals("weather3")){
                        wImg = R.drawable.rain;
                    }else if (arr[i+2].equals("weather4")){
                        wImg = R.drawable.snow;
                    }else if (arr[i+2].equals("weather5")){
                        wImg = R.drawable.lightning;
                    }
                    weatherImg.setImageResource(wImg);
                    weatherImg.setLayoutParams(new TableRow.LayoutParams(80,140));
                    weatherImg.setPadding(0,60,20,0);
                    weatherImg.setScaleType(ImageView.ScaleType.FIT_XY);

                    ImageView emotionImg = new ImageView(DiaryMain.this); //감정 이모티콘으로 변환
                    int eImg = 0;
                    if (arr[i+3].equals("emotion1")){
                        eImg = R.drawable.emotion1;
                    }else if (arr[i+3].equals("emotion2")){
                        eImg = R.drawable.emotion2;
                    }else if (arr[i+3].equals("emotion3")){
                        eImg = R.drawable.emotion3;
                    }else if (arr[i+3].equals("emotion4")){
                        eImg = R.drawable.emotion4;
                    }else if (arr[i+3].equals("emotion5")){
                        eImg = R.drawable.emotion5;
                    }
                    emotionImg.setImageResource(eImg);
                    emotionImg.setLayoutParams(new TableRow.LayoutParams(80,140));
                    emotionImg.setPadding(0,60,0,0);
                    emotionImg.setScaleType(ImageView.ScaleType.FIT_XY);

                    TextView tText2 = new TextView(DiaryMain.this);
                    tText2.setText(arr[i]+"\n"+arr[i+1]); //제목, 내용
                    tText2.setTextSize(20);
                    tText2.setGravity(Gravity.LEFT);
                    tText2.setPadding(0,60,0,0);

                    tr[cnt].addView(tText); //날짜
                    tr[cnt].addView(weatherImg); //날씨
                    tr[cnt].addView(emotionImg); //감정
                    tr[cnt+1].addView(tImg);
                    tr[cnt+2].addView(tText2);
                    tr[cnt].setPadding(0,5,0,20);
                    tr[cnt].setClickable(true);
                    tr[cnt+1].setPadding(0,5,0,20);
                    tr[cnt+1].setClickable(true);
                    tr[cnt+2].setPadding(0,5,0,20);
                    tr[cnt+2].setClickable(true);

                    table.addView(tr[cnt],lp);
                    table.addView(tr[cnt+1],lp);
                    table.addView(tr[cnt+2],lp);
                    cnt+=3;
                }

                //롱클릭 - 수정/삭제
                for(int j=0; j<tr.length; j+=3)
                {
                    final int finalJ = j/3;
                    for(int k=0;k<3;k++){
                        tr[j+k].setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(DiaryMain.this);
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent2 = new Intent(DiaryMain.this,DiaryPlus.class);
                                        intent2.putExtra("no", no);
                                        intent2.putExtra("id", id);
                                        intent2.putExtra("title", arr[finalJ*6]);
                                        intent2.putExtra("selectDate", arr[finalJ*6+5]);
                                        intent2.putExtra("weather", arr[finalJ*6+2]);
                                        intent2.putExtra("emotion", arr[finalJ*6+3]);
                                        intent2.putExtra("content", arr[finalJ*6+1]);
                                        intent2.putExtra("flag", "true");
                                        startActivity(intent2);
                                        finish();
                                    }
                                });
                                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();     //닫기
                                    }
                                });
                                alert.setMessage("다이어리를 수정/삭제하시겠습니까?");
                                alert.show();
                                return true;
                            }
                        });
                    }

                }

                //다이어리 클릭했을 때
                /*for(int j=0; j<tr.length; j++)
                {
                    final int finalJ = j;
                    tr[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent3 = new Intent(DiaryMain.this,TravelDetail.class);
                            //intent3.putExtra("id", id);
                            intent3.putExtra("no", no[finalJ]);
                            startActivity(intent3);
                            finish();
                        }
                    });
                }*/
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String date = (String) params[1];

                String link = "http://cs2020tv.dongyangmirae.kr/diary_main.php";
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
