package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class TravelMain extends Activity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_main);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        Intent intent2 = getIntent();
        id = intent.getStringExtra("id");

        TravelMainData task = new TravelMainData();
        task.execute(id);
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
            TextView text = findViewById(R.id.noTrip);
            TableLayout table = findViewById(R.id.table);
            ImageView tImg = findViewById(R.id.travelImg);
            TextView tText = findViewById(R.id.travelText);

            if(s.equals("no data")){
                text.setVisibility(View.VISIBLE);
                table.setVisibility(View.GONE);
            }
            else{
                text.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);

                String[] arr = s.split(",");
                //Uri uri = Uri.parse(arr[3]);

                tImg.setImageURI(Uri.parse(arr[3]));
                tText.setText(arr[0]+"\n"+arr[1]+"~"+arr[2]);
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
