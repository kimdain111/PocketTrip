package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class SchedulePlus extends Activity {

    private String no, id, chDate, chTime, flag, originalContent, originalTime;
    private EditText contentText;
    private TextView dateText;
    Button timeBtn;
    int h=0, m=0;
    int ch=0, cm=0; //수정/삭제로 들어온 경우 기존 선택했던 시,분

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_plus);

        Intent intent = getIntent();
        flag = intent.getExtras().getString("flag");
        no = intent.getExtras().getString("no");
        id= intent.getExtras().getString("id");
        chDate = intent.getExtras().getString("selectDate");

        if(flag.equals("true")){ //수정화면으로 들어온 경우
            originalTime = intent.getExtras().getString("time");
            originalContent = intent.getExtras().getString("content");
        }

        contentText = (EditText)findViewById(R.id.contentText);
        dateText = (TextView)findViewById(R.id.chDate);
        dateText.setText(chDate);

        timeBtn = (Button)findViewById(R.id.timeBtn);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });

        //수정/삭제로 들어온 경우 기본값 설정
        if(flag.equals("true")){
            Button okbtn = findViewById(R.id.okBtn);
            Button deleteBtn = findViewById(R.id.delete);
            okbtn.setText("수정");                //수정버튼으로 바꿔주기
            deleteBtn.setVisibility(View.VISIBLE);

            //시간, 내용 setText
            ch = Integer.parseInt(originalTime.substring(0,2));
            cm = Integer.parseInt(originalTime.substring(3,5));
            originalTime = ch+":"+cm;
            chTime = ch+":"+cm;

            timeBtn.setText(ch+"시"+cm+"분");
            contentText.setText(originalContent);
        }
    }

    //시간버튼 눌렀을 때
    void showTime(){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        if(flag.equals("false")){ //추가로 들어온 경우
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    h = hourOfDay;
                    m = minute;
                    updateDisplay();
                }
            },mHour+9,mMinute,true);

            timePickerDialog.setMessage("예정시간을 선택하세요.");
            timePickerDialog.show();
        }else{ //수정/삭제로 들어온 경우
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    h = hourOfDay;
                    m = minute;
                    updateDisplay();
                }
            },ch,cm,true);

            timePickerDialog.setMessage("예정시간을 선택하세요.");
            timePickerDialog.show();
        }
    }

    //버튼에 시간 표시
    private void updateDisplay(){
        chTime = h+":"+m;
        timeBtn.setText(h+"시"+m+"분");
    }

    //뒤로가기
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SchedulePlus.this,ScheduleMain.class);
        intent.putExtra("id", id);
        intent.putExtra("no", no);
        startActivity(intent);
        finish();
    }

    //확인버튼 눌렀을 때
    public void insert(View view){
        String content = contentText.getText().toString();

        if(timeBtn.getText().toString().equals(""))
            Toast.makeText(this, "예정시간을 선택해주세요", Toast.LENGTH_SHORT).show();
        else if(content.equals(""))
            Toast.makeText(SchedulePlus.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else{
            if(flag.equals("true")){ //수정화면일 때
                UpdateData task = new UpdateData();
                task.execute(no, chDate, chTime, content, originalTime, originalContent);
            }else{
                //1.execute메소드를 통해 AsyncTask실행
                SchedulePlus.InsertData task = new SchedulePlus.InsertData();
                task.execute(no, chDate, chTime, content);
            }
        }
    }

    //삭제버튼 눌렀을 때
    public void scheduleDelete(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(SchedulePlus.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteData task = new DeleteData();
                task.execute(no, chDate, originalTime, originalContent);

            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("일정을 삭제하시겠습니까?");
        alert.show();
    }

    //DB저장하는 클래스
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(SchedulePlus.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("schedule plus success")){
                Toast.makeText(getApplicationContext(),"일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(SchedulePlus.this,ScheduleMain.class);
                myintent.putExtra("no", no);
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
                String chDate = (String) params[1];
                String time = (String) params[2];
                String content = (String) params[3];

                String link = "http://cs2020tv.dongyangmirae.kr/schedule_plus.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "no=" + no + "&date=" + chDate + "&time=" + time + "&content=" + content;
                System.out.println("post 전송");
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

    //취소버튼 눌렀을 때
    public void cancel(View view){
        Intent myintent = new Intent(SchedulePlus.this,ScheduleMain.class);
        myintent.putExtra("id", id);
        myintent.putExtra("no", no);
        startActivity(myintent);
        finish();
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
                Toast.makeText(getApplicationContext(),"일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(SchedulePlus.this,ScheduleMain.class);
                intent2.putExtra("id", id);
                intent2.putExtra("no", no);
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
                String chDate = (String) params[1];
                String chtime = (String) params[2];
                String content = (String) params[3];
                String originalTime = (String) params[4];
                String originalContent = (String) params[5];

                String link = "http://cs2020tv.dongyangmirae.kr/scheduleUpdate.php";
                String data = "no=" + no + "&date=" + chDate + "&time=" + chtime + "&content=" + content + "&originalTime=" + originalTime + "&originalContent=" + originalContent;

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

    //일정 삭제 DB 반영
    class DeleteData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("delete")){
                Toast.makeText(getApplicationContext(),"일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(SchedulePlus.this, ScheduleMain.class);
                intent2.putExtra("id", id);
                intent2.putExtra("no", no);
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
                String chDate = (String) params[1];
                String originalTime = (String) params[2];
                String originalContent = (String) params[3];

                String link = "http://cs2020tv.dongyangmirae.kr/scheduleDelete.php";
                String data = "no="+no+"&date=" + chDate + "&originalTime=" + originalTime + "&originalContent=" + originalContent;

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
