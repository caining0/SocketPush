package com.androidsocket.test;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.lashou.pushlib.Push;
import com.lashou.pushlib.service.OnNotificationOpenListener;

/**
 *
 * @author cnn
 * @date 17-9-29
 */

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Push.init(this,"1234");
        //初始化,如果 不用start 只用通知功能，可只调用init 而不调用start
        Push.setDebug(true);
        //是否使用测试环境 服务器  10.168.31.69
        Push.setNotification(R.mipmap.ic_launcher_round, new OnNotificationOpenListener() {

            @Override
            public void open(Context context, String mes) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        Push.proguard(base);//调用此方法，会启动进程守护，会在程序被杀死的情况下依然连接socket
        super.attachBaseContext(base);
    }
}
