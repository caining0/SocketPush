package com.lashou.pushlib.service;


import android.content.Context;

/**
 * @author Administrator
 * @date
 */
public interface OnNotificationOpenListener  {
    /**
     * 打开指定activity
     * @param mes
     */
    public void open(Context context,String mes);
} 