package com.lashou.pushlib.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lashou.pushlib.Push;

/**
 * DO NOT do anything in this Receiver!<br/>
 *
 * @author Oslanka
 * @date 12/24/15
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    public static final String ACTION_NOTIFICATION_OPENED = "open";
    public static final String NOTIFICATION_MESSAGE = "message";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_NOTIFICATION_OPENED:
                Log.i("info", "--------->点击了通知");
                if (Push.getOnNotificationOpenListener() != null) {
                    Push.getOnNotificationOpenListener().open(context,intent.getStringExtra(NOTIFICATION_MESSAGE));
                }
                break;
            default:
                break;
        }

    }
}
