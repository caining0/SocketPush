package com.lashou.pushlib.service;


import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


public class AuthSSLSocket {

    /**
     * client.bks包含了两端的证书，所以双重验证用的一个bks文件，更换bks文件的时候注意一下
     * @param context
     * @return
     * @throws Exception
     */
    public static SSLContext getSSLContext(Context context) throws Exception{
        //Trust Key Store
        KeyStore keyStore = KeyStore.getInstance("BKS");
        InputStream keyStream = context.getAssets().open("client.bks");
        char keyStorePass[]="密码".toCharArray();  //证书密码
        keyStore.load(keyStream ,keyStorePass);
        keyStream.close();
        String protocol = "TLSv1.2";

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        trustManagerFactory.init(keyStore);
        TrustManager[] tms = trustManagerFactory.getTrustManagers();  
          
        KeyManager[] kms = null;
        keyStore = KeyStore.getInstance("BKS");
        keyStream = context.getAssets().open("client.bks");
        keyStore.load(keyStream,"密码".toCharArray());
        keyStream.close();
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
        keyManagerFactory.init(keyStore, "密码".toCharArray());
        kms = keyManagerFactory.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(kms, tms, null);    
          
        return sslContext;  
    }

}  