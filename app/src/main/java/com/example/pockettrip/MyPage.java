package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class MyPage extends Activity {
    private String id, no, rate, mypageFlag;
    private EditText idText, nameText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        no = intent.getExtras().getString("no");
        rate = intent.getExtras().getString("rate");
        mypageFlag = intent.getExtras().getString("mypageFlag");

        idText = (EditText)findViewById(R.id.idText);
        idText.setText(id);
        nameText = (EditText)findViewById(R.id.nameText);

        //회원정보(이름, 비밀번호) 조회
        selectInfo task = new selectInfo();
        task.execute(id);

    }

    //취소버튼 눌렀을 때
    public void cancel(View view){
        Intent myintent;
        switch(Integer.parseInt(mypageFlag)){
            case 1:
                myintent = new Intent(MyPage.this,TravelMain.class);
                break;
            case 2:
                myintent = new Intent(MyPage.this,TravelDetail.class);
                break;
            case 3:
                myintent = new Intent(MyPage.this,ScheduleMain.class);
                break;
            case 4:
                myintent = new Intent(MyPage.this,DiaryMain.class);
                break;
            case 5:
                myintent = new Intent(MyPage.this,PublicMoneyMain.class);
                myintent.putExtra("rate", rate);
                break;
            case 6:
                myintent = new Intent(MyPage.this,PrivateMoneyMain.class);
                break;
            case 7:
                myintent = new Intent(MyPage.this,CheckList.class);
                break;
            default:
                myintent = new Intent(MyPage.this,TravelMain.class);
                break;
        }

        myintent.putExtra("id", id);

        if(Integer.parseInt(mypageFlag) > 1){ //TravelMain페이지 빼고 no값 받기
            myintent.putExtra("no", no);
        }
        startActivity(myintent);
        finish();
    }

    //뒤로가기
    @Override
    public void onBackPressed() {
        Intent myintent;
        switch(Integer.parseInt(mypageFlag)){
            case 1:
                myintent = new Intent(MyPage.this,TravelMain.class);
                break;
            case 2:
                myintent = new Intent(MyPage.this,TravelDetail.class);
                break;
            case 3:
                myintent = new Intent(MyPage.this,ScheduleMain.class);
                break;
            case 4:
                myintent = new Intent(MyPage.this,DiaryMain.class);
                break;
            case 5:
                myintent = new Intent(MyPage.this,PublicMoneyMain.class);
                myintent.putExtra("rate", rate);
                break;
            case 6:
                myintent = new Intent(MyPage.this,PrivateMoneyMain.class);
                break;
            case 7:
                myintent = new Intent(MyPage.this,CheckList.class);
                break;
            default:
                myintent = new Intent(MyPage.this,TravelMain.class);
                break;
        }

        myintent.putExtra("id", id);
        if(Integer.parseInt(mypageFlag) > 1){ //TravelMain 페이지 빼고 no값 받기
            myintent.putExtra("no", no);
        }
        startActivity(myintent);
        finish();
    }


    //회원정보(이름,비밀번호 조회)
    class selectInfo extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MyPage.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            nameText.setText(s); //이름값 가져오기
        }
        //3. 백그라운드작업 수행(execute메소드 호출할 때 사용된 파라미터 전달받음)
        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/mypage_info.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "id=" + id;

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

    //로그아웃
    public void logout(View view)
    {
        Intent myIntent = new Intent(MyPage.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }

    //탈퇴
    public void leaveGroup(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MyPage.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DB에서 회원 삭제
                deleteUser task = new deleteUser();
                task.execute(id);

                Intent myIntent = new Intent(MyPage.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("탈퇴하시겠습니까?");
        alert.show();

    }

    //회원탈퇴 DB반영
    class deleteUser extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MyPage.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            Toast.makeText(getApplicationContext(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
        }
        //3. 백그라운드작업 수행(execute메소드 호출할 때 사용된 파라미터 전달받음)
        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/delete_user.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "id=" + id;

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
