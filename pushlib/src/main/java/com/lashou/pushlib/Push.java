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
import com.lashou.pushlib.service.MessageReceiver;
import com.lashou.pushlib.service.MyService;
import com.lashou.pushlib.service.Receiver1;
import com.lashou.pushlib.service.Receiver2;
import com.lashou.pushlib.service.Service2;

/**
 * Created by cnn on 17-9-28.
 */

public class Push {
    private static String appkey = null;
    private static boolean regester = false;
    private static MessageReceiver mPushMessageReceiver;
    private static Context activity;
    public static MessageListener messageListener;
    private static String MESSAGE = "message";
    public static String NOTIFICATION = "notification";
    private static boolean canStop =true;


    /**
     * 启动application 调用，确保key获取
     *
     * @param context
     */
    public static void init(final Context context, String apiId) {
        activity = context;
        if (TextUtils.isEmpty(appkey))
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
    }

    public static void setDebug(boolean debug) {
        HttpUtils.setUrl(debug);
        Configs.setUrl(debug);
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
        if (TextUtils.isEmpty(appkey)) return;
//        Activity activity = (Activity) context;
        context.startService(new Intent(context, MyService.class).putExtra(MyService.EXTRA_APPKEY, appkey));
//        registeBroadCast();
    }

    /**
     * 启动Service
     *
     * @param context
     */
    public static void stop(Context context) {
        if (canStop) {
            activity = context;
            context.stopService(new Intent(getActivity(), MyService.class));
            disConnect();
        }
    }


   /* *//**
     * 注册广播
     *//*
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
                Logger.i("uid--------->" + appkey + "  " + message);
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
    }*/

    public static Context getActivity() {
        return activity;
    }

    private static void disConnect() {
      /*  HttpUtils.getInstance().cookClose(PadOrderApplication.getCurrentLoginBean().getAuth_token(), appkey).enqueue(new Callback<Object>() {

            @Override
            protected void onSuccess(Object bean) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });*/

        try {
            if (regester) {
                getActivity().unregisterReceiver(mPushMessageReceiver);
                regester = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void proguard(Context baseContext) {
        canStop=false;
        DaemonClient daemonClient = new DaemonClient(getDaemonConfigurations());
        daemonClient.onAttachBaseContext(baseContext);
    }

    public static DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.lashou.pushlib:process1",
                MyService.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.lashou.pushlib:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }
    static class MyDaemonListener implements DaemonConfigurations.DaemonListener{
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
