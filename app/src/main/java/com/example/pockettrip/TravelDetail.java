package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TravelDetail extends Activity {
    String no;
    String id;

    String nation, engNation, first, last, nationName;
    String rate, cName;

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

    //마이페이지
    public void goMypage(View view)
    {
        Intent myIntent = new Intent(TravelDetail.this, MyPage.class);
        myIntent.putExtra("id", id);
        startActivity(myIntent);
        finish();
    }

    public void goSchedule(View view)
    {
        Intent myIntent = new Intent(TravelDetail.this, ScheduleMain.class);
        myIntent.putExtra("no", no);
        myIntent.putExtra("id", id);
        startActivity(myIntent);
        finish();
    }
    public void goDiary(View view)
    {
        Intent myIntent = new Intent(TravelDetail.this, DiaryMain.class);
        myIntent.putExtra("no", no);
        myIntent.putExtra("id", id);
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
                myIntent.putExtra("id", id);
                myIntent.putExtra("first", first);
                myIntent.putExtra("last", last);
                myIntent.putExtra("rate", rate);
                myIntent.putExtra("country", cName);
                startActivity(myIntent);
                finish();
            }
        });
        privatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(TravelDetail.this, PrivateMoneyMain.class);
                myIntent.putExtra("no", no);
                myIntent.putExtra("id", id);
                myIntent.putExtra("first", first);
                myIntent.putExtra("last", last);
                myIntent.putExtra("rate", rate);
                myIntent.putExtra("country", cName);
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
        Intent intent = new Intent(TravelDetail.this, CheckList.class);
        intent.putExtra("no", no);
        intent.putExtra("id", id);
        intent.putExtra("nation", nation);
        intent.putExtra("first", first);
        intent.putExtra("last", last);
        startActivity(intent);
        finish();
    }

    class TravelDetailData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(TravelDetail.this, "Please Wait", null, true, true);
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
            text.setText(arr[0]+"\n\n"+arr[1]+"\n~"+arr[2]);

            nation = arr[0];
            first = arr[1];
            last = arr[2];

            switch(nation){
                case "한국":
                    engNation="Asia/Seoul";
                    break;
                case "일본":
                    engNation="Asia/Tokyo";
                    break;
                case "중국":
                    engNation="Asia/Shanghai"; //Etc/GMT+8/
                    break;
                case "싱가포르":
                    engNation="Asia/Singapore";
                    break;
                case "홍콩":
                    engNation="Hongkong";
                    break;
                case "독일":
                    engNation="Europe/Berlin";
                    break;
                case "스위스":
                    engNation="Europe/Zurich";
                    break;
                case "이탈리아":
                    engNation="Europe/Rome";
                    break;
                case "오스트리아":
                    engNation="Europe/Vienna";
                    break;
                case "영국":
                    engNation="Europe/London";
                    break;
                case "프랑스":
                    engNation="Europe/Paris";
                    break;
                case "미국":
                    engNation="America/Chicago"; //washingtonD.c가 없어서
                    break;
                case "호주":
                    engNation="Australia/Canberra";
                    break;

            }
            //시간표시
            TimeZone tz;
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            tz = TimeZone.getTimeZone(engNation);
            df.setTimeZone(tz);
            TextView time = findViewById(R.id.nationTime);
            time.setText(df.format(date));

            //날씨
            getWeather(engNation);

            //환율
            getExchange(nation);
        }

    }
    //날씨api 가져오기
    public void getWeather(String engNation)
    {
        String nation[] = engNation.split("/");
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+nation[1]+"&appid=dd186abd9c63e0a01024c72900aeaaeb";
        OpenWeather weatherTask = new OpenWeather();
        weatherTask.execute(url);
    }
    class OpenWeather extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null){
                String nowTemp = "";
                String maxTemp = "";
                String minTemp = "";
                String main ="";
                try{
                    nowTemp = String.valueOf(Math.round(Double.parseDouble(jsonObject.getJSONObject("main").getString("temp"))-273.15));
                    maxTemp = String.valueOf(Math.round(Double.parseDouble(jsonObject.getJSONObject("main").getString("temp_max"))-273.15));
                    minTemp = String.valueOf(Math.round(Double.parseDouble(jsonObject.getJSONObject("main").getString("temp_min"))-273.15));
                    main = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                transferWeather(main);

                TextView temp = findViewById(R.id.temperature);
                temp.setText(nowTemp+"°C\n"+maxTemp+"°/"+minTemp+"°");
            }
        }

        @Override
        protected JSONObject doInBackground(String... datas) {
            try{
                HttpURLConnection conn = (HttpURLConnection)new URL(datas[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String readed;
                    while((readed=br.readLine())!=null){
                        JSONObject jObject = new JSONObject(readed);
                        return jObject;
                    }
                }
                else{
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void transferWeather(String weather)
        {
            ImageView weatherIcon = findViewById(R.id.weatherIcon);
            if(weather.equals("Thunderstorm"))
                weatherIcon.setImageResource(R.drawable.thunderstorm_icon);
            else if(weather.equals("Drizzle"))
                weatherIcon.setImageResource(R.drawable.drizzle_icon);
            else if(weather.equals("Rain"))
                weatherIcon.setImageResource(R.drawable.rain_icon);
            else if(weather.equals("Snow"))
                weatherIcon.setImageResource(R.drawable.snow_icon);
            else if(weather.equals("Mist"))
                weatherIcon.setImageResource(R.drawable.mist_icon);
            else if(weather.equals("Clear"))
                weatherIcon.setImageResource(R.drawable.clear_icon);
            else if(weather.equals("Clouds"))
                weatherIcon.setImageResource(R.drawable.clouds_icon);
        }
    }
    //환율크롤링
    private void getExchange(String nation)
    {
        nationName = nation;
        String url = "https://finance.naver.com/marketindex/exchangeList.nhn";
        ExchangeRate rateTask = new ExchangeRate();
        rateTask.execute(url);
    }

    class ExchangeRate extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jObject) {
            ArrayList rateArr = new ArrayList();
            String country = ""; String sale = "";
           try{
               for(int i =0; i<jObject.getJSONArray("exchange").length(); i++){
                   country = jObject.getJSONArray("exchange").getJSONObject(i).getString("country");
                   sale = jObject.getJSONArray("exchange").getJSONObject(i).getString("sale");
                   rateArr.add(country); rateArr.add(sale);
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

            TextView nationText = findViewById(R.id.nationText);
            TextView rateText = findViewById(R.id.rateText);
           for(int i=0; i<rateArr.size(); i++){
               String countryName = (String) rateArr.get(i);
               if(countryName.contains(nationName)) //미국, 일본, 중국, 홍콩, 영국, 스위스, 호주, 싱가포르
               {
                   nationText.setText(countryName);
                   rateText.setText(rateArr.get(i+1).toString());
                   rate = rateArr.get(i+1).toString();
                   cName = countryName;
               }
               else if(countryName.equals("유럽연합 EUR"))
               {
                   nationText.setText(countryName);
                   rateText.setText(rateArr.get(i+1).toString());
                   rate = rateArr.get(i+1).toString();
                   cName = countryName;
               }
               else{
                   nationText.setText("-");
                   rateText.setText("-");
               }
           }
        }

        @Override
        protected JSONObject doInBackground(String... datas) {
            JSONObject result = new JSONObject();
            JSONArray arr = new JSONArray();
            Document doc = null;
            try{
                //환율정보 스크래핑
                doc = Jsoup.connect(datas[0]).get();
                //국가명, 환율
                Elements country = doc.select(".tit");
                Elements sale = doc.select(".sale");

                for(int i=0; i<country.size(); i++){
                    Element country_el = country.get(i);
                    Element sale_el = sale.get(i);
                    JSONObject obj = new JSONObject();

                    obj.put("country", country_el.text());
                    obj.put("sale", sale_el.text());
                    arr.put(obj);
                }
                result.put("result", "success");
                result.put("exchange", arr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}