package com.coolweather.app.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.Area;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 无知 on 2016/10/19.
 */

public class ChooseAreaActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private  List<String> dataList = new ArrayList<String>();
    private List<Area> areaList;
    private Area selectedArea;
    private int currentLevel;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        getSupportActionBar().hide();
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                selectedArea = areaList.get(index);
                queryArea(selectedArea.getProvinceName(), selectedArea.getLevel() + 1, selectedArea.getId());
            }
        });
        queryArea(null,1,0);
    }

    private void queryArea(String areaName,int level,int parentId) {
        currentLevel = level;
        areaList = coolWeatherDB.loadAreas(parentId);
        if(areaList.size() > 0){
            dataList.clear();
            for(Area area:areaList){
                dataList.add(area.getAreaName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            if(level == 1){
                titleText.setText("中国");
            }
            else{titleText.setText(selectedArea.getAreaName());}
        }else {
            queryFromServer(areaName,currentLevel,parentId);
        }
    }

    private void queryFromServer(final String Name, final int level,final int parentId) {
        Log.d("Utility","Name is "+Name+"\n level is"+level);
        String address;
        final String appid = "25945";
        final String secret = "eb3602222ece406d958501bfe52d3596";
        if(TextUtils.isEmpty(Name)){
            address = "http://route.showapi.com/101-39?showapi_appid="+appid+"&showapi_timestamp=20151214132239&showapi_sign="+secret+"&level="+level+"&areaName=&";
        }else {
            address = "http://route.showapi.com/101-39?showapi_appid="+appid+"&showapi_timestamp=20151214132239&showapi_sign="+secret+"&level="+level+"&areaName="+Name+"&";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                    result = Utility.handleAreaResponse(coolWeatherDB, response,parentId);
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            queryArea(Name,level,parentId);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);//无法取消
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == 3){
            queryArea(selectedArea.getProvinceName(),2,selectedArea.getParentId());
        }else if(currentLevel == 2){
            queryArea(null,1,0);
        }else{
            finish();
        }
    }
}
