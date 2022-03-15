package com.dev_marinov.convertercft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class FragmentCalc extends Fragment {

   View frag;
   TextView tvUpdate;
   Button btUpdate;
   TextView tvEditCurrencyLeft, tvEditCurrencyRight;
   CardView cardVEditCurrencyLeft, cardVEditCurrencyRight;
   TextView tvCharNameLeft, tvCharNameRight;
   EditText edPriceLeft, edPriceRight;
   String temp = "";
   TextWatcher textWatcherLeft, textWatcherRight;
   BroadcastReceiver broadcastReceiver;
   BroadcastReceiver broadcastReceiverHashMap;
   public final static String FIRST_ACTION = "first_action";
   public final static String SECOND_ACTION = "second_action";
   boolean flag; // переменная для костыля проблемы отправки дважды одинакого сообщения от broadcastReceiver
   boolean flagHashMap; // переменная для костыля проблемы отправки дважды одинакого сообщения от broadcastReceiverHashMap
   double tempRateValuteLeft, tempRateValuteRight;
   ProgressBar progressBar;
   ImageView imgUpdate, imgSetting;
   Handler handler;
   ConstraintLayout clMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("1111", "FragmentCalc загрузился");

        frag = inflater.inflate(R.layout.fragment_calc, container, false);
        handler = new Handler(Looper.getMainLooper()); // handler для главного потока
        clMain = frag.findViewById(R.id.clMain);

        tvUpdate = frag.findViewById(R.id.tvUpdate); // кнопка изменить валюту
        imgUpdate = frag.findViewById(R.id.imgUpdate); // кнопка обновить валюту
        imgSetting = frag.findViewById(R.id.imgSetting); // кнопка настроек alarmmanager обновления валюты
        progressBar = frag.findViewById(R.id.progressBar);


        cardVEditCurrencyLeft = frag.findViewById(R.id.cardVEditCurrencyLeft); // кнопка изменить валюту
        cardVEditCurrencyRight = frag.findViewById(R.id.cardVEditCurrencyRight); // кнопка изменить валюту

        tvCharNameLeft = frag.findViewById(R.id.tvCharNameLeft); // textview содержит сокр.назв валюты, т.е "USD" (keyValuteName)
        tvCharNameRight = frag.findViewById(R.id.tvCharNameRight); // textview содержит сокр.назв валюты, т.е "USD" (keyValuteName)

        edPriceLeft = frag.findViewById(R.id.edPriceLeft); // editText ввода и вывода данных калькуляции
        edPriceRight = frag.findViewById(R.id.edPriceRight); // editText ввода и вывода данных калькуляции

        // загурзка последних используемых валют tvCharNameLeft и tvCharNameRight
        tvCharNameLeft.setText(((MainActivity)getActivity()).loadSettingString("sharedPrefCharNameLeft", ""));
        tvCharNameRight.setText(((MainActivity)getActivity()).loadSettingString("sharedPrefCharNameRight", ""));
        // загурзка последних используемых валют tvCharNameLeft и tvCharNameRight
        edPriceLeft.setText(((MainActivity)getActivity()).loadSettingString("sharedPrefPriceLeft", ""));
        edPriceRight.setText(((MainActivity)getActivity()).loadSettingString("sharedPrefPriceRight", ""));

        imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getData();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                            cardVEditCurrencyLeft.setClickable(false);
                            cardVEditCurrencyRight.setClickable(false);
                    }
                });

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        cardVEditCurrencyLeft.setClickable(true);
                        cardVEditCurrencyRight.setClickable(true);
                    }
                };
                handler.postDelayed(runnable, 1500);
            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).flipCard("gofragmentAlarm"); // метод анимации и перехода во fragmentAlarm
            }
        });



        // клики по CardView это переход во FragmentList для просмотра спика валют для выбора
        cardVEditCurrencyLeft.setOnClickListener(new View.OnClickListener() { // если нажали изменить валюту слева
            @Override
            public void onClick(View view) {
                temp = "clickLeft"; // то запишем в общую переменную строку клика
                ((MainActivity)getActivity()).flipCard("goFragmentList"); // метод анимации и перехода во fragmentList
            }
        });
        cardVEditCurrencyRight.setOnClickListener(new View.OnClickListener() { // если нажали изменить валюту слева
            @Override
            public void onClick(View view) {
              temp = "clickRight"; // то запишем в общую переменную строку клика

//                FragmentList fragmentList = new FragmentList();
//                FragmentManager fragmentManager = ((MainActivity)getActivity()).getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.ll_frag_list, fragmentList);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

                ((MainActivity)getActivity()).flipCard("goFragmentList"); // метод анимации и перехода во fragmentList
            }
        });

        flag = true;  // переменная для костыля проблемы отправки дважды одинакого сообщения от broadcastReceiver
        // broadcastReceiver для получения значения клика выбранной валиюты из списка fragmentList в адаптере холдера
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // keyValuteName записывает имя валюты (например AUD)
                String keyValuteName= intent.getStringExtra("KeyValuteName");

                if(flag)  // переменная для костыля проблемы отправки дважды одинакого сообщения от broadcastReceiver
                {
                    //Log.e("ggg", "tempLeft =" + tempLeft + "  tempRight =" + tempRight + " и keyValuteName =" + keyValuteName);
                    if(temp.equals("clickLeft"))
                    {
                        Log.e("ggg", "пришел для левого keyValuteName=" + keyValuteName);
                        tvCharNameLeft.setText(keyValuteName);
                        ((MainActivity)getActivity()).saveSettingString("sharedPrefCharNameLeft" , keyValuteName);

                        ((MainActivity)context).getData(); // запрос на обновление данных для получения значения курсов
                    }
                    if(temp.equals("clickRight"))
                    {
                        Log.e("ggg", "пришел для правого keyValuteName=" + keyValuteName);
                        tvCharNameRight.setText(keyValuteName);
                        ((MainActivity)getActivity()).saveSettingString("sharedPrefCharNameRight" , keyValuteName);

                        ((MainActivity)context).getData(); // запрос на обновление данных для получения значения курсов
                    }
                    flag = false;
                }

            }
        };
        // создаем фильтр для BroadcastReceiver
        // Он будет запускаться в основном потоке активности (также как поток пользовательского интерфейса).
        IntentFilter intentFilter1 = new IntentFilter(FIRST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(broadcastReceiver, intentFilter1);

        flagHashMap = true;
        // broadcastReceiverHashMap получает команду пора перебирать массив, т.к. он заполнился
        broadcastReceiverHashMap = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String startSearchNameValute = intent.getStringExtra("KeySearch");
                if(flagHashMap) // флаг чтобы broadcastReceiverHashMap заходил в условие один раз
                {
                    if(startSearchNameValute.equals("search"))
                    {
                        tvUpdate.setText("Данные за " + ((MainActivity)getActivity()).stringDate.substring(0,10));
                        // 2022-03-12T11:30:00+03:00

                        for (int i = 0; i < ((MainActivity)getActivity()).hashMap.size(); i++) {
                            // если содержимое tvCharNameLeft и tvCharNameRight (например RUR и AUD) то переберем массив
                            // и получим из него значение этих валют и записываем в tempRateValuteLeft и tempRateValuteRight
                            if(tvCharNameLeft.getText().toString()
                                    .equals(((MainActivity)getActivity()).hashMap.get(i).keyValuteName.toString()))
                            {
                                tempRateValuteLeft = ((MainActivity)getActivity()).hashMap.get(i).value;
                                Log.e("aaaaaa","-aaaleft-" + ((MainActivity)getActivity()).hashMap.get(i).value);
                            }
                            if(tvCharNameRight.getText().toString()
                                    .equals(((MainActivity)getActivity()).hashMap.get(i).keyValuteName.toString()))
                            {
                                tempRateValuteRight = ((MainActivity)getActivity()).hashMap.get(i).value;
                                Log.e("aaaaaa","-aaaright-" + ((MainActivity)getActivity()).hashMap.get(i).value);

                            }
                        }

                        // запускю метод после raschet() с парметрами после нового перебора hashmap для обновления
                        // tempRateValuteRight и tempRateValuteLeft
                        if(temp.equals("clickLeft") && edPriceLeft.getText().toString().length() != 0)
                        {
                            raschet(edPriceLeft.getText().toString(), "textWatcherLeft");
                        }
                        if(temp.equals("clickRight") && edPriceRight.getText().toString().length() != 0)
                        {
                            raschet(edPriceRight.getText().toString(), "textWatcherRight");
                        }
                    }
                    flagHashMap = false;  // флаг чтобы broadcastReceiverHashMap заходил в условие один раз
                }


            }
        };
        // создаем фильтр для BroadcastReceiver
        // Он будет запускаться в основном потоке активности (также как поток пользовательского интерфейса).
        IntentFilter intentFilter2 = new IntentFilter(SECOND_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(broadcastReceiverHashMap, intentFilter2);

        calculation();

        edPriceLeft.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
              if(b)
                {
                    edPriceLeft.addTextChangedListener(textWatcherLeft); // отключаю
                    edPriceRight.removeTextChangedListener(textWatcherRight); // отключаю
                }
            }
        });
        edPriceRight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    edPriceRight.addTextChangedListener(textWatcherRight); // отключаю
                    edPriceLeft.removeTextChangedListener(textWatcherLeft); // отключаю
                }
            }
        });

        return frag;
    }


    public void calculation()
    {
        textWatcherLeft = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // Log.e("TextWatcher", "LEFT beforeTextChange");
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // Log.e("TextWatcher", "LEFT onTextChanged");
            }
            @Override
            public void afterTextChanged(Editable editable) {
                        Log.e("TextWatcher", "LEFT afterTextChanged editable =" + editable.toString());
                if(editable.toString().length() == 0)
                {
                    edPriceRight.setText("");
                }
                else {
                        raschet(editable.toString().replace(",","."), "textWatcherLeft");

                 }
            }
        };

        textWatcherRight = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                   // Log.e("TextWatcher", "RIGHT beforeTextChange");
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //Log.e("TextWatcher", "RIGHT onTextChanged");
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    Log.e("TextWatcher", "RIGHT afterTextChanged editable =" + editable );
                    if(editable.toString().length() == 0)
                    {
                        edPriceLeft.setText("");
                    }
                    else {
                        raschet(editable.toString().replace(",","."), "textWatcherRight");
                    }
                }
            };

    }


    public void raschet(String enterNumbersStr, String textWatcher)
    {


        Log.e("TextWatcher", " edPriceLeft.setText raschet enterNumbersStr=" + enterNumbersStr
                + "textWatcher =" + textWatcher);
        // тут храниться курс валюты слева и справа
        // tempRateValuteLeft
        // tempRateValuteRight

        double enterNumbersD = Double.parseDouble(String.valueOf(enterNumbersStr));
        // ТУТ ТОЛЬКО ДЛЯ RUR справа или слева ---------------------------------------------------------------------
        // ЕСЛИ СЛЕВА RUR А СПРАВА ЛЮБАЯ ДРУГАЯ ВАЛЮТА
        // пользователь сейчас вводит число слева, значит мы должны отобразить результат справа
        if((textWatcher.equals("textWatcherLeft") && tvCharNameLeft.getText().toString().equals("RUR")))
        {
            double resForRight = enterNumbersD / tempRateValuteRight;
            DecimalFormat f = new DecimalFormat("0.000");
            edPriceRight.setText("" +f.format(resForRight).replace(",","."));
            Log.e("TextWatcher", " ЗАШЕЛ 1 edPriceRight.setText =" + f.format(resForRight).replace(",","."));
        }
        // пользователь сейчас вводит число справа, значит мы должны отобразить результат слева
        if(textWatcher.equals("textWatcherRight") && tvCharNameLeft.getText().toString().equals("RUR"))
        {
            double resForLeft = enterNumbersD * tempRateValuteRight;
            DecimalFormat f = new DecimalFormat("0.000");
            edPriceLeft.setText("" +f.format(resForLeft).replace(",","."));
            Log.e("TextWatcher", " ЗАШЕЛ 2 edPriceLeft.setText =" + f.format(resForLeft).replace(",","."));
        }
        // // ЕСЛИ СПРАВА RUR А СЛЕВА ЛЮБАЯ ДРУГАЯ ВАЛЮТА
        // пользователь сейчас вводит число слева, значит мы должны отобразить результат справа
        if((textWatcher.equals("textWatcherLeft") && tvCharNameRight.getText().toString().equals("RUR")))
        {
            double resForRight = enterNumbersD * tempRateValuteLeft;
            DecimalFormat f = new DecimalFormat("0.000");
            edPriceRight.setText("" +f.format(resForRight).replace(",","."));
            Log.e("TextWatcher", " ЗАШЕЛ 3 edPriceRight.setText =" + f.format(resForRight).replace(",","."));
        }
        // пользователь сейчас вводит число справа, значит мы должны отобразить результат слева
        if(textWatcher.equals("textWatcherRight") && tvCharNameRight.getText().toString().equals("RUR"))
        {
            double resForLeft = enterNumbersD / tempRateValuteLeft;
            DecimalFormat f = new DecimalFormat("0.000");
            edPriceLeft.setText("" +f.format(resForLeft).replace(",","."));
            Log.e("TextWatcher", " ЗАШЕЛ 4 edPriceLeft.setText =" + f.format(resForLeft).replace(",","."));
        }
        // ТУТ ТОЛЬКО ДЛЯ ДРУГИХ ВАЛЮТ справа или слева КРОМЕ RUR ---------------------------------------------------------------------
        // пользователь сейчас вводит число слева, значит мы должны отобразить результат справа
        // при это валюты RUR нет справа и слева
        if((textWatcher.equals("textWatcherLeft") && !tvCharNameLeft.getText().toString().equals("RUR"))
        && (textWatcher.equals("textWatcherLeft") && !tvCharNameRight.getText().toString().equals("RUR")))
        {
            double resForRight = (tempRateValuteRight / tempRateValuteLeft) * enterNumbersD;
            DecimalFormat f = new DecimalFormat("0.000");
            edPriceRight.setText("" +f.format(resForRight).replace(",","."));
            Log.e("TextWatcher", " ЗАШЕЛ 5 edPriceRight.setText =" + f.format(resForRight).replace(",","."));
        }
        // пользователь сейчас вводит число справа, значит мы должны отобразить результат слева
        // при это валюты RUR нет справа и слева
        if(textWatcher.equals("textWatcherRight") && !tvCharNameLeft.getText().toString().equals("RUR")
        && (textWatcher.equals("textWatcherRight") && !tvCharNameRight.getText().toString().equals("RUR")))
        {
            double resForLeft = (tempRateValuteLeft / tempRateValuteRight) * enterNumbersD;
            DecimalFormat f = new DecimalFormat("0.000");
            edPriceLeft.setText("" +f.format(resForLeft).replace(",","."));
            Log.e("TextWatcher", " ЗАШЕЛ 6 edPriceLeft.setText =" + f.format(resForLeft).replace(",","."));
        }
        ((MainActivity)getActivity()).saveSettingString("sharedPrefPriceLeft" , edPriceLeft.getText().toString());
        ((MainActivity)getActivity()).saveSettingString("sharedPrefPriceRight" , edPriceRight.getText().toString());
    }

    @Override
    public void onPause() {
        handler.removeCallbacksAndMessages(null);// отписываемся от handler, который был для progressBar
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        edPriceLeft.removeTextChangedListener(textWatcherLeft);
        edPriceRight.removeTextChangedListener(textWatcherRight);
        //ll_edit_currency_right.setOnClickListener(null);
        super.onDestroyView();
    }

    // при учнитожении приложения закрывается BroadcastReceiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("life", "FragmentCalc onDestroy");
        // дерегистрируем (выключаем) BroadcastReceiver
        getActivity().unregisterReceiver(broadcastReceiver);
    }

}