package com.lashou.pushlib.utils;

/**
 * Created by Administrator on 2017/6/14.
 */
public class PushMessageUtil {

    public static final String ACITION_MESSAGE = "com.lashou.padorder.tv.queuenumber.message";
    public static final String EXTRA_MESSAGE = "queuenumber.message";
    public static final String EXTRA_MESSAGE_ERROR = "queuenumber.message.error";
    public static final String EXTRA_MESSAGE_HEARTBEAT = "queuenumber.message.heartbeat";

    public static final int MESSAGE_TIME = 1002;
    public static final long MESSAGE_TIME_DELAY = 5 * 1000;
    public static final int MESSAGE_NEW_NUMBER = 1001;
    public static final long MESSAGE_NEW_NUMBER_DELAY = 10 * 1000;  //消息在左侧屏幕最短显示时间
    private static final long NEW_NUMBER_SHOW_TIME = 30 * 1000;     //消息在左侧屏幕最长显示时间
    private static final long NO_NEW_NUMBER = 20 * 60 * 1000;       //无新消息叫号页面显示时间
    private static final long APP_EXIT_SAVE_EXPTION = 30 * 60 * 1000;//应用退出取餐号列表保存时间
}
