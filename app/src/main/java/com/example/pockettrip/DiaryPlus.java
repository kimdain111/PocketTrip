package com.example.pockettrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

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

public class DiaryPlus extends Activity {

    private static final int REQUEST_CODE = 0;
    private int mYear, mMonth, mDay;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    ImageView imgList1, imgList2, imgList3, diaryImg;
    private EditText titleText, contentText;
    private RadioGroup weatherGroup, emotionGroup;
    private TextView dateText;
    private String weather="weather1", emotion="emotion1", no, id, chDate, flag, title, content;
    Uri photoUri;
    String upLoadServerUri = "http://cs2020tv.dongyangmirae.kr/uploadImage.php";
    String imgUrl, imgName;
    int serverResponseCode = 0;
    boolean selectFlag = false; //갤러리에서 이미지 선택했는지 안했는지

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_plus);

        Intent intent = getIntent();
        flag = intent.getExtras().getString("flag");
        id= intent.getExtras().getString("id");
        no = intent.getExtras().getString("no");
        chDate = intent.getExtras().getString("selectDate");

        if(flag.equals("true")){ //수정화면으로 들어왔을 때 intent 추가로 받기
            title = intent.getExtras().getString("title");
            chDate = intent.getExtras().getString("selectDate");
            weather = intent.getExtras().getString("weather");
            emotion = intent.getExtras().getString("emotion");
            content = intent.getExtras().getString("content");
        }

        imgList1 = (ImageView)findViewById(R.id.imgList1);
        /*imgList2 = (ImageView)findViewById(R.id.imgList2);
        imgList3 = (ImageView)findViewById(R.id.imgList3);*/

        titleText = (EditText)findViewById(R.id.titleText);
        contentText = (EditText)findViewById(R.id.contentText);

        weatherGroup = (RadioGroup)findViewById(R.id.weatherGroup);
        emotionGroup = (RadioGroup)findViewById(R.id.emotionGroup);

        dateText = (TextView)findViewById(R.id.chDate);
        dateText.setText(chDate);

        //라디오버튼 값 가져오기
        weatherGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.weather1:
                        weather = "weather1";
                        break;
                    case R.id.weather2:
                        weather = "weather2";
                        break;
                    case R.id.weather3:
                        weather = "weather3";
                        break;
                    case R.id.weather4:
                        weather = "weather4";
                        break;
                    case R.id.weather5:
                        weather = "weather5";
                        break;
                }
            }
        });

        emotionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.emotion1:
                        emotion = "emotion1";
                        break;
                    case R.id.emotion2:
                        emotion = "emotion2";
                        break;
                    case R.id.emotion3:
                        emotion = "emotion3";
                        break;
                    case R.id.emotion4:
                        emotion = "emotion4";
                        break;
                    case R.id.emotion5:
                        emotion = "emotion5";
                        break;
                }
            }
        });

        //사진 버튼
        diaryImg = (ImageView)findViewById(R.id.imgList1);
        diaryImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true); //사진 여러장 선택
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICTURE_REQUEST_CODE);*/
                selectFlag = true;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        //수정/삭제로 들어온 경우 기본값 설정
        if(flag.equals("true")){
            Button okbtn = findViewById(R.id.okBtn);
            Button deleteBtn = findViewById(R.id.delete);
            okbtn.setText("수정");                //수정버튼으로 바꿔주기
            deleteBtn.setVisibility(View.VISIBLE);

            //제목, 날짜, 날씨, 감정, 내용 setText
            titleText.setText(title);
            dateText.setText(chDate);
            switch(weather){
                case "weather1":
                    weatherGroup.check(R.id.weather1);
                    break;
                case "weather2":
                    weatherGroup.check(R.id.weather2);
                    break;
                case "weather3":
                    weatherGroup.check(R.id.weather3);
                    break;
                case "weather4":
                    weatherGroup.check(R.id.weather4);
                    break;
                case "weather5":
                    weatherGroup.check(R.id.weather5);
                    break;
            }
            switch(emotion){
                case "emotion1":
                    emotionGroup.check(R.id.emotion1);
                    break;
                case "emotion2":
                    emotionGroup.check(R.id.emotion2);
                    break;
                case "emotion3":
                    emotionGroup.check(R.id.emotion3);
                    break;
                case "emotion4":
                    emotionGroup.check(R.id.emotion4);
                    break;
                case "emotion5":
                    emotionGroup.check(R.id.emotion5);
                    break;
            }
            contentText.setText(content);

            SelectImg selectTask = new SelectImg();
            selectTask.execute(title, content);
        }
    }

    @Override
    public void onBackPressed() { //뒤로가기
        Intent intent = new Intent(DiaryPlus.this,DiaryMain.class);
        intent.putExtra("id", id);
        intent.putExtra("no", no);
        startActivity(intent);
        finish();
    }

    //갤러리 통해서 사진 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK){
                photoUri = data.getData();
                //travelImg.setImageURI(photoUri);
                try{
                    InputStream in = getContentResolver().openInputStream(photoUri);
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    diaryImg.setImageBitmap(img);
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
        /*if(requestCode == PICTURE_REQUEST_CODE){
            if(resultCode == RESULT_OK){

                //기존 이미지 지우기
                imgList1.setImageResource(0);
                imgList2.setImageResource(0);
                imgList3.setImageResource(0);

                //Uri 또는 ClipData를 가져옴
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                //이미지 URI를 이용하여 이미지뷰에 순서대로 세팅
                if(clipData!=null){
                    if(clipData.getItemCount() > 3){ //3장 초과로 선택하면
                        Toast.makeText(this, "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i=0;i<3; i++){
                            if(i<clipData.getItemCount()){
                                Uri uriOne = clipData.getItemAt(i).getUri();
                                switch (i){
                                    case 0:
                                        imgList1.setImageURI(uriOne);
                                        break;
                                    case 1:
                                        imgList2.setImageURI(uriOne);
                                        break;
                                    case 2:
                                        imgList3.setImageURI(uriOne);
                                        break;
                                }
                            }
                        }
                    }
                }else if(uri != null){
                    imgList1.setImageURI(uri);
                }

            }
        }*/
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

    //취소버튼 눌렀을 때(X버튼)
    public void cancel(View view){
        Intent myintent = new Intent(DiaryPlus.this,DiaryMain.class);
        myintent.putExtra("id", id);
        myintent.putExtra("no", no);
        startActivity(myintent);
        finish();
    }

    //확인버튼 눌렀을 때
    public void insert(View view){
        String titleTxt = titleText.getText().toString();
        String contentTxt = contentText.getText().toString();

        if(titleTxt.equals(""))
            Toast.makeText(DiaryPlus.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(contentTxt.equals(""))
            Toast.makeText(DiaryPlus.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(selectFlag == false && flag.equals("false"))
            Toast.makeText(DiaryPlus.this, "사진을 선택해 주세요", Toast.LENGTH_SHORT).show();
        else{
            if(flag.equals("true")) //수정화면일때떄
            {
                UpdateData task = new UpdateData();
                if(selectFlag)  //이미지 바꿨을때
                {
                    final String finalUrl = imgUrl;
                    UploadFile task2 = new UploadFile();
                    task2.execute(finalUrl);
                    String imgPath = "http://cs2020tv.dongyangmirae.kr/img/"+imgName;
                    task.execute(no, title, content, weather, emotion, imgPath, chDate, titleTxt, contentTxt);
                }
                else{
                    task.execute(no, title, content, weather, emotion, "notChangeImg", chDate, titleTxt, contentTxt);
                }
            }else{
                final String finalUrl = imgUrl;
                UploadFile task2 = new UploadFile();
                task2.execute(finalUrl);
                //1.execute메소드를 통해 AsyncTask실행
                String imgPath = "http://cs2020tv.dongyangmirae.kr/img/"+imgName;
                InsertData task = new InsertData();
                task.execute(no, titleTxt, contentTxt, weather, emotion, imgPath, chDate);
            }
        }
    }

    //삭제버튼 눌렀을 때
    public void diaryDelete(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(DiaryPlus.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteData task = new DeleteData();
                task.execute(no, title, content);

            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("다이어리를 삭제하겠습니까?");
        alert.show();

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
                            Toast.makeText(DiaryPlus.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("myTag", "upload file error: " + ex.getMessage(), ex);

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DiaryPlus.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("myTag", "Exception : " + e.getMessage(), e);
                }
                return serverResponseCode;
            } // End else block
        }
    }

    //DB 저장 클래스
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //2. 스레드 작업 이전에 수행할 동작구현 (ex.이미지로딩중에 이미지 띄워놓기)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(DiaryPlus.this, "Please Wait", null, true, true);
        }
        //4. 결과파라미터 리턴받아서 리턴값을 통해 스레드작업 끝났을 때의 동작 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("diary plus success")){
                Toast.makeText(getApplicationContext(),"다이어리가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(DiaryPlus.this,DiaryMain.class);
                myintent.putExtra("no", no);
                startActivity(myintent);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
        }
        //3. 백그라운드작업 수행(execute메소드 호출할 때 사용된 파라미터 전달받음)
        @Override
        protected String doInBackground(String... params) {
            try{
                String no = (String) params[0];
                String title = (String) params[1];
                String content = (String) params[2];
                String weather = (String) params[3];
                String emotion = (String) params[4];
                String photo = (String) params[5];
                String date = (String) params[6];

                String link = "http://cs2020tv.dongyangmirae.kr/diary_plus.php";
                //전송할 데이터는 "이름=값"형식, 여러개를 보낼시에는 사이에 &추가
                //여기에 적어준 이름을 나중에 php에서 사용해 값을 얻음
                String data = "no=" + no + "&title=" + title + "&content=" + content + "&weather=" + weather + "&emotion=" + emotion + "&photo=" + photo + "&date=" + date;
                System.out.println("post 전송");
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
                        System.out.println("사진경로 : "+s);
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
                diaryImg.setImageBitmap(bitmap[0]);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... datas) {
            try{
                String title = datas[0];
                String content = datas[1];
                String link = "http://cs2020tv.dongyangmirae.kr/diaryImgSelect.php";
                String data = "title="+title + "&content="+content;

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
                Toast.makeText(getApplicationContext(),"다이어리가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(DiaryPlus.this,DiaryMain.class);
                intent2.putExtra("id", id);
                intent2.putExtra("no", no);
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
                String originalTitle = (String) params[1];
                String originalContent = (String) params[2];
                String weather = (String) params[3];
                String emotion = (String) params[4];
                String imgUrl = (String) params[5];
                String date = (String) params[6];
                String title = (String) params[7];
                String content = (String) params[8];

                String link = "http://cs2020tv.dongyangmirae.kr/diaryUpdate.php";
                String data = "no=" + no + "&originalTitle=" + originalTitle + "&weather=" + weather + "&emotion=" + emotion + "&originalContent=" + originalContent + "&imgUrl=" + imgUrl + "&title=" + title + "&content="+content;

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
                Toast.makeText(getApplicationContext(),"다이어리가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(DiaryPlus.this, DiaryMain.class);
                intent2.putExtra("id", id);
                intent2.putExtra("no", no);
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
                String title = (String) params[1];
                String content = (String) params[2];

                String link = "http://cs2020tv.dongyangmirae.kr/diaryDelete.php";
                String data = "no="+no+"&title=" + title + "&content=" + content;

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
