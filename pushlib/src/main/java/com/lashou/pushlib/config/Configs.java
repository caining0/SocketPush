package com.lashou.pushlib.config;

/**
 * Created by Administrator on 2017/6/14.
 */
public class Configs {

    private static final String serverIp_online = "wdpush.lashou.com";
    private static final int serverPort_online = 8080;
    private static final String serverIp_debug = "10.168.31.69";
    private static final int serverPort_debug = 8088;

    public static String serverIp = serverIp_debug;
    public static int serverPort = serverPort_debug;

    public static void setUrl(boolean debug) {
        if (debug) {
            serverIp = serverIp_debug;
            serverPort = serverPort_debug;
        } else {
            serverIp = serverIp_online;
            serverPort = serverPort_online;
        }
    }

}
