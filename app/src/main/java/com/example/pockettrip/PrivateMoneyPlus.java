package com.example.pockettrip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class PrivateMoneyPlus extends Activity {
    EditText etcash, etcategory, etmemo;
    private int mYear, mMonth, mDay;
    private RadioGroup payGroup;
    private String payment="cash", no, category="eat";
    private Button chDate;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_privateplus);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");

        etcash = (EditText)findViewById(R.id.numInput);
        etmemo = (EditText)findViewById(R.id.memoInput);

        payGroup = (RadioGroup)findViewById(R.id.payGroup);

        this.InitializeView();
        this.InitializeListener();

        payGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.cashBtn:
                        payment = "cash";
                        break;
                    case R.id.cardBtn:
                        payment = "card";
                        break;
                }
            }
        });
    }
    public void InitializeView(){
        chDate = (Button)findViewById(R.id.cashDateBtn);
    }

    public void InitializeListener(){
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int datOfMonth) {
                chDate.setText(year + "-"+(monthOfYear+1)+"-"+datOfMonth);
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent myintent = new Intent(PrivateMoneyPlus.this,PrivateMoneyMain.class);
        myintent.putExtra("no",no);
        startActivity(myintent);
        finish();
    }

    public void cancel(View view){
        Intent myintent = new Intent(PrivateMoneyPlus.this,PrivateMoneyMain.class);
        startActivity(myintent);
        finish();
    }

    public void OnClickHandler(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, mYear, mMonth , mDay);
        dialog.show();
    }

    public void insert(View view){
        String cash = etcash.getText().toString();
        String memo = etmemo.getText().toString();

        if(cash.equals(""))
            Toast.makeText(PrivateMoneyPlus.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            PrivateMoneyPlus.InsertData task = new PrivateMoneyPlus.InsertData();
            task.execute(no, cash, category, payment, memo, chDate.getText().toString());
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PrivateMoneyPlus.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("cash plus success")){
                Toast.makeText(getApplicationContext(),"금액이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(PrivateMoneyPlus.this,PublicMoneyMain.class);
                myintent.putExtra("no",no);
                startActivity(myintent);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String cash = (String) params[1];
                String category = (String) params[2];
                String payment = (String) params[3];
                String memo = (String) params[4];
                String date = (String) params[5];

                String link = "http://cs2020tv.dongyangmirae.kr/pr_Spend.php";
                String data = "no=" + no + "&cash=" + cash + "&category=" + category + "&payment=" + payment + "&memo=" + memo + "&date=" +date;

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
