package com.lashou.pushlib.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.lashou.pushlib.config.Configs;
import com.lashou.pushlib.utils.Logger;
import com.lashou.pushlib.utils.PushMessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.lashou.pushlib.Push.NOTIFICATION;
import static com.lashou.pushlib.Push.getActivity;
import static com.lashou.pushlib.Push.messageListener;


public class MyService extends Service {

    private static final String TAG = "MyService";
    public static final String EXTRA_APPKEY = "appkey";
    private static final String serverIp = Configs.serverIp;
    private static final int serverPort = Configs.serverPort;

    private MyBinder mBinder = new MyBinder();
    private MessageClient client;
    private String appkey;
    private static MessageReceiver mPushMessageReceiver;
    private static boolean regester = false;
    public static boolean onStart = false;



    @Override  
    public void onCreate() {  
        super.onCreate();
        onStart=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            appkey = intent.getStringExtra(EXTRA_APPKEY);
        }
        if(!TextUtils.isEmpty(appkey)){
            client = new MessageClient(MyService.this,mBinder, serverIp,serverPort,appkey);
            try {
                new Thread(){
                    @Override
                    public void run() {
                        client.connect();
                    }
                }.start();
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
            registeBroadCast();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onStart=false;
        Log.d(TAG, "onDestroy() executed");
        if(client != null){
            client.disconnect();
            getActivity().unregisterReceiver(mPushMessageReceiver);
            regester=false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {

        private int reConnectTime = 10 * 1000;
        /**
         * 异常情况10秒重连
         */
        public void delayStartPushServer(){

            if(client == null){
                client = new MessageClient(MyService.this,this, serverIp,serverPort,appkey);
            }
            client.disconnect();
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(reConnectTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.connect();
                }
            }.start();
        }

    }
    /**
     * 注册广播
     */
    private static void registeBroadCast() {
        if (regester) return;
        regester = true;
        mPushMessageReceiver = new MessageReceiver();
        mPushMessageReceiver.setmOnMessageReceiveListener(new OnMessageReceiveListener() {
            @Override
            public void onMessageReceive(String message) {
                if (messageListener != null)
                    messageListener.onMessageReceive(message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (NOTIFICATION.equals(jsonObject.optString("action"))) {
                        sendNotification(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPushSrcMessage(String message) {
//                Logger.i("uid--------->" + appkey + "  " + message);
//                messageListener.onMessageReceive(message);
            }

            @Override
            public void onHeartBeat(String time) {
                if (messageListener != null)
                    messageListener.onHeartBeat(time);
            }

            @Override
            public void onError(String time) {
                Logger.i("uid=" + "  error  " + time);
                if (messageListener != null)
                    messageListener.onError(time);
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        //设置接收广播的类型
        intentFilter.addAction(PushMessageUtil.ACITION_MESSAGE);
        //调用Context的registerReceiver（）方法进行动态注册
        getActivity().registerReceiver(mPushMessageReceiver, intentFilter);
    }

    private static int NOTIFICATION_ID = 1;

    private static void sendNotification(String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        //设置点击通知跳转的activity

        Notification notification =
                new NotificationCompat.Builder(getActivity()).setSmallIcon(android.R.mipmap.sym_def_app_icon)
                        .setContentTitle(message)
                        .setContentText(message)
                        .build();


//        mBuilder.setContentIntent(resultPendingIntent);


//        final Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
//这通知的其他属性，比如：声音和振动
        notification.defaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;


        mNotificationManager.notify(NOTIFICATION_ID++, notification);
    }
}