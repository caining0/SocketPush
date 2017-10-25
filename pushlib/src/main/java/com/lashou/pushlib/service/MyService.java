package com.lashou.pushlib.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.lashou.pushlib.Push;
import com.lashou.pushlib.config.Configs;
import com.lashou.pushlib.utils.SharedPrefeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.lashou.pushlib.Push.NOTIFICATION;
import static com.lashou.pushlib.Push.getActivity;


/**
 * @author Oslanka
 * @date 邮箱 Oslanka@163.com
 */
public class MyService extends Service {

    private static final String TAG = "MyService";
    public static final String EXTRA_APPKEY = "appkey";
    private static final String serverIp = Configs.serverIp;
    private static final int serverPort = Configs.serverPort;

    private MyBinder mBinder = new MyBinder();
    private MessageClient client;
    private String appkey;
//    private static MessageReceiver mPushMessageReceiver;
//    private static boolean onCreate = false;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            appkey = intent.getStringExtra(EXTRA_APPKEY);
        }
        if (!TextUtils.isEmpty(appkey)) {
//            onCreate = true;
            Log.i("info", "=====>start");
            client = MessageClient.getInstance(MyService.this, mBinder, serverIp, serverPort, appkey);
            try {
                new Thread() {
                    @Override
                    public void run() {
                        client.connect();
                    }
                }.start();
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        onCreate = false;
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        if (client != null) {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        client.disconnect();
                        client=null;
                    }
                }.start();
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
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
        public void delayStartPushServer() {

            if (client == null) {
                client = MessageClient.getInstance(MyService.this, this, serverIp, serverPort, appkey);
            }
            client.disconnect();
            new Thread() {
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

    private static void sendNotification(Context context, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //设置点击通知跳转的activity
        Intent resultIntent = new Intent(context, NotificationClickReceiver.class);
        resultIntent.setAction(NotificationClickReceiver.ACTION_NOTIFICATION_OPENED);
        resultIntent.putExtra(NotificationClickReceiver.NOTIFICATION_MESSAGE, message);
        long id = SharedPrefeUtils.getLong(context, "notificationId", 0);
        if (id > 10000) {
            id = 0;
        }
        SharedPrefeUtils.saveLong(context, "notificationId", ++id);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, (int) id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int icon = Push.getNotificationIcon() == 0 ? android.R.mipmap.sym_def_app_icon : Push.getNotificationIcon();
        Notification notification =
                new NotificationCompat.Builder(context).setSmallIcon(icon)
                        .setLargeIcon(drawableToBitmap(context.getResources().getDrawable(icon)))
                        .setContentTitle(message)
                        .setContentText(message)
                        .setContentIntent(resultPendingIntent).build();


//        mBuilder.setContentIntent(resultPendingIntent);


//        final Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
//这通知的其他属性，比如：声音和振动
        notification.defaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;


        mNotificationManager.notify((int) id, notification);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {


        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public static Handler getMyHandler() {
        if (myHandler == null) {
            myHandler = new Handler(Looper.getMainLooper()) {
                private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            if (msg.obj != null) {
                                String message = (String) msg.obj;
                                if (Push.getMessageListener() != null) {
                                    Push.getMessageListener().onMessageReceive(message);
                                }
                                try {
                                    JSONObject jsonObject = new JSONObject(message);
                                    if (NOTIFICATION.equals(jsonObject.optString("action"))) {

                                        sendNotification(getActivity(), message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            break;
                        case 1:
                            long l = (long) msg.obj;
                            if (Push.getMessageListener() != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(l);
                                Push.getMessageListener().onHeartBeat(formatDate.format(calendar.getTime()));
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        return myHandler;
    }

    private static Handler myHandler;
}