package com.lashou.pushlib.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lashou.pushlib.utils.PushMessageUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageReceiver extends BroadcastReceiver {
    private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(PushMessageUtil.ACITION_MESSAGE.equals(action)){

            boolean error = intent.getBooleanExtra(PushMessageUtil.EXTRA_MESSAGE_ERROR, false);
            if(error){
                if(mOnMessageReceiveListener != null){
                    mOnMessageReceiveListener.onError(formatDate.format(Calendar.getInstance().getTime()));
                }
                abortBroadcast();
                return;
            }

            long l = intent.getLongExtra(PushMessageUtil.EXTRA_MESSAGE_HEARTBEAT, 0);
            if(l>0){
                if(mOnMessageReceiveListener != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(l);
                    mOnMessageReceiveListener.onHeartBeat(formatDate.format(calendar.getTime()));
                }
            }
            String stringExtra = intent.getStringExtra(PushMessageUtil.EXTRA_MESSAGE);
            if(mOnMessageReceiveListener != null && !TextUtils.isEmpty(stringExtra)){
                mOnMessageReceiveListener.onPushSrcMessage("receive message");
                mOnMessageReceiveListener.onMessageReceive(stringExtra);
            }
//            MessageInfo messageInfo = parseMessage(stringExtra);

            /*if(!TextUtils.isEmpty(stringExtra) &&  mOnMessageReceiveListener != null){
//                PushMessageUtil.addMessage(messageInfo);
                mOnMessageReceiveListener.onPushSrcMessage(null);
            }*/
            //禁止往下传播
            abortBroadcast();
        }

    }

   /* private MessageInfo parseMessage(String stringExtra) {
        if(TextUtils.isEmpty(stringExtra)){
            return null;
        }
        MessageInfo messageInfo = null;
        try {
            messageInfo = gson.fromJson(stringExtra, MessageInfo.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Logger.d(e.getMessage()+",,,str="+stringExtra);
        }catch (Exception e) {
            e.printStackTrace();
            Logger.d(e.getMessage()+",,,str="+stringExtra);
        }
        return messageInfo;
    }*/

    public void setmOnMessageReceiveListener(OnMessageReceiveListener mOnMessageReceiveListener) {
        this.mOnMessageReceiveListener = mOnMessageReceiveListener;
    }

    private OnMessageReceiveListener mOnMessageReceiveListener;


//    Intent i = new Intent("custom.action.mybroadcast");
//    sendOrderedBroadcast(i, null);
//    sendBroadcast(i);
}
