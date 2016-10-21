package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 无知 on 2016/10/19.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_AREA = "create table Area("
                                                  +"id integer,"
                                                  +"parentId integer,"
                                                  +"level integer,"
                                                  +"areaName text,"
                                                  +"provinceName text)";//建表语句

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_AREA);//建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
