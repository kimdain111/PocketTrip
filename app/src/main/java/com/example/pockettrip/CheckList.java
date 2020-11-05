package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CheckList extends Activity {

    String no, id, nation, first, last;
    EditText checkEdit;
    TableLayout table;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        Intent myIntent = getIntent();
        no = myIntent.getExtras().getString("no");
        id= myIntent.getExtras().getString("id");
        nation = myIntent.getExtras().getString("nation");
        first = myIntent.getExtras().getString("first");
        last= myIntent.getExtras().getString("last");

        TextView nationTv = (TextView) findViewById(R.id.travel);
        TextView firstTv = (TextView) findViewById(R.id.travelFirst);
        TextView lastTv = (TextView) findViewById(R.id.travelLast);

        nationTv.setText(nation);
        firstTv.setText(first);
        lastTv.setText(last);

        checkEdit = (EditText) findViewById(R.id.editcheck);
        table = (TableLayout) findViewById(R.id.table);

        SelectList task = new SelectList();
        task.execute(no);
    }

    @Override
    public void onBackPressed() {
        final ArrayList<String> checkArr = new ArrayList<String>();
        checkArr.add(no);
        for(int i=0; i<table.getChildCount(); i++){
            TableRow row = (TableRow)table.getChildAt(i);
            final CheckBox box = (CheckBox)row.getChildAt(0);
            if(box.isChecked()){
                checkArr.add(box.getText().toString());
            }
        }
        checkArr.add(Integer.toString(checkArr.size()-1));
        CheckUpdate task2 = new CheckUpdate();
        task2.execute(checkArr);

        Intent intent2 = new Intent(CheckList.this,TravelDetail.class);
        intent2.putExtra("id", id);
        intent2.putExtra("no", no);
        startActivity(intent2);
        finish();
    }

    //마이페이지
    public void goMypage(View view)
    {
        final ArrayList<String> checkArr = new ArrayList<String>();
        checkArr.add(no);
        for(int i=0; i<table.getChildCount(); i++){
            TableRow row = (TableRow)table.getChildAt(i);
            final CheckBox box = (CheckBox)row.getChildAt(0);
            if(box.isChecked()){
                checkArr.add(box.getText().toString());
            }
        }
        checkArr.add(Integer.toString(checkArr.size()-1));
        CheckUpdate task2 = new CheckUpdate();
        task2.execute(checkArr);

        Intent myIntent = new Intent(CheckList.this, MyPage.class);
        myIntent.putExtra("id", id);
        startActivity(myIntent);
        finish();
    }

    //db에 있는 체크리스트 목록 보여주기
    class SelectList extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("no data")) { }
            else{
                int cnt = 0;
                final String[] arr = s.split(",");
                TableRow tr[] = new TableRow[arr.length/2];

                for(int i=0; i<arr.length-1; i+=2){
                    tr[cnt] = new TableRow(CheckList.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    tr[cnt].setLayoutParams(lp);

                    final CheckBox box = new CheckBox(CheckList.this);
                    box.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                    box.setText(arr[i]);
                    box.setTextSize(20);

                    if(arr[i+1].equals("check"))
                        box.setChecked(true);
                    else
                        box.setChecked(false);

                    Button deleteBtn = new Button(CheckList.this);
                    deleteBtn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    deleteBtn.setText("삭제");

                    tr[cnt].addView(box);
                    tr[cnt].addView(deleteBtn);
                    tr[cnt].setPadding(0,5,0,20);

                    table.addView(tr[cnt],lp);
                    cnt++;
                }
                //삭제버튼 클릭시
                for(int i=0; i<table.getChildCount(); i++){
                    TableRow row = (TableRow)table.getChildAt(i);
                    final CheckBox box = (CheckBox)row.getChildAt(0);
                    final Button deleteBtn = (Button)row.getChildAt(1);
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckDelete task = new CheckDelete();
                            task.execute(no, box.getText().toString());
                        }
                    });
                }
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                String no = strings[0];
                String link = "http://cs2020tv.dongyangmirae.kr/checkSelect.php";
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
    //체크리스트 항목 삽입
    public void checkInsert(View view){
        String clist = checkEdit.getText().toString();
        String check = "uncheck";

        if(clist.equals(""))
            Toast.makeText(CheckList.this, "항목을 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            InsertData task = new InsertData();
            task.execute(no, clist, check);
            finish();
            startActivity(getIntent());
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(CheckList.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String clist = (String) params[1];
                String check = (String) params[2];

                String link = "http://cs2020tv.dongyangmirae.kr/checklist.php";
                String data = "no=" + no + "&clist=" + clist + "&check=" + check;

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

    //체크여부 업데이트
    class CheckUpdate extends AsyncTask<ArrayList, Void, ArrayList>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*@Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }*/

        @Override
        protected ArrayList doInBackground(ArrayList... params) {
            try{
                String no = params[0].get(0).toString();

                String link = "http://cs2020tv.dongyangmirae.kr/checkUpdate.php";
                String data = "no=" + no;
                for(int i=1; i<params[0].size()-1; i++){
                    data = data + "&clist" + i + "=" + params[0].get(i).toString();
                }
                data = data + "&c=" + params[0].get(params[0].size()-1);

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
            } catch(Exception e){
            }
            return null;
        }
    }

    //삭제
    class CheckDelete extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            finish();
            startActivity(getIntent());
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String clist = (String) params[1];

                String link = "http://cs2020tv.dongyangmirae.kr/checkDelete.php";
                String data = "no=" + no + "&clist=" + clist;

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
