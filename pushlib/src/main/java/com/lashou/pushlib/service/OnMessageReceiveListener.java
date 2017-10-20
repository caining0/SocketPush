package com.lashou.pushlib.service;

/**
 * Created by lixinli on 2017/7/31.
 */
 public interface OnMessageReceiveListener {
    /**
     * 接收到的message
     * @param message
     */
    void onMessageReceive(String message);

    /**
     * 测试使用
     * @param message
     */
    void onPushSrcMessage(String message);
    /**
     * 心跳
     * @param beatTime
     */
    void onHeartBeat(String beatTime);

    /**
     * error
     * @param str
     */
    void onError(String str);
}
