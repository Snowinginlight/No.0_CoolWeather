package com.coolweather.app.util;

/**
 * Created by 无知 on 2016/10/19.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
