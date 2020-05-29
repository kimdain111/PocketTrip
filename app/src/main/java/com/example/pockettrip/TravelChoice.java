package com.example.pockettrip;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

public class TravelChoice extends AppCompatActivity {

    private Button firstDate;
    private Button lastDate;
    private static final int REQUEST_CODE = 0;
    ImageView travelImg;
    String id;
    String imgUrl;
    String nation;
    Uri photoUri;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private DatePickerDialog.OnDateSetListener callbackMethod2;

    private int mYear;
    private int mMonth;
    private int mDay;

    public int firstYear;
    public int firstMonth;
    public int firstDay;

    public int lastYear;
    public int lastMonth;
    public int lastDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_choice);

        this.InitializeView();
        this.InitializeListener();

        this.InitializeView2();
        this.InitializeListener2();

        Intent myIntent = getIntent();
        id = myIntent.getExtras().getString("id");

        //선택한 여행지
        Spinner spinner = (Spinner)findViewById(R.id.nationSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nation = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //여행지대표사진 설정
        travelImg = (ImageView)findViewById(R.id.travelImg);
        travelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK){
                photoUri = data.getData();

                try{
                    InputStream in = getContentResolver().openInputStream(photoUri);

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    travelImg.setImageBitmap(img);
                    //imgUrl = getRealPathFromURI(photoUri);
                } catch(Exception e){
                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "사진선택취소", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*private String getRealPathFromURI(Uri contentURI) {
        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            thePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return  thePath;
    }*/

    //이미지경로 절대경로를 실제경로로 바꿔주는 함수
    /*private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath(); }
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/

    //날짜 선택
    public void InitializeView(){
        firstDate = (Button)findViewById(R.id.firstDate);
    }

    public void InitializeListener(){

        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int datOfMonth) {
                firstDate.setText(year + "-"+(monthOfYear+1)+"-"+datOfMonth);
                firstYear = year;
                firstMonth = monthOfYear+1;
                firstDay = datOfMonth;
            }
        };
    }
    public void InitializeView2(){
        lastDate = (Button)findViewById(R.id.lastDate);
    }

    public void InitializeListener2(){
        callbackMethod2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int datOfMonth) {
                lastDate.setText(year + "-"+(monthOfYear+1)+"-"+datOfMonth);
                lastYear = year;
                lastMonth = monthOfYear+1;
                lastDay = datOfMonth;
            }
        };
    }

    public void OnClickHandler(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, mYear, mMonth , mDay);
        dialog.show();
    }

    public void OnClickHandler2(View view) {
        Calendar minDate = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod2, firstYear, firstMonth-1, firstDay+1);
        minDate.set(firstYear, firstMonth-1, firstDay+1);
        dialog.getDatePicker().setMinDate(minDate.getTime().getTime());
        dialog.show();
    }

    //DB저장
    public void travelInsert(View view){
        if(firstDate.getText().toString().equals(""))
            Toast.makeText(this, "출발날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
        else if(lastDate.getText().toString().equals(""))
            Toast.makeText(this,"도착날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
        else{
            TravelData task = new TravelData();
            //task.execute(id, nation, firstDate.getText().toString(), lastDate.getText().toString(), photoUri.toString());
            task.execute(id, nation, firstDate.getText().toString(), lastDate.getText().toString(), imgUrl);
        }
    }
    class TravelData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(TravelChoice.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("travel insert")){
                Toast.makeText(getApplicationContext(),"여행지가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(TravelChoice.this,TravelMain.class);
                intent2.putExtra("id", id);
                startActivity(intent2);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                String id = (String) params[0];
                String nation = (String) params[1];
                String firstDate = (String) params[2];
                String lastDate = (String) params[3];
                String imgUrl = (String) params[4];

                String link = "http://cs2020tv.dongyangmirae.kr/travelChoice.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼떄는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "id=" + id + "&nation=" + nation + "&firstDate=" + firstDate + "&lastDate=" + lastDate + "&imgUrl=" + imgUrl;

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

}



