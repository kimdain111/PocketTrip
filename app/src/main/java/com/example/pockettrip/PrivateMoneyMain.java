package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PrivateMoneyMain extends Activity {
    private RecyclerView listview;
    private Diary_Adapter adapter;
    private String no, id;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_privatemoney);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");
        id= intent.getExtras().getString("id");
        PrivateMoneyMain.selectDate task = new PrivateMoneyMain.selectDate();
        task.execute(no);

        ImageButton addCashBtn = (ImageButton)findViewById(R.id.addCash);
        addCashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(PrivateMoneyMain.this, PrivateMoneyPlus.class);
                myintent.putExtra("no", no);
                myintent.putExtra("id", id);
                startActivity(myintent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(PrivateMoneyMain.this,TravelDetail.class);
        intent2.putExtra("no", no);
        intent2.putExtra("id", id);
        startActivity(intent2);
        finish();
    }

    //마이페이지
    public void goMypage(View view)
    {
        Intent myIntent = new Intent(PrivateMoneyMain.this, MyPage.class);
        myIntent.putExtra("id", id);
        startActivity(myIntent);
        finish();
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() { //날짜 선택
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            //Toast.makeText(PrivateMoneyMain.this, str, Toast.LENGTH_SHORT).show();

        }
    };
    class selectDate extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PrivateMoneyMain.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");
            TextView dateText = findViewById(R.id.date);
            dateText.setText(arr[0]);
            init(arr[0], arr[1]);
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/dateList.php";
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

        adapter = new Diary_Adapter(this, itemList,itemList, onClickItem);
        listview.setAdapter(adapter);

        Diary_Date_Decoration decoration = new Diary_Date_Decoration();
        listview.addItemDecoration(decoration);
        System.out.println("###init끝남###");
    }

    class BalanceData extends AsyncTask<String, Void, String>{
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PrivateMoneyMain.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");

            TextView importText = findViewById(R.id.importText);
            TextView spendText = findViewById(R.id.spendText);
            TextView subText = findViewById(R.id.subText);

            int importTv = 0, spendTv = 0;

            if(s.equals("no data")){
                importText.setText("0");
                spendText.setText("0");
                subText.setText("0");
            } else{
                for(int i=0; i<arr.length; i+=2){
                    if(arr[i+1].equals("import")) importTv = importTv + Integer.parseInt(arr[i]);
                    else spendTv =spendTv + Integer.parseInt(arr[i]);
                }
                importText.setText(Integer.toString(importTv)+"원");
                spendText.setText(Integer.toString(spendTv)+"원");
                subText.setText(Integer.toString(importTv-spendTv)+"원");
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/pr_balance.php";
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
