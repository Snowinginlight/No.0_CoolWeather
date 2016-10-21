package com.coolweather.app.util;

import android.text.TextUtils;
import android.util.Log;
import com.coolweather.app.model.Area;
import com.coolweather.app.model.CoolWeatherDB;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 无知 on 2016/10/19.
 */

public class Utility {

    public synchronized static boolean handleAreaResponse(CoolWeatherDB coolWeatherDB, String response, int parentId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject1 = new JSONObject(response);//获得原始json对象
                JSONObject jsonObject2 = jsonObject1.getJSONObject("showapi_res_body");//获得需要的json对象
                JSONArray jsonArray = jsonObject2.getJSONArray("data");//获得data里面的数据
                for (int i = 0; i < jsonArray.length(); i++) {
                    Area area = new Area();
                    JSONObject jsonOb = jsonArray.getJSONObject(i);
                    Log.d("Utility",jsonOb.toString());
                    if (jsonOb.getInt("parentId") == parentId ) {
                        area.setId(jsonOb.getInt("id"));
                        area.setParentId(jsonOb.getInt("parentId"));
                        area.setLevel(jsonOb.getInt("level"));
                        area.setAreaName(jsonOb.getString("areaName"));
                        if(parentId == 0) {
                            area.setProvinceName(jsonOb.getString("areaName"));
                            Log.d("MyTest","Utility province is "+area.getProvinceName()+parentId);
                        }else{
                            area.setProvinceName(jsonOb.getString("provinceName"));
                            Log.d("MyTest","Utility province is "+area.getProvinceName()+parentId);
                        }
                        coolWeatherDB.saveArea(area);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
