package com.example.rainfallapp.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.rainfallapp.R;

import java.text.DateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView tvAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tvAlarm = findViewById(R.id.textViewAlarm);

        Button buttonTimePicker = findViewById(R.id.buttonOpenAlarm);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button buttonCancelAlarm = findViewById(R.id.buttonCancelAlarm);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        updateTimeText(calendar);
        startAlarm(calendar);
    }

    private void updateTimeText(Calendar calendar) {
        String time = "Forecast alarm set for: ";
        time += DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        tvAlarm.setText(time);
    }

    private void startAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
//        if (calendar.before(Calendar.getInstance())) {
//            calendar.add(Calendar.DATE, 1);
//        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @SuppressLint("SetTextI18n")
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        tvAlarm.setText("Alarm canceled");
    }
}
