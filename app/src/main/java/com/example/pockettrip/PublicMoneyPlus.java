package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
    private String payment="cash", no, id, category, type="import", chDate, sort, rate, country, flag, memo, cash;
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
        rate = intent.getExtras().getString("rate");
        country = intent.getExtras().getString("country");
        flag = intent.getExtras().getString("flag");

        //Toast.makeText(getApplicationContext(),"rate: "+ rate+" id: "+id,Toast.LENGTH_SHORT).show();

        if(flag.equals("true")){ //수정화면으로 들어왔을 때 intent 추가로 받기
            memo = intent.getExtras().getString("memo");
            chDate = intent.getExtras().getString("date");
            cash = intent.getExtras().getString("cash");
            payment = intent.getExtras().getString("payment");
            category = intent.getExtras().getString("category");
            type = intent.getExtras().getString("type");
            sort = intent.getExtras().getString("sort");
        } else {
            chDate = intent.getExtras().getString("selectDate");
            sort = intent.getExtras().getString("sort");
        }

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
                    if(type=="import") {
                        Intent intent = new Intent(PublicMoneyPlus.this, PlanImportCategory.class);
                        intent.putExtra("data", "Plan Import Category");
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(PublicMoneyPlus.this, SpendCategory.class);
                        intent.putExtra("data", "Spend Category");
                        startActivityForResult(intent, 1);
                    }
                }
            });
        }

        if(flag.equals("true")){
            Button okBtn = findViewById(R.id.registerBtn);
            Button deleteBtn = findViewById(R.id.cancelBtn);
            okBtn.setText("수정");
            deleteBtn.setVisibility(View.VISIBLE);

            etcash.setText(cash);
            //etcategory.setText(category);
            etmemo.setText(memo);
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
        String recash = etcash.getText().toString();
        String rememo = etmemo.getText().toString();

        if(recash.equals(""))
            Toast.makeText(PublicMoneyPlus.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(cateBtn.getText().toString().equals(""))
            Toast.makeText(this, "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
        else{
            if(flag.equals("true")){
                //Toast.makeText(this, cash + memo, Toast.LENGTH_SHORT).show();
                PublicMoneyPlus.UpdateData task = new PublicMoneyPlus.UpdateData();
                task.execute(no, sort, recash, category, payment, rememo, type, chDate, cash, memo);
            }
            else{
                //1.execute메소드를 통해 AsyncTask실행
                PublicMoneyPlus.InsertData task = new PublicMoneyPlus.InsertData();
                task.execute(no, sort, recash, category, payment, rememo, chDate, type);
            }

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
                myintent.putExtra("rate", rate);
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

    //일정 수정 DB 반영
    class UpdateData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("update")){
                Toast.makeText(getApplicationContext(),"가계부가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(PublicMoneyPlus.this,PublicMoneyMain.class);
                intent2.putExtra("id", id);
                intent2.putExtra("no", no);
                intent2.putExtra("rate",rate);
                startActivity(intent2);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String sort = (String) params[1];
                String cash = (String) params[2];
                String category = (String) params[3];
                String payment = (String) params[4];
                String memo = (String) params[5];
                String type = (String) params[6];
                String date = (String) params[7];
                String originalCash = (String) params[8];
                String originalMemo = (String) params[9];


                String link = "http://cs2020tv.dongyangmirae.kr/pu_update.php";
                String data = "no=" + no + "&sort=" + sort + "&cash=" + cash + "&category=" + category + "&payment=" + payment + "&memo=" + memo + "&type=" + type + "&date=" + date + "&originalCash=" + originalCash + "&originalMemo=" + originalMemo;

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
            }catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }
        }
    }

    public void moneyDelete(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(PublicMoneyPlus.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteData task = new DeleteData();
                //Toast.makeText(getApplicationContext(), no+chDate+cash+memo,Toast.LENGTH_SHORT).show();
                task.execute(no, chDate, cash, memo);

            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("가계부를 삭제하시겠습니까?");
        alert.show();
    }

    //여행지 정보 삭제
    class DeleteData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("delete")){
                Toast.makeText(getApplicationContext(),"가계부가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(PublicMoneyPlus.this, PublicMoneyMain.class);
                intent2.putExtra("id", id);
                intent2.putExtra("no", no);
                intent2.putExtra("rate", rate);
                startActivity(intent2);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String date = (String) params[1];
                String cash = (String) params[2];
                String memo = (String) params[3];

                String link = "http://cs2020tv.dongyangmirae.kr/pu_delete.php";
                String data = "no="+no+"&date="+date+"&cash=" + cash + "&memo=" + memo;

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
            }catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }
        }
    }
}
