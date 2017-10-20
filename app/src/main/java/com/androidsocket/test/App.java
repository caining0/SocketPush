package com.androidsocket.test;

import android.app.Application;
import android.content.Context;

import com.lashou.pushlib.Push;

/**
 * Created by cnn on 17-9-29.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Push.init(this,"1234");//初始化,如果 不用start 只用通知功能，可只调用init 而不调用start
        Push.setDebug(true);//是否使用测试环境 服务器  10.168.31.69
    }

    @Override
    protected void attachBaseContext(Context base) {
        Push.proguard(base);//调用此方法，会启动进程守护，会在程序被杀死的情况下依然连接socket
        super.attachBaseContext(base);
    }
}
