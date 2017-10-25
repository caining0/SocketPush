package com.lashou.pushlib.service;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import static com.lashou.pushlib.service.TCPClient.message_operation_auth_return;
import static com.lashou.pushlib.service.TCPClient.message_operation_heartbeat_return;
import static com.lashou.pushlib.service.TCPClient.message_operation_push;


/**
 * @author Michael Huang
 */
public class MessageClient {
    //	Socket socket = null;
    private SSLSocket socket;

    static DataOutputStream dos = null;
    static DataInputStream dis = null;
    private static boolean bConnected = false;
    static Thread tRecv = null;
    static Thread sendThread = null;
    public static boolean showLog = true;
    private static Context context;
    private int TIME_OUT_PERIOD = 1000 * 60 * 3;//3分钟超时时间
    static String ip;
    static int port;
    static String appkey;
    private static MyService.MyBinder binder;
    private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static MessageClient getInstance(Context context, MyService.MyBinder binder, String ip, int port, String appkey) {
        MessageClient.binder = binder;
        MessageClient.context = context;
        MessageClient.ip = ip;
        MessageClient.port = port;
        MessageClient.appkey = appkey;
        return new MessageClient();
    }

//    public MessageClient(String ip, int port, String appkey) {
//        this(null, null, ip, port, appkey);
//    }

//	public static void main(String[] args) {
////		MessageClient client = new MessageClient("10.168.31.69",8088,"1");
//		MessageClient client = new MessageClient("wdpush.lashou.com",80,"1");
//		client.showLog = false;
//		client.connect();
//	}

    public void connect(/*String ip,int port,String authappkey*/) {
        lashReceiveTime = System.currentTimeMillis();
        showLog("connect()");
        try {
            //普通链接
//			socket = new Socket();
//			socket.connect(new InetSocketAddress(ip, port), 5000);

            //ssl链接
            SSLContext sslContext = AuthSSLSocket.getSSLContext(context);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            socket = (SSLSocket) factory.createSocket();
            String[] pwdsuits = socket.getSupportedCipherSuites();
            //socket可以使用所有支持的加密套件
            socket.setEnabledCipherSuites(pwdsuits);
            //默认就是true
            socket.setUseClientMode(true);
            socket.connect(new InetSocketAddress(ip, port), 10 * 1000);
            MyHandshakeCompletedListener listener = new MyHandshakeCompletedListener();
            socket.addHandshakeCompletedListener(listener);


            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
//			System.out.println("~~~~~~~~连接成功~~~~~~~~!");
            showLog("~~~~~~~~连接成功~~~~~~~~!");
                bConnected = true;
                start(appkey);
        } catch (Exception e) {
            e.printStackTrace();
            //如果有异常重新连接
            if (binder != null) {
                binder.delayStartPushServer();
            }
        }
        showLog("bConnected=" + bConnected);
    }

    private static void showLog(String content) {
        if (showLog) {
            Log.i("info", "------" + content);
        }
    }

    public boolean isbConnected() {
        return bConnected;
    }

    private void start(String appkey) {
        if (appkey == null) {
            showLog("start(null)");
            return;
        }
//		System.out.println("start("+appkey+") thread to receive and send");
        showLog("start(" + appkey + ") thread to receive and send");
            tRecv = new Thread(new RecvThread());
            tRecv.start();

            sendThread = new Thread(new SendThread(appkey));
            sendThread.start();
    }

    public void disconnect() {
//		System.out.println("disconnect()");
        bConnected = false;
        try {
            dos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket.close();
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class SendThread implements Runnable {
        private String authappkey;

        //		private int counter = 0;
        public SendThread(String key) {
            this.authappkey = key;
        }

        @Override
        public void run() {
            showLog("run()");
            try {
                if (bConnected) {
                    dos.write(TCPClient.auth(authappkey));
                }
                while (bConnected) {
                    if (System.currentTimeMillis() - TIME_OUT_PERIOD > lashReceiveTime) {
                        if (binder != null) {
                            binder.delayStartPushServer();
                        }
                        break;
                    }
//					if(counter>3){
//						disconnect();
//						if(binder != null){
//							binder.delayStartPushServer();
//						}
////						connect();
//						break;
//					}
//					counter++;
                    dos.write(TCPClient.heartbeat(authappkey));
                    dos.flush();
                    Thread.sleep(TCPClient.millis);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static long lashReceiveTime = 0;

    private static class RecvThread implements Runnable {

        private int errorTime = 0;

        @Override
        public void run() {
            showLog("run()");
            try {
                while (bConnected) {
                    DataPacket dataPacket = TCPClient.read(dis);
                    Log.i("info", "=====>dataPacket");

                    if (dataPacket == null) {
                        if (errorTime > 100) {
                            errorTime = 0;
                            if (binder != null) {
                                binder.delayStartPushServer();
                            }
                            break;
                        }
                        errorTime++;
                        showLog("errorTimes" + errorTime);
                        continue;
                    }
                    errorTime = 0;
                    switch (dataPacket.getOperation()) {
                        case message_operation_auth_return:
                            lashReceiveTime = System.currentTimeMillis();
                            break;
                        case message_operation_heartbeat_return:
                            lashReceiveTime = System.currentTimeMillis();
                            if (showLog) {
//                                Intent i = new Intent(PushMessageUtil.ACITION_MESSAGE);
//                                i.putExtra(PushMessageUtil.EXTRA_MESSAGE_HEARTBEAT, lashReceiveTime);
//                                context.sendOrderedBroadcast(i, null);

                                final Message message = new Message();
                                message.what=1;
                                message.obj=lashReceiveTime;

                                MyService.getMyHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyService.getMyHandler().sendMessage(message);
                                    }
                                });
                            }
                            break;
                        case message_operation_push:
                            String body = dataPacket.getBody();
                            if (showLog && !TextUtils.isEmpty(body)) {
//                                Intent i = new Intent(PushMessageUtil.ACITION_MESSAGE);
//                                i.putExtra(PushMessageUtil.EXTRA_MESSAGE, body);
//                                context.sendOrderedBroadcast(i, null);

                                final Message message = new Message();
                                message.what=0;
                                message.obj=body;
                                MyService.getMyHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyService.getMyHandler().sendMessage(message);
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }

//					sendBroadcast(i);
                }
            } catch (SocketException e) {
                e.printStackTrace();
//				System.out.println("SocketException ，bye!");
            } catch (EOFException e) {
                e.printStackTrace();
//				System.out.println("EOFException ，bye!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}