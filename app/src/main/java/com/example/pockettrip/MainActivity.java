package com.example.pockettrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    private EditText idText;
    private EditText pwText;
    CheckBox idCheckbox;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Boolean loginCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idText = (EditText)findViewById(R.id.idText);
        pwText = (EditText)findViewById(R.id.pwText);
        idCheckbox = (CheckBox)findViewById(R.id.idCheckbox);

        pref = getSharedPreferences("autologin", MODE_PRIVATE);
        editor = pref.edit();

        if(pref.getBoolean("autoLoginOn", false)) //껐다켰도 자동로그인 유지
        {
            Intent intent = new Intent(MainActivity.this,TravelMain.class);
            intent.putExtra("id", pref.getString("id",null));
            startActivity(intent);
            finish();
        }
        idCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    loginCheck = true;
                }
                else{
                    loginCheck = false;
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void join(View view){
        Intent myintent = new Intent(MainActivity.this,Join.class);
        startActivity(myintent);
        finish();
    }
    public void login(View view){
        String id = idText.getText().toString();
        String pw = pwText.getText().toString();
        if(id.equals(""))
            Toast.makeText(MainActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(pw.equals(""))
            Toast.makeText(MainActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            LoginData task = new LoginData();
            task.execute(id, pw);
        }
    }

    class LoginData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("ID does not exist"))
                Toast.makeText(getApplicationContext(),"존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
            else if(s.equals("PW is wrong"))
                Toast.makeText(getApplicationContext(),"비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
            else if(s.equals("login success")){
                if(loginCheck){//자동로그인
                    editor.putString("id", idText.getText().toString());
                    editor.putString("pw", pwText.getText().toString());
                    editor.putBoolean("autoLoginOn", true);
                    editor.commit();
                }
                Toast.makeText(getApplicationContext(),"로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,TravelMain.class);
                intent.putExtra("id", idText.getText().toString()); //id값 넘겨주기
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                String id = (String) params[0];
                String pw = (String) params[1];

                String link = "http://cs2020tv.dongyangmirae.kr/login.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼떄는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "id=" + id + "&pw=" + pw;

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
