package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TravelChoice extends Activity {

    private Button firstDate;
    private Button lastDate;
    private static final int REQUEST_CODE = 0;
    ImageView travelImg;
    String id, no;
    String imgUrl;
    String nation;
    Uri photoUri;
    String imgName;
    String upLoadServerUri = "http://cs2020tv.dongyangmirae.kr/uploadImage.php";
    int serverResponseCode = 0;
    boolean selectFlag = false; //갤러리에서 이미지 선택했는지 안했는지

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

    String selectedFirst, selectedLast;
    String first[]; //수정을 통해 들어올 때 변경 전 날짜
    String last[];

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

        Intent myIntent2 = getIntent();
        id = myIntent2.getExtras().getString("id");
        no = myIntent2.getExtras().getString("no");
        nation = myIntent2.getExtras().getString("nation");
        selectedFirst = myIntent2.getExtras().getString("first");
        selectedLast = myIntent2.getExtras().getString("last");

        //선택한 여행지
        Spinner spinner = (Spinner)findViewById(R.id.nationSpinner);
        if(no != null){
            spinner.setSelection(getIndex(spinner, nation)); //수정할 나라로 기본값 해줌
            Button okbtn = findViewById(R.id.ok);
            Button deleteBtn = findViewById(R.id.delete);
            okbtn.setText("수정");                //수정버튼으로 바꿔주기
            deleteBtn.setVisibility(View.VISIBLE);

            //출발, 도착날짜 버튼 setText
            first = selectedFirst.split("-");
            last = selectedLast.split("-");
            firstDate.setText(first[0]+"-"+first[1]+"-"+first[2]);
            lastDate.setText(last[0]+"-"+last[1]+"-"+last[2]);

        }

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
                selectFlag = true;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        //수정을 통해 들어온 경우 기본 이미지
        if(no != null){
            SelectImg selectTask = new SelectImg();
            selectTask.execute(no);
        }
    }
    private int getIndex(Spinner spinner, String name)
    {
        for(int i=0; i<spinner.getCount();i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(name)){
                return i;
            }
        }
        return 0;
    }
    public void cancel(View view){
        Intent intent2 = new Intent(TravelChoice.this,TravelMain.class);
        intent2.putExtra("id", id);
        startActivity(intent2);
        finish();
    }
    public void travelDelete(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(TravelChoice.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteData task = new DeleteData();
                task.execute(no);

            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("여행지를 삭제하면 해당 다이어리, 가계부, 일정, 체크리스가 삭제됩니다.\n" +
                "삭제하시겠습니까?");
        alert.show();

    }
    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(TravelChoice.this,TravelMain.class);
        intent2.putExtra("id", id);
        startActivity(intent2);
        finish();
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
                    imgUrl = getRealPathFromURI(photoUri);
                    imgName = getFileName(photoUri);
                } catch(Exception e){
                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                selectFlag = false;
                Toast.makeText(this, "사진선택취소", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

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
        DatePickerDialog dialog;
        final Calendar c = Calendar.getInstance();

        //수정을 통해 들어왔을 때
        if(selectedFirst != null){
            mYear = Integer.parseInt(first[0]);
            mMonth = Integer.parseInt(first[1])-1;
            mDay = Integer.parseInt(first[2]);
            dialog = new DatePickerDialog(this, callbackMethod, mYear, mMonth , mDay);
            if(lastYear == 0){ //출발시간만 바꿀 때
                c.set(Integer.parseInt(last[0]), Integer.parseInt(last[1])-1, Integer.parseInt(last[2]));
            }
            else{
                c.set(lastYear, lastMonth-1, lastDay);
            }
            dialog.getDatePicker().setMaxDate(c.getTime().getTime());
            dialog.show();
        }
        //등록하는 경우
        else{
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            dialog = new DatePickerDialog(this, callbackMethod, mYear, mMonth , mDay);
            dialog.show();
        }
    }

    public void OnClickHandler2(View view) {
        Calendar minDate = Calendar.getInstance();
        DatePickerDialog dialog;
        if(selectedLast != null)
        {
            if(firstYear == 0){ //도착날짜만 수정할 때
                dialog = new DatePickerDialog(this, callbackMethod2, Integer.parseInt(first[0]), Integer.parseInt(first[1])-1, Integer.parseInt(first[2])+1);
                minDate.set(Integer.parseInt(first[0]), Integer.parseInt(first[1])-1, Integer.parseInt(first[2])+1);
            }
            else{
                dialog = new DatePickerDialog(this, callbackMethod2, firstYear, firstMonth-1, firstDay+1);
                minDate.set(firstYear, firstMonth-1, firstDay+1);
            }
        }
        else{
            dialog = new DatePickerDialog(this, callbackMethod2, firstYear, firstMonth-1, firstDay+1);
            minDate.set(firstYear, firstMonth-1, firstDay+1);
        }

        dialog.getDatePicker().setMinDate(minDate.getTime().getTime());
        dialog.show();
    }

    //DB저장
    public void travelInsert(View view){
        if(firstDate.getText().toString().equals(""))
            Toast.makeText(this, "출발날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
        else if(lastDate.getText().toString().equals(""))
            Toast.makeText(this,"도착날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
        else if(selectFlag == false && no == null)
            Toast.makeText(this,"사진을 선택해주세요", Toast.LENGTH_SHORT).show();
        else{
            if(no != null) //수정화면일때떄
            {
                UpdateData task = new UpdateData();
                if(selectFlag)  //이미지 바꿨을때
                {
                    final String finalUrl = imgUrl;
                    UploadFile task2 = new UploadFile();
                    task2.execute(finalUrl);
                    String imgPath = "http://cs2020tv.dongyangmirae.kr/img/"+imgName;
                    task.execute(no, id, nation, firstDate.getText().toString(), lastDate.getText().toString(), imgPath);
                }
                else{
                    task.execute(no, id, nation, firstDate.getText().toString(), lastDate.getText().toString(), "notChangeImg");
                }
            }
            else{
                final String finalUrl = imgUrl;
                //서버에 이미지 저장
                UploadFile task2 = new UploadFile();
                task2.execute(finalUrl);
                //DB에 저장
                String imgPath = "http://cs2020tv.dongyangmirae.kr/img/"+imgName;
                TravelData task = new TravelData();
                task.execute(id, nation, firstDate.getText().toString(), lastDate.getText().toString(), imgPath);
            }
        }
    }
    //서버에 이미지 저장하는 클래스
    class UploadFile extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String sourceFileUri = (String) strings[0];
            String fileName = sourceFileUri;
            HttpURLConnection conn = null;
            DataOutputStream dos = null;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(sourceFileUri);
            if (!sourceFile.isFile()) {
                Log.d("myTag", "Source File not exist ; "+fileName);
                return 0;
            }
            else
            {
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();

                    String serverResponseMessage = conn.getResponseMessage();

                    Log.d("myTag", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(TravelChoice.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("myTag", "upload file error: " + ex.getMessage(), ex);

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(TravelChoice.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("myTag", "Exception : " + e.getMessage(), e);
                }
                return serverResponseCode;
            } // End else block
        }
    }
    //여행지추가
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
    //수정을 통해 들어온 경우 기본 이미지 설정
    class SelectImg extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            final Bitmap[] bitmap = new Bitmap[1];
            Thread uThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        URL url = new URL(s);
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
                travelImg.setImageBitmap(bitmap[0]);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... datas) {
            try{
                String no = datas[0];
                String link = "http://cs2020tv.dongyangmirae.kr/travelImgSelect.php";
                String data = "no="+no;

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

    //여행지 정보 수정 DB 반영
    class UpdateData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("update")){
                Toast.makeText(getApplicationContext(),"여행지가 수정되었습니다.", Toast.LENGTH_SHORT).show();
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
                String no = (String) params[0];
                String id = (String) params[1];
                String nation = (String) params[2];
                String firstDate = (String) params[3];
                String lastDate = (String) params[4];
                String imgUrl = (String) params[5];

                String link = "http://cs2020tv.dongyangmirae.kr/travelUpdate.php";
                String data = "no=" + no + "&id=" + id + "&nation=" + nation + "&firstDate=" + firstDate + "&lastDate=" + lastDate + "&imgUrl=" + imgUrl;

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
            }catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }
        }
    }

    //여행지 정보 삭제
    class DeleteData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("delete")){
                Toast.makeText(getApplicationContext(),"여행지가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
                String no = (String) params[0];

                String link = "http://cs2020tv.dongyangmirae.kr/travelDelete.php";
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
            }catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }
        }
    }
}



