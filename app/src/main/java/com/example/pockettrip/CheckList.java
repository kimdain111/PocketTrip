package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class CheckList extends Activity {

    String no, id, nation, first, last, flag, tf1, tf2;
    EditText checkEdit;
    View view1, view2;
    ScrollView scroll1, scroll2;
    TableLayout table1, table2;

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

        //새로고침시 받아오는 intent
        Intent intent = getIntent();
        flag = intent.getExtras().getString("flag");
        no = intent.getExtras().getString("no");
        id= intent.getExtras().getString("id");
        nation = intent.getExtras().getString("nation");
        first = intent.getExtras().getString("first");
        last= intent.getExtras().getString("last");
        tf1= intent.getExtras().getString("tf1");
        tf2= intent.getExtras().getString("tf2");
        /*tabC1= intent.getExtras().getString("tabC1");
        tabC2= intent.getExtras().getString("tabC2");*/

        TextView nationTv = (TextView) findViewById(R.id.travel);
        TextView firstTv = (TextView) findViewById(R.id.travelFirst);
        TextView lastTv = (TextView) findViewById(R.id.travelLast);

        nationTv.setText(nation);
        firstTv.setText(first);
        lastTv.setText(last);

        checkEdit = (EditText) findViewById(R.id.editcheck);
        table1 = (TableLayout) findViewById(R.id.table1);
        table2 = (TableLayout) findViewById(R.id.table2);
        scroll1 = (ScrollView) findViewById(R.id.scroll1);
        scroll2 = (ScrollView) findViewById(R.id.scroll2);
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);

        if(flag == null)
            flag = "check1";
        if(flag.equals("check2"))
        {
            scroll1.setVisibility(View.GONE);
            scroll2.setVisibility(View.VISIBLE);
            view1.setVisibility(View.INVISIBLE);
            view2.setVisibility(View.VISIBLE);
        }
        else if(flag.equals("check1"))
        {
            scroll1.setVisibility(View.VISIBLE);
            scroll2.setVisibility(View.GONE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.INVISIBLE);
        }
        SelectList task = new SelectList();
        task.execute(no, flag);
    }

    @Override
    public void onBackPressed() {
        final ArrayList<String> checkArr = new ArrayList<String>();
        checkArr.add(no);
        if(flag.equals("check1")){
            for(int i=0; i<table1.getChildCount(); i++){
                TableRow row = (TableRow)table1.getChildAt(i);
                final CheckBox box = (CheckBox)row.getChildAt(0);
                if(box.isChecked()){
                    checkArr.add(box.getText().toString());
                }
            }
        }
        else if(flag.equals("check2")){
            for(int i=0; i<table2.getChildCount(); i++){
                TableRow row = (TableRow)table2.getChildAt(i);
                final CheckBox box = (CheckBox)row.getChildAt(0);
                if(box.isChecked()){
                    checkArr.add(box.getText().toString());
                }
            }
        }
        checkArr.add(Integer.toString(checkArr.size()-1));
        checkArr.add(flag);
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
        if(flag.equals("check1")){
            for(int i=0; i<table1.getChildCount(); i++){
                TableRow row = (TableRow)table1.getChildAt(i);
                final CheckBox box = (CheckBox)row.getChildAt(0);
                if(box.isChecked()){
                    checkArr.add(box.getText().toString());
                }
            }
        }
        else if(flag.equals("check2")){
            for(int i=0; i<table2.getChildCount(); i++){
                TableRow row = (TableRow)table2.getChildAt(i);
                final CheckBox box = (CheckBox)row.getChildAt(0);
                if(box.isChecked()){
                    checkArr.add(box.getText().toString());
                }
            }
        }
        checkArr.add(Integer.toString(checkArr.size()-1));
        CheckUpdate task2 = new CheckUpdate();
        task2.execute(checkArr);

        Intent myIntent = new Intent(CheckList.this, MyPage.class);
        myIntent.putExtra("id", id);
        myIntent.putExtra("no", no);
        myIntent.putExtra("mypageFlag", "7");
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
                    Typeface face = null; //폰트설정
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        face = getResources().getFont(R.font.like);
                    }
                    box.setTypeface(face);
                    box.setText(arr[i]);
                    box.setTextSize(20);

                    if(arr[i+1].equals("check"))
                        box.setChecked(true);
                    else
                        box.setChecked(false);

                    Button deleteBtn = new Button(CheckList.this);
                    deleteBtn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    deleteBtn.setText("삭제");
                    deleteBtn.setTypeface(face);
                    tr[cnt].addView(box);
                    tr[cnt].addView(deleteBtn);
                    tr[cnt].setPadding(0,5,0,20);

                    if(flag.equals("check1"))
                        table1.addView(tr[cnt],lp);
                    else if(flag.equals("check2"))
                        table2.addView(tr[cnt],lp);
                    cnt++;
                }
                //삭제버튼 클릭시
                if(flag.equals("check1")){
                    for(int i=0; i<table1.getChildCount(); i++){
                        TableRow row = (TableRow)table1.getChildAt(i);
                        final CheckBox box = (CheckBox)row.getChildAt(0);
                        final Button deleteBtn = (Button)row.getChildAt(1);
                        deleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckDelete task = new CheckDelete();
                                task.execute(no, box.getText().toString(), flag);
                            }
                        });
                    }
                }
                else if(flag.equals("check2")){
                    for(int i=0; i<table2.getChildCount(); i++){
                        TableRow row = (TableRow)table2.getChildAt(i);
                        final CheckBox box = (CheckBox)row.getChildAt(0);
                        final Button deleteBtn = (Button)row.getChildAt(1);
                        deleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckDelete task = new CheckDelete();
                                task.execute(no, box.getText().toString(), flag);
                            }
                        });
                    }
                }
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                String no = strings[0];
                String flag = strings[1];
                String link = "http://cs2020tv.dongyangmirae.kr/checkSelect.php";
                String data = "no=" + no + "&flag=" + flag;

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
    //기념품탭클릭
    public void check1Click(View view){
        final ArrayList<String> checkArr = new ArrayList<String>();
        checkArr.add(no);
        for(int i=0; i<table2.getChildCount(); i++){
            TableRow row = (TableRow)table2.getChildAt(i);
            final CheckBox box = (CheckBox)row.getChildAt(0);
            if(box.isChecked()){
                checkArr.add(box.getText().toString());
            }
        }
        checkArr.add(Integer.toString(checkArr.size()-1));
        checkArr.add("check2");
        CheckUpdate task2 = new CheckUpdate();
        task2.execute(checkArr);

        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.INVISIBLE);
        scroll1.setVisibility(View.VISIBLE);
        scroll2.setVisibility(View.GONE);
        flag = "check1";
        if(tf1 == null && tf2 == null){
            SelectList task = new SelectList();
            task.execute(no, flag);
        }
        tf1 = "tf";
    }
    //준비물탭클릭
    public void check2Click(View view){
        final ArrayList<String> checkArr = new ArrayList<String>();
        checkArr.add(no);
        for(int i=0; i<table1.getChildCount(); i++){
            TableRow row = (TableRow)table1.getChildAt(i);
            final CheckBox box = (CheckBox)row.getChildAt(0);
            if(box.isChecked()){
                checkArr.add(box.getText().toString());
            }
        }
        checkArr.add(Integer.toString(checkArr.size()-1));
        checkArr.add("check1");


        view1.setVisibility(View.INVISIBLE);
        view2.setVisibility(View.VISIBLE);
        scroll1.setVisibility(View.GONE);
        scroll2.setVisibility(View.VISIBLE);
        flag = "check2";
        if(tf2 == null && tf1 == null){
            Log.d("myTag", "들어옴");
            SelectList task = new SelectList();
            task.execute(no, flag);
        }
        Log.d("myTag", "못들어옴: " + tf1 + tf2);
        tf2 = "tf";
        CheckUpdate task2 = new CheckUpdate();
        task2.execute(checkArr);
    }
    //체크리스트 항목 삽입
    public void checkInsert(View view){
        String clist = checkEdit.getText().toString();
        String check = "uncheck";

        Intent intent = new Intent(CheckList.this, CheckList.class);
        intent.putExtra("flag", flag);
        intent.putExtra("no", no);
        intent.putExtra("id", id);
        intent.putExtra("nation", nation);
        intent.putExtra("first", first);
        intent.putExtra("last", last);

        if(clist.equals(""))
            Toast.makeText(CheckList.this, "항목을 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            final ArrayList<String> checkArr = new ArrayList<String>();
            checkArr.add(no);
            if(flag.equals("check1")) {
                for(int i=0; i<table1.getChildCount(); i++){
                    TableRow row = (TableRow)table1.getChildAt(i);
                    final CheckBox box = (CheckBox)row.getChildAt(0);
                    if(box.isChecked()){
                        checkArr.add(box.getText().toString());
                    }
                }

                InsertData task = new InsertData();
                task.execute(no, clist, check, flag);
                /*intent.putExtra("tf1", 0);
                intent.putExtra("tf2", 0);*/
            }
            else if(flag.equals("check2")) {
                for(int i=0; i<table2.getChildCount(); i++){
                    TableRow row = (TableRow)table2.getChildAt(i);
                    final CheckBox box = (CheckBox)row.getChildAt(0);
                    if(box.isChecked()){
                        checkArr.add(box.getText().toString());
                    }
                }
                InsertData task = new InsertData();
                task.execute(no, clist, check, flag);
                /*intent.putExtra("tf1", 0);
                intent.putExtra("tf2", 0);*/
            }
            checkArr.add(Integer.toString(checkArr.size()-1));
            checkArr.add(flag);
            CheckUpdate task2 = new CheckUpdate();
            task2.execute(checkArr);

            startActivity(intent);
            finish();
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
                String data = "no=" + no + "&clist=" + clist + "&check=" + check + "&flag=" + flag;

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
                data = data + "&c=" + params[0].get(params[0].size()-2) + "&flag=" + params[0].get(params[0].size()-1);

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
            Intent intent = new Intent(CheckList.this, CheckList.class);
            intent.putExtra("flag", flag);
            intent.putExtra("no", no);
            intent.putExtra("id", id);
            intent.putExtra("nation", nation);
            intent.putExtra("first", first);
            intent.putExtra("last", last);
            if(flag.equals("check1")){
                /*intent.putExtra("tf1", tf1);
                intent.putExtra("tf2", 0);*/
            }
            else if(flag.equals("check2")){
                /*intent.putExtra("tf1", 0);
                intent.putExtra("tf2", tf2);*/
            }
            startActivity(intent);
            finish();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String clist = (String) params[1];
                String flag = (String) params[2];

                String link = "http://cs2020tv.dongyangmirae.kr/checkDelete.php";
                String data = "no=" + no + "&clist=" + clist + "&flag=" + flag;

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
