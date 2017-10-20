package com.lashou.pushlib.service;

/**
 * Created by cnn on 2017/9/29.
 */
 public interface MessageListener {
   void onGetID(String id);

   /**
     * 接收到的message
     * @param message
     */
    void onMessageReceive(String message);

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
