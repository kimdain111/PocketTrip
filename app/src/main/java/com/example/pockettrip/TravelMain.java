package com.example.pockettrip;

import android.app.Activity;
import android.app.ProgressDialog;
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
                    //Uri uri = Uri.parse(arr[i+4]);
                    //tImg.setImageURI(uri);
                    /*try{
                        InputStream in = getContentResolver().openInputStream(uri);

                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();

                        tImg.setImageBitmap(img);
                    } catch(Exception e){
                    }*/
                    tImg.setImageResource(R.drawable.default_gallery);
                    tImg.setLayoutParams(new LayoutParams(250,250));
                    tImg.setPadding(0,0,50,0);

                    TextView tText = new TextView(TravelMain.this);
                    tText.setText(arr[i+1]+"\n"+arr[i+2]+"~"+arr[i+3]);
                    tText.setTextSize(20);
                    tText.setGravity(Gravity.CENTER);
                    tText.setPadding(0,40,0,0);

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
                    tr[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent3 = new Intent(TravelMain.this,TravelDetail.class);
                            intent3.putExtra("no", no[finalJ]);
                            startActivity(intent3);
                            finish();
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
