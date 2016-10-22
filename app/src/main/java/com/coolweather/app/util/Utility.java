package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.coolweather.app.model.Area;
import com.coolweather.app.model.CoolWeatherDB;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 无知 on 2016/10/19.
 */

public class Utility {

    public synchronized static int handleAreaResponse(CoolWeatherDB coolWeatherDB, String response, int parentId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                if(parentId > 40000){
                return -1;
            }
                JSONObject jsonObject1 = new JSONObject(response);//获得原始json对象
                JSONObject jsonObject2 = jsonObject1.getJSONObject("showapi_res_body");
                if(jsonObject2.getInt("ret_code") == -1){
                    return -1;
                }
                JSONArray jsonArray = jsonObject2.getJSONArray("data");//获得data里面的数据
                for (int i = 0; i < jsonArray.length(); i++) {
                    Area area = new Area();
                    JSONObject jsonOb = jsonArray.getJSONObject(i);
                    if(!jsonOb.isNull("parentId")) {
                        if (jsonOb.getInt("parentId") == parentId) {
                            area.setId(jsonOb.getInt("id"));
                            area.setParentId(jsonOb.getInt("parentId"));
                            area.setLevel(jsonOb.getInt("level"));
                            area.setAreaName(jsonOb.getString("areaName"));
                            if (parentId == 0) {
                                area.setProvinceName(jsonOb.getString("areaName"));
                            } else {
                                area.setProvinceName(jsonOb.getString("provinceName"));
                            }
                            coolWeatherDB.saveArea(area);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    public static int handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject body = jsonObject.getJSONObject("showapi_res_body");
            if(body.getInt("ret_code") == -1){
                return -1;
            }
            JSONObject cityInfo = body.getJSONObject("cityInfo");
            JSONObject f1 = body.getJSONObject("f1");
            JSONObject now = body.getJSONObject("now");
            String cityName = cityInfo.getString("c3");
            String cityId = cityInfo.getString("c1");
            String temp1 = f1.getString("day_air_temperature");
            String temp2 = f1.getString("night_air_temperature");
            String weatherDesp = now.getString("weather");
            String publishTime = now.getString("temperature_time");
            saveWeatherInfo(context,cityName,cityId,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 1;
    }
//将服务器返回天气信息存储到SharedPreferences
    private static void saveWeatherInfo(Context context, String cityName, String cityId, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_Name", cityName);
        editor.putString("city_Id", cityId);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        Log.d("Test",new Date().toString());
        editor.commit();
    }
}
