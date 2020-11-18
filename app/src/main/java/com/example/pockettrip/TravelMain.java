package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class TravelMain extends Activity {
    String id;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_main);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        Intent intent2 = getIntent();

        TravelMainData task = new TravelMainData();
        task.execute(id);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void goMypage(View view)
    {
        Intent myIntent = new Intent(TravelMain.this, MyPage.class);
        myIntent.putExtra("id", id);
        myIntent.putExtra("mypageFlag", "1");
        startActivity(myIntent);
        finish();
    }
    public void addTravel(View view)
    {
        Intent myIntent = new Intent(TravelMain.this, TravelChoice.class);
        myIntent.putExtra("id", id);
        startActivity(myIntent);
        finish();
    }

    class TravelMainData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(TravelMain.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            final String[] arr = s.split(",");
            final String[] no = new String[arr.length/5];

            TableLayout table = findViewById(R.id.table);
            TextView text = findViewById(R.id.noTrip);
            final TableRow tr[] = new TableRow[arr.length/5];

            if(s.equals("no data")){
                text.setVisibility(View.VISIBLE);
                table.setVisibility(View.GONE);
            }
            else{
                text.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);

                int cnt = 0;

                for(int i=0; i<arr.length; i+=5)
                {
                    tr[cnt] = new TableRow(TravelMain.this);
                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT);
                    tr[cnt].setLayoutParams(lp);

                    ImageView tImg = new ImageView(TravelMain.this);
                    final Bitmap[] bitmap = new Bitmap[1];
                    final int finalI = i;
                    Thread uThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                URL url = new URL(arr[finalI +4]);
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

                    tImg.setLayoutParams(new LayoutParams(300,300));
                    tImg.setPadding(0,0,50,0);

                    Typeface face = null; //폰트설정
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        face = getResources().getFont(R.font.like);
                    }
                    TextView tText = new TextView(TravelMain.this);
                    tText.setText(arr[i+1]+"\n"+arr[i+2]+"~"+arr[i+3]);
                    tText.setTextSize(20);
                    tText.setGravity(Gravity.CENTER);
                    tText.setPadding(0,60,0,0);
                    tText.setTypeface(face);

                    tr[cnt].addView(tImg);
                    tr[cnt].addView(tText);
                    tr[cnt].setPadding(0,5,0,20);
                    tr[cnt].setClickable(true);

                    no[cnt] = arr[i];
                    table.addView(tr[cnt],lp);
                    cnt++;
                }

                for(int j=0; j<tr.length; j++)
                {
                    final int finalJ = j;
                    //클릭
                    tr[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent3 = new Intent(TravelMain.this,TravelDetail.class);
                            intent3.putExtra("id", id);
                            intent3.putExtra("no", no[finalJ]);
                            startActivity(intent3);
                            finish();
                        }
                    });
                    //롱클릭
                    tr[j].setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(TravelMain.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent2 = new Intent(TravelMain.this,TravelChoice.class);
                                    intent2.putExtra("id", id);
                                    intent2.putExtra("no", no[finalJ]);
                                    intent2.putExtra("nation", arr[finalJ*5+1]);
                                    intent2.putExtra("first", arr[finalJ*5+2]);
                                    intent2.putExtra("last", arr[finalJ*5+3]);
                                    startActivity(intent2);
                                    finish();
                                }
                            });
                            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage("여행지를 수정/삭제하시겠습니까?");
                            alert.show();
                            return true;
                        }
                    });
                }
            }
            /*else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();*/
        }


        @Override
        protected String doInBackground(String... params) {
            try{
                String id = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/travelSelect.php";
                String data = "id=" + id;

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
