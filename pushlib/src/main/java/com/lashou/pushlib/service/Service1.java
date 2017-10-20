package com.lashou.pushlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service {
    public static String key = "";

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..
        if (!MyService.onStart&& !TextUtils.isEmpty(key)) {
            startService(new Intent(getApplicationContext(), MyService.class).putExtra(MyService.EXTRA_APPKEY, key));
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}