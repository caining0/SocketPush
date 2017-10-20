package com.lashou.pushlib.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.lashou.pushlib.utils.SharedPrefeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by cnn on 17-9-28.
 */

public class HttpUtils {
    private static HttpUtils h;
    private static CallBackHttp call;
    private static Context context;
    private static final String PUSH_KEY = "push_key";
    private static String shopId;

    private static String debugUrl = "http://10.168.31.69:8086/getAppKey";
    private static String realUrl = "http://wdpush.lashou.com/appkey/getAppKey";
    private static String baseUrl = debugUrl;

    public static HttpUtils getInstance(CallBackHttp call, Context context, String shopId) {
        if (h == null) {
            HttpUtils.call = call;
            HttpUtils.context = context;
            HttpUtils.shopId = shopId;
            h = new HttpUtils();
            h.getKey();
        }
        return h;
    }

    private void getKey() {
//        @GET("http://wdpush.lashou.com/appkey/getAppKey")
//    @GET("http://10.168.31.69:8086/getAppKey")

//        Call<KeyBean> getAppKey(@Query("Busines") String busines, @Query("Applic") String applic, @Query("Equip") String equip);
        String code = Build.SERIAL;
        if (TextUtils.isEmpty(code)) {
            code = AppInfoUtils.getDeviceId(context);
            if ("unknown".equals(code)) {
                code += "1";
            }
        }

        final HashMap<String, String> map = new HashMap<>();
        String busines = shopId;
        map.put("Busines", busines);
        map.put("Applic", context.getPackageName());
        map.put("Equip", code);
        String pushKey = SharedPrefeUtils.getString(context, PUSH_KEY + busines + context.getPackageName() + code, "");
        if (!TextUtils.isEmpty(pushKey)) {
            call.finish(pushKey);
        } else {
            get(baseUrl + "?Busines=" + busines + "&Applic=" + context.getPackageName() + "&Equip=" + code + "", busines, context.getPackageName(), code);
        }

    }

    /**
     * 发送get请求
     *
     * @param url
     * @return
     */
    public void get(final String url, String busines, String applic, String equip) {

        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader br = null;
                InputStreamReader isr = null;
                URLConnection conn;
                try {
                    URL geturl = new URL(url);
                    conn = geturl.openConnection();//创建连接
                    conn.connect();//get连接
                    isr = new InputStreamReader(conn.getInputStream());
                    br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);//获取输入流数据
                    }
//                    System.out.println(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    call.error(e.getMessage());
                } finally {//执行流的关闭
                    if (br != null) {
                        try {
                            if (br != null) {
                                br.close();
                            }
                            if (isr != null) {
                                isr.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();//异步获取返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(s)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObject.optInt("ret") == 0) {
                String key = jsonObject.optString("appkey");
                call.finish(key);
                SharedPrefeUtils.saveSettings(context, PUSH_KEY + busines + applic + equip, key);
            } else {
                call.finish("error,ret code is not 0");
            }

        } else {
            call.finish("error,key is null");
        }
//        return s;
    }

    public static void setUrl(boolean debug) {
        if (debug) {
            baseUrl = debugUrl;
        } else {
            baseUrl = realUrl;
        }
    }


    public interface CallBackHttp {
        void finish(String msg);

        void error(String msg);
    }
}
