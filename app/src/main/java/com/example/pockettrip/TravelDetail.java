package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class TravelDetail extends Activity {
    String no;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_detail);

        Intent myIntent = getIntent();
        no = myIntent.getExtras().getString("no");

        TravelDetailData task = new TravelDetailData();
        task.execute(no);
    }
    public void goSchedule(View view)
    {

    }
    public void goDiary(View view)
    {
        Intent myIntent = new Intent(TravelDetail.this, DiaryMain.class);
        myIntent.putExtra("no", no);
        startActivity(myIntent);
        finish();
    }
    public void goMoney(View view)
    {
        /*Intent myIntent = new Intent(TravelDetail.this, TravelChoice.class);
        myIntent.putExtra("no", no);
        startActivity(myIntent);
        finish();*/
    }
    public void goCheckList(View view)
    {

    }

    class TravelDetailData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(TravelDetail.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            TextView text = findViewById(R.id.traveltext);
            final String[] arr = s.split(",");

            text.setText(arr[0]+"\n"+arr[1]+"~"+arr[2]);
        }


        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/travelDetail.php";
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

}
