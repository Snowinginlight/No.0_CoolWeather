package com.coolweather.app.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.coolweather.app.db.CoolWeatherOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 无知 on 2016/10/19.
 */

public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION = 1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }//获取CoolWeatherDb实例

    public void saveArea(Area area) {
        if (area != null) {
            ContentValues values = new ContentValues();
            values.put("id", area.getId());
            values.put("parentId", area.getParentId());
            values.put("level", area.getLevel());
            values.put("areaName", area.getAreaName());
            values.put("provinceName", area.getProvinceName());
            db.insert("Area", null, values);
        }
    }

    public List<Area> loadAreas(int parentId) {
        List<Area> list = new ArrayList<Area>();
        Cursor cursor = db.query("Area", null, "parentId = ?", new String[]{String.valueOf(parentId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Area area = new Area();
                area.setId(cursor.getInt(cursor.getColumnIndex("id")));
                area.setLevel(cursor.getInt(cursor.getColumnIndex("level")));
                area.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
                area.setParentId(parentId);
                area.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                list.add(area);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}

