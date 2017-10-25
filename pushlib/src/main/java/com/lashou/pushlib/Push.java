package com.lashou.pushlib;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lashou.pushlib.config.Configs;
import com.lashou.pushlib.http.HttpUtils;
import com.lashou.pushlib.lib.proguard.DaemonClient;
import com.lashou.pushlib.lib.proguard.DaemonConfigurations;
import com.lashou.pushlib.service.MessageListener;
import com.lashou.pushlib.service.MyService;
import com.lashou.pushlib.service.OnNotificationOpenListener;
import com.lashou.pushlib.service.Receiver1;
import com.lashou.pushlib.service.Receiver2;
import com.lashou.pushlib.service.Service1;
import com.lashou.pushlib.service.Service2;
import com.lashou.pushlib.utils.SharedPrefeUtils;

/**
 * Created by cnn on 17-9-28.
 */

public class Push {
    private static String appkey = null;
    private static Context activity;
    private static MessageListener messageListener;
    private static String MESSAGE = "message";
    public static String NOTIFICATION = "notification";
    private static boolean onProguard = false;
    private static boolean isOn = false;
    private static int notificationIcon;
    private static OnNotificationOpenListener onNotificationOpenListener;


    public static MessageListener getMessageListener() {
        return messageListener;
    }

    public static int getNotificationIcon() {
        return notificationIcon;
    }

    public static OnNotificationOpenListener getOnNotificationOpenListener() {
        return onNotificationOpenListener;
    }

    /**
     * 启动application 调用，确保key获取
     *
     * @param context
     */
    public static void init(final Context context, String apiId) {
        SharedPrefeUtils.saveSettings(context, "apiId", apiId);
        activity = context;
        if (TextUtils.isEmpty(appkey)) {
            HttpUtils.getInstance(new HttpUtils.CallBackHttp() {
                @Override
                public void finish(String msg) {
                    Log.i("push_key----->", msg);
                    appkey = msg;
                    if (messageListener != null && !TextUtils.isEmpty(appkey)) {
                        messageListener.onGetID(appkey);
                    }
                    beginService(context, appkey);
                }

                @Override
                public void error(String msg) {
                    Log.i("push----->", msg);
                }
            }, context, apiId);
        }else {
            if (!isOn){
                beginService(context,appkey);
            }
        }
    }

    public static void setDebug(boolean debug) {
        HttpUtils.setUrl(debug);
        Configs.setUrl(debug);
    }


    public static void setNotification(int icon, OnNotificationOpenListener onNotificationOpenListener) {
        notificationIcon = icon;
        Push.onNotificationOpenListener = onNotificationOpenListener;
    }


    /**
     * 启动Service
     *
     * @param context
     */
    public static void start(Context context, String apiId, MessageListener messageListener) {
        activity = context;
        Push.messageListener = messageListener;
        init(context, apiId);
    }

    private static void beginService(Context context, String appkey) {
        if (TextUtils.isEmpty(appkey)) {
            return;
        }
        isOn=true;
        context.startService(new Intent(context, MyService.class).putExtra(MyService.EXTRA_APPKEY, appkey));
    }

    /**
     * 启动Service
     *
     */
    public static void stop() {
        if (!isOnProguard()){
            isOn=false;
            getActivity().stopService(new Intent(getActivity(), MyService.class));
        }
    }


    public static Context getActivity() {
        return activity;
    }


    public static boolean isOnProguard() {
        return onProguard;
    }

    public static void proguard(Context baseContext) {
        onProguard = true;

        DaemonClient daemonClient = new DaemonClient(getDaemonConfigurations());
        daemonClient.onAttachBaseContext(baseContext);
        //you have to start the service once.
        baseContext.startService(new Intent(baseContext, Service1.class));
    }

    public static DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.lashou.pushlib:process1",
                Service1.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.lashou.pushlib:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    static class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
}
