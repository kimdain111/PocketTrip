package com.example.pockettrip;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class TravelChoice extends AppCompatActivity {

    private Button firstDate;
    private Button lastDate;
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
        setContentView(R.layout.travel_choice); //activity_main.xml을 화면에 표시하라

        this.InitializeView();
        this.InitializeListener();

        this.InitializeView2();
        this.InitializeListener2();
    }

    public void InitializeView(){
        firstDate = (Button)findViewById(R.id.firstDate);
    }

    public void InitializeListener(){

        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int datOfMonth) {
                firstDate.setText(year + "년"+(monthOfYear+1)+"월"+datOfMonth+"일");
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
                lastDate.setText(year + "년"+(monthOfYear+1)+"월"+datOfMonth+"일");
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
}



