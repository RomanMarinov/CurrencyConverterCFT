package com.dev_marinov.convertercft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    HashMap<Integer, ObjectListValute> hashMap = new HashMap<>();
    Intent intent;
    ArrayList<ObjectListValute> arrayList;
    String stringDate;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide(); // скрыть
        setStatusBar(); // установка черного цвета текста строки состояния
        getData(); // получить данные карсов

        // savedInstanceState то загрузить fragmentCalc
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new FragmentCalc())
                    .commit();
        }
    }

    // метода переходов фрагментов и анимации
    public void flipCard(String string) {
        if(string.equals("backStack"))
        {
            // popBackStack() озвращающает предыдущее состояние стека по этому имени.
            getSupportFragmentManager().popBackStack();
        }

        if(string.equals("goFragmentList"))
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.animator.card_flip_right_enter,
                            R.animator.card_flip_right_exit,
                            R.animator.card_flip_left_enter,
                            R.animator.card_flip_left_exit)
                    .replace(R.id.container, new FragmentList())
                    .addToBackStack(null)
                    .commit();
        }

        if(string.equals("gofragmentAlarm"))
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.animator.card_flip_right_enter,
                            R.animator.card_flip_right_exit,
                            R.animator.card_flip_left_enter,
                            R.animator.card_flip_left_exit)
                    .replace(R.id.container, new FragmentAlarm())
                    .addToBackStack(null)
                    .commit();
        }
    }

    // установка черного цвета текста строки состояния
    public void setStatusBar()
    {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // установка фона экрана
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.color.white);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS Флаг, указывающий, что это Окно отвечает за отрисовку фона для системных полос.
        // Если установлено, системные панели отображаются с прозрачным фоном, а соответствующие области в этом окне заполняются цветами,
        // указанными в Window#getStatusBarColor()и Window#getNavigationBarColor().
        window.setStatusBarColor(getResources().getColor(android.R.color.white));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
        window.setBackgroundDrawable(background);
    }

    public void getData()
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get("https://www.cbr-xml-daily.ru/daily_json.js", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("3333","-onFailure-");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Log.e("3333","-onSuccess-responseString-" + responseString);

                // добавить в hashMap рубли
                hashMap.put(0, new ObjectListValute("RUR", "Российский рубль", 1));

                JSONObject jsonObjectResp = null;
                try {
                    jsonObjectResp = new JSONObject(responseString);
                    stringDate = jsonObjectResp.getString("Date");
                    Log.e("3333","-stringDate-" + stringDate.toString());

                    JSONObject jsonObjectValute = jsonObjectResp.getJSONObject("Valute");
                    Iterator<String> key = jsonObjectValute.keys();

                    int count = 0;
                    while (key.hasNext())
                    {
                        String keys = key.next();
                        String name = jsonObjectValute.getJSONObject(keys).getString("Name");
                        double value = jsonObjectValute.getJSONObject(keys).getDouble("Value");

                        Log.e("test","keys=" + keys + " Name=" + name + " value="+value);
                        count++;
                        hashMap.put(count, new ObjectListValute(keys, name, value));
                    }

                    // работа с кампаратором для алфавитной записи рублей
                    arrayList = new ArrayList<ObjectListValute>(hashMap.values());
                    Collections.sort(arrayList, new Sorted());
                    // проверка записоль так как надо и нет
                    for (int i = 0; i < arrayList.size(); i++) {
                        Log.e("showme","-nameValute-" + arrayList.get(i).nameValute);
                    }

                    // intent для того чтобы только после заполнения hashmap
                    // FragmentCalc получил сообщение работать в hashmap и получать из него данные
                    intent = new Intent(FragmentCalc.SECOND_ACTION);
                    intent.putExtra("KeySearch", "search");
                    sendBroadcast(intent);

                } catch (JSONException e) {
                    Log.e("3333","-try catch-" + e);
                }
            }
        });
    }

    // считывает файл
    public String loadSettingString(String key, String default_value) {
        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        return sharedPreferences.getString(key, default_value);
    }
    // сохраняет в файл
    public void saveSettingString(String key, String value) {
        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit(); // edit() - редактирование файлов
        ed.putString(key, value); // добавляем ключ и его значение
        if (ed.commit()) // сохранить файл
        {
            //успешно записано данные в файл
        } else {
            //ошибка при записи
            Toast.makeText(this, "Write error", Toast.LENGTH_SHORT).show();
        }
    }

}