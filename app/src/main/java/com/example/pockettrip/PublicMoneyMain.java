package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class PublicMoneyMain extends Activity {
    private RecyclerView listview;
    private Diary_Adapter adapter;
    private String no, id, selectDate = "A", sort, country, rate;
    private float rateValue;
    ImageButton addCashBtn;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_publicmoney);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");
        id= intent.getExtras().getString("id");
        rate = intent.getExtras().getString("rate");
        country = intent.getExtras().getString("country");

        rateValue = Float.parseFloat(rate.replaceAll(",",""));

        //1.execute메소드를 통해 AsyncTask실행
        //상단 날짜 리스트뷰 띄우기
        selectDate task = new selectDate();
        task.execute(no);

        //가계부 조회
        PublicMoneyData task2 = new PublicMoneyData();
        task2.execute(no, selectDate);

        BalanceData task3 = new BalanceData();
        task3.execute(no);

        addCashBtn = (ImageButton)findViewById(R.id.addCash);
        addCashBtn.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(PublicMoneyMain.this,TravelDetail.class);
        intent2.putExtra("no", no);
        intent2.putExtra("id", id);
        startActivity(intent2);
        finish();
    }

    //마이페이지
    public void goMypage(View view)
    {
        Intent myIntent = new Intent(PublicMoneyMain.this, MyPage.class);
        myIntent.putExtra("id", id);
        myIntent.putExtra("no", no);
        myIntent.putExtra("rate", rate);
        myIntent.putExtra("mypageFlag", "5");
        startActivity(myIntent);
        finish();
    }

    //가계부 입력
    public void cashPlus(View view){
        Intent myintent = new Intent(PublicMoneyMain.this, PublicMoneyPlus.class);
        myintent.putExtra("no", no);
        myintent.putExtra("id", id);
        myintent.putExtra("selectDate", selectDate);
        myintent.putExtra("sort", sort);
        myintent.putExtra("rate", rate);
        myintent.putExtra("country", country);
        myintent.putExtra("flag", "false");
        startActivity(myintent);
        finish();
    }

    //날짜 버튼 클릭
    private View.OnClickListener onClickItem = new View.OnClickListener() { //날짜 선택
        @Override
        public void onClick(View v) {
            selectDate = (String) v.getTag();
           if(selectDate.equals("A")) {
               Toast.makeText(getApplicationContext(),selectDate,Toast.LENGTH_SHORT).show();
               addCashBtn.setVisibility(View.GONE);
               sort = "all";
           }
           else if(selectDate.equals("P")) {
               Toast.makeText(getApplicationContext(),selectDate,Toast.LENGTH_SHORT).show();
               addCashBtn.setVisibility(View.VISIBLE);
               sort = "plan";
           }
           else {
               Toast.makeText(getApplicationContext(),selectDate,Toast.LENGTH_SHORT).show();
               addCashBtn.setVisibility(View.VISIBLE);
               sort = "all";
           }

           PublicMoneyData task4 = new PublicMoneyData();
           task4.execute(no, selectDate);

           BalanceData task5 = new BalanceData();
           task5.execute(no);

        }
    };

    //여행기간 날짜 조회
    class selectDate extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PublicMoneyMain.this, "Please Wait", null, true, true);
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
        listview = findViewById(R.id.date_listview2);
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
        itemList.add("P");
        itemPrintList.add("P");
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

    //해당 날짜의 가계부 조회
    class PublicMoneyData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PublicMoneyMain.this, "Please Wait", null, true, true);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");

            TableLayout table = findViewById(R.id.cashTable); //가계부 테이블
            table.removeAllViews();
            TextView text = findViewById(R.id.noCash); //가계부 없음 텍스트
            final TableRow tr[] = new TableRow[(arr.length/7)*2];

            if(s.equals("no data")){
                text.setVisibility(View.VISIBLE);
                table.setVisibility(View.GONE);
            }
            else{
                text.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);

                int cnt = 0;
                for(int i=0; i<arr.length; i+=7)
                {
                    tr[cnt] = new TableRow(PublicMoneyMain.this);
                    tr[cnt+1] = new TableRow(PublicMoneyMain.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    tr[cnt].setLayoutParams(lp);
                    tr[cnt+1].setLayoutParams(lp);

                    ImageView tImg = new ImageView(PublicMoneyMain.this);
                    if(arr[i].equals("식비")){
                        tImg.setImageResource(R.drawable.eat);
                    } else if(arr[i].equals("교통")){
                        tImg.setImageResource(R.drawable.bus);
                    } else if(arr[i].equals("생활")){
                        tImg.setImageResource(R.drawable.sofa);
                    } else if(arr[i].equals("문화")){
                        tImg.setImageResource(R.drawable.culture);
                    } else if(arr[i].equals("기념품")){
                        tImg.setImageResource(R.drawable.souvenir);
                    } else if(arr[i].equals("패션")){
                        tImg.setImageResource(R.drawable.clothes);
                    } else if(arr[i].equals("지출기타")){
                        tImg.setImageResource(R.drawable.coin);
                    } else if(arr[i].equals("교통계획")){
                        tImg.setImageResource(R.drawable.airplane);
                    } else if(arr[i].equals("숙소")){
                        tImg.setImageResource(R.drawable.hotel);
                    } else if(arr[i].equals("투어")){
                        tImg.setImageResource(R.drawable.tour);
                    } else if(arr[i].equals("식사")){
                        tImg.setImageResource(R.drawable.eat);
                    } else if(arr[i].equals("오락")){
                        tImg.setImageResource(R.drawable.play);
                    } else if(arr[i].equals("계획소비기타")){
                        tImg.setImageResource(R.drawable.coin);
                    } else if(arr[i].equals("환전")){
                        tImg.setImageResource(R.drawable.money);
                    } else if(arr[i].equals("기타")){
                        tImg.setImageResource(R.drawable.coin);
                    } else {
                        tImg.setImageResource(R.drawable.coin);
                    }

                    tImg.setLayoutParams(new TableRow.LayoutParams(200,200));
                    tImg.setPadding(0,0,50,0);

                    TextView tText = new TextView(PublicMoneyMain.this);

                    if(arr[i+4].equals("0000-00-00")){
                        tText.setText((arr[i+1]+"원\n"+arr[i+2]));
                        tText.setTextSize(20);
                    } else{
                        tText.setText(arr[i+1]+"\n(" + String.format("%.1f",Integer.parseInt(arr[i+1])*rateValue) +"원)\n"+arr[i+2]);
                        tText.setTextSize(20);
                    }

                    if(arr[i+3].equals("spend")){
                        tText.setTextColor(Color.parseColor("#ff0000"));

                    } else {
                        tText.setTextColor(Color.parseColor("#0000ff"));
                    }

                    Typeface face = getResources().getFont(R.font.like);
                    tText.setTypeface(face);

                    TextView dateText = new TextView(PublicMoneyMain.this);

                    dateText.setText(arr[i+4]);
                    dateText.setTextSize(15);
                    tr[cnt].addView(dateText);
                    tr[cnt].setPadding(0,5,0,20);
                    table.addView(tr[cnt],lp);

                    tr[cnt+1].addView(tImg);
                    tr[cnt+1].addView(tText);
                    tr[cnt+1].setPadding(0,15,0,0);
                    tr[cnt+1].setClickable(true);
                    table.addView(tr[cnt+1],lp);
                    cnt+=2;

                }

                //롱클릭 - 수정/삭제
                for(int j=0; j<tr.length; j+=2)
                {
                    final int finalJ = j/2;
                    for(int k=0;k<2;k++) {
                        tr[j+k].setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(PublicMoneyMain.this);
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent2 = new Intent(PublicMoneyMain.this, PublicMoneyPlus.class);
                                        intent2.putExtra("no", no);
                                        intent2.putExtra("id", id);
                                        intent2.putExtra("rate", rate);
                                        intent2.putExtra("sort", arr[finalJ * 7 + 5]);
                                        intent2.putExtra("cash", arr[finalJ * 7 + 1]);
                                        intent2.putExtra("category", arr[finalJ * 7]);
                                        intent2.putExtra("payment", arr[finalJ * 7 + 6]);
                                        intent2.putExtra("memo", arr[finalJ * 7 + 2]);
                                        intent2.putExtra("date", arr[finalJ * 7 + 4]);
                                        intent2.putExtra("type", arr[finalJ * 7 + 3]);
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
                                alert.setMessage("가계부를 수정/삭제하시겠습니까?");
                                alert.show();
                                return true;
                            }
                        });
                    }

                }

            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String date = (String) params[1];

                String link = "http://cs2020tv.dongyangmirae.kr/pu_main.php";
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

    class BalanceData extends AsyncTask<String, Void, String>{
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PublicMoneyMain.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");

            TextView importText = findViewById(R.id.importText);
            TextView spendText = findViewById(R.id.spendText);
            TextView subText = findViewById(R.id.subText);

            float importTv = 0, spendTv = 0;

            if(s.equals("no data")){
                importText.setText("0");
                spendText.setText("0");
                subText.setText("0");
            } else{
                for(int i=0; i<arr.length; i+=4){
                    if(arr[i+1].equals("import")){
                        if(arr[i+2].equals("환전")){
                            importTv = importTv + Integer.parseInt(arr[i])/rateValue;
                        }else {
                            importTv = importTv + Integer.parseInt(arr[i]);
                        }
                    }
                    else if(arr[i+3].equals("all")) spendTv =spendTv + Integer.parseInt(arr[i]);
                }
                importText.setText(String.format("%.1f",importTv));
                spendText.setText(String.format("%.1f",spendTv));
                subText.setText(String.format("%.1f", importTv-spendTv));
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/pu_balance.php";
                String data = "no=" + no;

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
