package com.example.pockettrip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import androidx.annotation.Nullable;

public class PublicMoneyPlus extends Activity {

    EditText etcash, etcategory, etmemo;
    private RadioGroup typeGroup;
    private RadioGroup payGroup;
    private TextView dateText, sortText;
    private String payment="cash", no, id, category="식비", type="import", chDate, sort, rate, country;
    private Button cateBtn, exchangeBtn;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_publicplus);

        Intent intent = getIntent();
        no = intent.getExtras().getString("no");
        id = intent.getExtras().getString("id");
        chDate = intent.getExtras().getString("selectDate");
        sort = intent.getExtras().getString("sort");
        rate = intent.getExtras().getString("rate");
        country = intent.getExtras().getString("country");

        etcash = (EditText)findViewById(R.id.numInput);
        etmemo = (EditText)findViewById(R.id.memoInput);

        typeGroup = (RadioGroup)findViewById(R.id.typeGroup);
        payGroup = (RadioGroup)findViewById(R.id.payGroup);

        dateText = (TextView)findViewById(R.id.chDate2);
        sortText = (TextView)findViewById(R.id.sortText);

        if(sort.equals("plan")){
            sortText.setVisibility(View.GONE);
            dateText.setVisibility(View.GONE);
            chDate="0000-00-00";
        } else {
            sortText.setVisibility(View.VISIBLE);
            dateText.setVisibility(View.VISIBLE);
            dateText.setText(chDate);
        }

        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.importBtn:
                        type = "import";
                        break;
                    case R.id.spendBtn:
                        type = "spend";
                        break;
                }
            }
        });
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

        /*exchangeBtn = (Button) findViewById(R.id.exchangeBtn);
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicMoneyPlus.this, ExchangeMoney.class);
                intent.putExtra("data", "Exchange Money");
                intent.putExtra("rate", rate);
                intent.putExtra("country", country);
                Toast.makeText(getApplicationContext(), rate, Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, 2);
            }
        });*/

        cateBtn = (Button) findViewById(R.id.categoryBtn);
        if(sort.equals("plan")){
                cateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(type=="import") {
                            Intent intent = new Intent(PublicMoneyPlus.this, PlanImportCategory.class);
                            intent.putExtra("data", "Plan Import Category");
                            startActivityForResult(intent, 1);
                        } else {
                            Intent intent = new Intent(PublicMoneyPlus.this, PlanSpendCategory.class);
                            intent.putExtra("data", "Plan Spend Category");
                            startActivityForResult(intent, 1);
                        }
                    }
                });
        } else {
            cateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PublicMoneyPlus.this, SpendCategory.class);
                    intent.putExtra("data", "Spend Category");
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                String result = data.getStringExtra("result");
                cateBtn.setText(result);
                category = result;
            }
        }
        else if (requestCode==2){
            if(resultCode==RESULT_OK){
                String result = data.getStringExtra("result");
                etcash.setText(result);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.mymenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
            case R.id.traffic:
                cateBtn.setText(item.getTitle());
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent myintent = new Intent(PublicMoneyPlus.this,PublicMoneyMain.class);
        myintent.putExtra("no",no);
        myintent.putExtra("id",id);
        startActivity(myintent);
        finish();
    }

    public void cancel(View view){
        Intent myintent = new Intent(PublicMoneyPlus.this,PublicMoneyMain.class);
        myintent.putExtra("no",no);
        myintent.putExtra("id",id);
        startActivity(myintent);
        finish();
    }

    public void insert(View view){
        String cash = etcash.getText().toString();
        String memo = etmemo.getText().toString();

        if(cash.equals(""))
            Toast.makeText(PublicMoneyPlus.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(cateBtn.getText().toString().equals(""))
            Toast.makeText(this, "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
        else{
            //1.execute메소드를 통해 AsyncTask실행
            PublicMoneyPlus.InsertData task = new PublicMoneyPlus.InsertData();
            task.execute(no, sort, cash, category, payment, memo, chDate, type);
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PublicMoneyPlus.this, "Please Wait",null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("cash plus success")){
                Toast.makeText(getApplicationContext(),"금액이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(PublicMoneyPlus.this,PublicMoneyMain.class);
                myintent.putExtra("no",no);
                myintent.putExtra("id",id);
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
                String no = (String) params[0];
                String sort = (String) params[1];
                String cash = (String) params[2];
                String category = (String) params[3];
                String payment = (String) params[4];
                String memo = (String) params[5];
                String date = (String) params[6];
                String type = (String) params[7];

                String link = "http://cs2020tv.dongyangmirae.kr/pu_spend.php"; //=(String)params[0];
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "no=" + no + "&sort=" + sort + "&cash=" + cash + "&category=" + category + "&payment=" + payment + "&memo=" + memo + "&date=" +date+ "&type=" + type;

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
}
