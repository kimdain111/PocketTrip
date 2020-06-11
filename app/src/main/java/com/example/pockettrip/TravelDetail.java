package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TravelDetail extends Activity {
    String no;
    String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_detail);

        Intent myIntent = getIntent();
        no = myIntent.getExtras().getString("no");
        id= myIntent.getExtras().getString("id");
        TravelDetailData task = new TravelDetailData();
        task.execute(no);
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(TravelDetail.this,TravelMain.class);
        intent2.putExtra("id", id);
        startActivity(intent2);
        finish();
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
        final LinearLayout layout1 = findViewById(R.id.layout1);
        final TableLayout layout2 = findViewById(R.id.layout2);
        final TableLayout layout3 = findViewById(R.id.layout3);
        final LinearLayout layout4 = findViewById(R.id.layout4);
        ImageButton publicbtn = findViewById(R.id.publicbtn);
        ImageButton privatebtn = findViewById(R.id.privatebtn);
        ImageButton xbtn = findViewById(R.id.xbtn);

        layout1.setVisibility(View.INVISIBLE);
        layout2.setVisibility(View.INVISIBLE);
        layout3.setVisibility(View.INVISIBLE);
        layout4.setVisibility(View.VISIBLE);

        publicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(TravelDetail.this, PublicMoneyMain.class);
                myIntent.putExtra("no", no);
                startActivity(myIntent);
                finish();
            }
        });
        privatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(TravelDetail.this, PrivateMoneyMain.class);
                myIntent.putExtra("no", no);
                startActivity(myIntent);
                finish();
            }
        });
        xbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.GONE);
            }
        });

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
            ImageView tImg = findViewById(R.id.travelimg);
            TextView text = findViewById(R.id.traveltext);

            final String[] arr = s.split(",");
            final Bitmap[] bitmap = new Bitmap[1];
            Thread uThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        URL url = new URL(arr[3]);
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmap[0] = BitmapFactory.decodeStream(is);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            uThread.start();
            try{
                uThread.join();
                tImg.setImageBitmap(bitmap[0]);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
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
