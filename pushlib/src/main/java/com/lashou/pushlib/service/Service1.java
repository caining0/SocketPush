package com.lashou.pushlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.lashou.pushlib.http.HttpUtils;
import com.lashou.pushlib.utils.SharedPrefeUtils;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..
//        if (!Push.isOnProguard()&& !TextUtils.isEmpty(key)) {
        final String  apiId= SharedPrefeUtils.getString(getApplicationContext(),"apiId","");
        Toast.makeText(this, ""+apiId, Toast.LENGTH_SHORT).show();
        HttpUtils.getInstance(new HttpUtils.CallBackHttp() {
            @Override
            public void finish(String msg) {
                startService(new Intent(getApplicationContext(), MyService.class).putExtra(MyService.EXTRA_APPKEY, apiId));
            }

            @Override
            public void error(String msg) {

            }
        },getApplicationContext(),apiId);
//        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}