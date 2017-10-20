package com.lashou.pushlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 保存用户设置
 *
 * @author caining
 */
public class SharedPrefeUtils {
    private static final String PREFERENES_SETTINGS = "push_lib";

    /**
     * 保存用户设置
     *
     * @param key,flag
     */
    public static void saveSettings(Context context, String key, int flag) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        Editor editor = settings.edit();
        editor.putInt(key, flag);
        editor.commit();
    }

    public static void saveSettings(Context context, String key, String flag) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        Editor editor = settings.edit();
        editor.putString(key, flag);
        editor.commit();
    }

    public static void saveSettings(Context context, String key[], String flag[]) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        Editor editor = settings.edit();
        for (int i = 0; i < flag.length; i++) {
            editor.putString(key[i], flag[i]);
        }
        editor.commit();
    }

    public static void saveSettings(Context context, String key, boolean flag) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        Editor editor = settings.edit();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 读取用户设置
     *
     * @param context , key
     */
    public static String getSettings(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getString(key, "");
    }

    public static int getSettingsInt(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getInt(key, 0);
    }

    public static boolean getSettings(Context context, String key, boolean def) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getBoolean(key, def);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getString(key, defaultValue);
    }

    public static boolean getBooelan(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getBoolean(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getInt(key, defValue);
    }

    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getFloat(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENES_SETTINGS, Context.MODE_APPEND);
        return settings.getLong(key, defValue);
    }
}