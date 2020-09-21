package com.example.pockettrip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CheckList extends AppCompatActivity {

    String no, id, nation, first, last;
    Button insertButton;
    EditText checkEdit;
    RecyclerView recyclerView;
    private ArrayList<CheckDTO> checkArrayList;
    private CheckAdapter checkAdapter;

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

        recyclerView = (RecyclerView) findViewById(R.id.checklist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkArrayList = new ArrayList<>();
        checkAdapter = new CheckAdapter(checkArrayList);
        recyclerView.setAdapter(checkAdapter);
        checkEdit = (EditText) findViewById(R.id.editcheck);

    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(CheckList.this,TravelDetail.class);
        intent2.putExtra("id", id);
        intent2.putExtra("no", no);
        startActivity(intent2);
        finish();
    }

    public void checkInsert(View view){

        String clist = checkEdit.getText().toString();
        String check = "uncheck";

        if(clist.equals(""))
            Toast.makeText(CheckList.this, "체크리스트를 입력하세요", Toast.LENGTH_SHORT).show();
        else{
            //1.execute메소드를 통해 AsyncTask실행
            CheckDTO newCheck = new CheckDTO(clist);
            checkArrayList.add(newCheck);
            checkAdapter.notifyDataSetChanged();
            checkEdit.setText(null);
            CheckList.InsertData task = new CheckList.InsertData();
            task.execute(no, clist, check);
            Toast.makeText(CheckList.this, clist, Toast.LENGTH_SHORT).show();
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(CheckList.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");
            //init(arr[0], arr[1]);
        }
        //3. 백그라운드작업 수행(execute메소드 호출할 때 사용된 파라미터 전달받음)
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

    /*private void init(String l, String c){
        recyclerView = (RecyclerView) findViewById(R.id.checklist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        checkArrayList = new ArrayList<>();
        checkAdapter = new CheckAdapter(checkArrayList);
        recyclerView.setAdapter(checkAdapter);
    }*/
}
