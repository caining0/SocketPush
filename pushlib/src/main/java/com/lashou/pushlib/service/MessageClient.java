package com.lashou.pushlib.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lashou.pushlib.utils.PushMessageUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import static com.lashou.pushlib.service.TCPClient.message_operation_auth_return;
import static com.lashou.pushlib.service.TCPClient.message_operation_heartbeat_return;
import static com.lashou.pushlib.service.TCPClient.message_operation_push;


/**
 * @author Michael Huang
 * 
 */
public class MessageClient{
//	Socket socket = null;
	private SSLSocket socket;

	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;
	Thread tRecv = null;
	Thread sendThread = null;
	public boolean showLog = true;
	private Context context;
	private int TIME_OUT_PERIOD = 1000 * 60 * 3;//3分钟超时时间
	String ip;
	int port;
	String appkey;
	private MyService.MyBinder binder;

	public MessageClient(Context context, MyService.MyBinder binder, String ip, int port, String appkey){
		this.binder = binder;
		this.context = context;
		this.ip = ip;
		this.port = port;
		this.appkey = appkey;
	}
	public MessageClient(String ip,int port,String appkey){
		this(null, null,ip, port, appkey);
	}

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
			socket = (SSLSocket)factory.createSocket();
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
		} catch (Exception e){
			e.printStackTrace();
			//如果有异常重新连接
			if(binder != null){
				binder.delayStartPushServer();
			}
		}
		showLog("bConnected="+bConnected);
	}

	private void showLog(String content) {
		if(showLog){
            Log.i("info","------"+content);
        }
	}

	public boolean isbConnected(){
		return bConnected;
	}
	private void start(String appkey){
		if(appkey == null){
			showLog("start(null)");
			return ;
		}
//		System.out.println("start("+appkey+") thread to receive and send");
		showLog("start("+appkey+") thread to receive and send");
		tRecv = new Thread(new RecvThread());
		sendThread = new Thread(new SendThread(appkey));
		tRecv.start();
		sendThread.start();
	}

	public void disconnect() {
//		System.out.println("disconnect()");
		bConnected = false;
		try {
			dos.close();

		} catch (Exception e){
			e.printStackTrace();
		}
		try {
			dis.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		try {
			socket.close();
			socket = null;
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private class SendThread implements Runnable{
		private String authappkey;
//		private int counter = 0;
		public SendThread(String key){
			this.authappkey = key;
		}
		@Override
		public void run() {
			showLog("run()");
			try {
				if(bConnected){
					dos.write(TCPClient.auth(authappkey));
				}
				while (bConnected){
					if(System.currentTimeMillis() - TIME_OUT_PERIOD > lashReceiveTime){
						if(binder != null){
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
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private long lashReceiveTime =0;
	private class RecvThread implements Runnable {

		private int errorTime = 0;
		public void run() {
			showLog("run()");
			try {
				while (bConnected) {
					DataPacket dataPacket = TCPClient.read(dis);
					if(dataPacket == null){
						if(errorTime>100){
							errorTime = 0;
							if(binder != null){
								binder.delayStartPushServer();
							}
							break;
						}
						errorTime++;
						showLog("errorTimes"+errorTime);
						continue;
					}
					errorTime = 0;
					switch (dataPacket.getOperation()){
						case message_operation_auth_return:
							lashReceiveTime = System.currentTimeMillis();
							break;
						case message_operation_heartbeat_return:
							lashReceiveTime = System.currentTimeMillis();
							if(showLog ){
								Intent i = new Intent(PushMessageUtil.ACITION_MESSAGE);
								i.putExtra(PushMessageUtil.EXTRA_MESSAGE_HEARTBEAT,lashReceiveTime);
								context.sendOrderedBroadcast(i, null);
							}
							break;
						case message_operation_push:
							if(showLog && !TextUtils.isEmpty(dataPacket.getBody())){
								Intent i = new Intent(PushMessageUtil.ACITION_MESSAGE);
								i.putExtra(PushMessageUtil.EXTRA_MESSAGE,dataPacket.getBody());
								context.sendOrderedBroadcast(i, null);
							}
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
			}catch (Exception e){
				e.printStackTrace();
			}

		}

	}
}