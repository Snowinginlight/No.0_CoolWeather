package com.coolweather.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * Created by 无知 on 2016/10/21.
 */

public class WeatherActivity extends AppCompatActivity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;//用于显示城市名
    private TextView publishText;//用于显示发布时间
    private TextView weatherDespText;//用于显示天气描述信息
    private TextView temp1Text;//用于显示日间气温
    private TextView temp2Text;//用于显示夜间气温
    private TextView currentDatetext;//用于显示当前日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        getSupportActionBar().hide();
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDatetext = (TextView) findViewById(R.id.current_date);
        /*
        *以下内容需大量修改
         */
        String areaName = getIntent().getStringExtra("areaName");
        if (!TextUtils.isEmpty(areaName)) {
            //有地区名字就去查天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.VISIBLE);//该布局包括了除cityNameText之外的其他文本布局
            cityNameText.setVisibility(View.VISIBLE);
            queryWeatherCode(areaName);
        } else {
            //没有县级号时就直接显示本地天气
            showWeather();
        }
    }


    private void queryWeatherCode(String areaName) {
        final String appid = "25945";
        final String secret = "eb3602222ece406d958501bfe52d3596";
        String address = "http://route.showapi.com/9-2?showapi_appid="+appid+"&showapi_timestamp=20151214132239&showapi_sign="+secret+"&areaid=&area="+areaName+"&needMoreDay=0&needIndex=0&needHourData=0&need3HourForcast=0&needAlarm=0";
        queryFromServer(address, "areaName");
    }

    /*private void queryWeather(String weatherCode) {
        final String appid = "25945";
        final String secret = "eb3602222ece406d958501bfe52d3596";
        String address = "http://route.showapi.com/9-2?showapi_appid=" + appid + "&showapi_timestamp=20151214132239&" +
                "showapi_sign=" + secret + "&areaid=" + weatherCode + "&area=" +
                "&needMoreDay=0&needIndex=0&needHourData=0&need3HourForcast=0&needAlarm=0&";
        queryFromServer(address,"weatherCode");
    }*/

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                    int result = 0;
                    result = Utility.handleWeatherResponse(WeatherActivity.this, response);
                if(result == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }else if(result == -1){
                    Toast.makeText(WeatherActivity.this,"数据库未录入该地区天气数据，请重新选择",Toast.LENGTH_SHORT).show();
                }else{return;}
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
     *从SharedPreferences文件中读取存储天气的信息，并显示在界面上
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_Name", ""));
        Log.d("Weather","showWeather is "+prefs.getString("city_Name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDatetext.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

}

