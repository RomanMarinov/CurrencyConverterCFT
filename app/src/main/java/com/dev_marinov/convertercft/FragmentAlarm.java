package com.dev_marinov.convertercft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class FragmentAlarm extends Fragment {

    View frag;
    //Компонент TimePicker позволяет пользователю выбрать время (часы, минуты).
    // Данный виджет используют как правило на отдельном экране
    TimePicker timePicker;
    Button startAlarm, stopAlarm;
    TextView tvStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frag = inflater.inflate(R.layout.fragment_alarm, container, false);

        timePicker = frag.findViewById(R.id.timePicker);
        tvStatus = frag.findViewById(R.id.tvStatus);
        tvStatus.setText("" + ((MainActivity)getActivity()).loadSettingString("statusAlarm", ""));

        // запуск alarmManager
        startAlarm = frag.findViewById(R.id.startAlarm);
        startAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Нам нужен объект календаря, чтобы получить указанное время в миллисекундах.
                // поскольку метод диспетчера аварийных сигналов требует времени в миллисекундах для установки аварийного сигнала
                Calendar calendar = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    calendar.set(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getHour(), timePicker.getMinute(), 0);
                } else {
                    calendar.set(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                }
                startAlarm(calendar.getTimeInMillis());
            }
        });
        // остановка alarmManager
        stopAlarm = frag.findViewById(R.id.stopAlarm);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        });

        return frag;
    }

    private void startAlarm(long time) {
        // создание менеджера
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);////////
        //создание нового намерения с указанием широковещательного приемника
        Intent intent = new Intent(getActivity(), MyAlarm.class);
        //создание отложенного намерения с использованием намерения
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        //установка повторяющегося будильника, который будет срабатывать каждый день
        alarmManager.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pendingIntent);
        //Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
        tvStatus.setText("статус : установлено");
        ((MainActivity)getActivity()).saveSettingString("statusAlarm", tvStatus.getText().toString());
    }
    public void stopAlarm()
    {
        // создание менеджера
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);////////
        //создание нового намерения с указанием широковещательного приемника
        Intent intent = new Intent(getActivity(), MyAlarm.class);
        //создание отложенного намерения с использованием намерения
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        alarmManager.cancel(pendingIntent);
        //Toast.makeText(this, "Alarm not set", Toast.LENGTH_SHORT).show();
        tvStatus.setText("статус : не установлено");
        ((MainActivity)getActivity()).saveSettingString("statusAlarm", tvStatus.getText().toString());
    }
}