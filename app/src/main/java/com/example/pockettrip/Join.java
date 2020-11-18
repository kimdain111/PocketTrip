package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class Join extends Activity {

    private EditText idText;
    private EditText pwText;
    private EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        idText = (EditText) findViewById((R.id.idText));
        pwText = (EditText)findViewById((R.id.pwText));
        nameText = (EditText)findViewById((R.id.nameText));
    }

    @Override
    public void onBackPressed() {
        Intent myintent = new Intent(Join.this,MainActivity.class);
        startActivity(myintent);
        finish();
    }

    //취소버튼 눌렀을 때
    public void cancel(View view){
        Intent myintent = new Intent(Join.this,MainActivity.class);
        startActivity(myintent);
        finish();
    }
    //회원가입페이지에서 join버튼 눌렀을 때 onclick
    public void insert(View view){
        String id = idText.getText().toString();
        String pw = pwText.getText().toString();
        String name = nameText.getText().toString();
        if(id.equals(""))
            Toast.makeText(Join.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(pw.equals(""))
            Toast.makeText(Join.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(name.equals(""))
            Toast.makeText(Join.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            //1.execute메소드를 통해 AsyncTask실행
            InsertData task = new InsertData();
            task.execute(id, pw, name);
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Join.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("join fail"))
                Toast.makeText(getApplicationContext(),"이미 있는 아이디입니다.", Toast.LENGTH_SHORT).show();
            else if(s.equals("join success")){
                Toast.makeText(getApplicationContext(),"회원가입 성공", Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(Join.this,MainActivity.class);
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
                String id = (String) params[0];
                String pw = (String) params[1];
                String name = (String) params[2];

                String link = "http://cs2020tv.dongyangmirae.kr/join.php"; //=(String)params[0];
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼떄는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "id=" + id + "&pw=" + pw + "&name=" + name;

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
