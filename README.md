###说明：本SDK为公司内部使用，可参考共同探讨学习，如想使用，请联系商务  ###
## 一、     理论知识  ##

##简介##

**Socket**的英文原义是“孔”或“插座”。通常也称作"套接字"，用于描述IP地址和端口，是一个通信链的句柄，可以用来实现不同虚拟机或不同计算机之间的通信。

  **socket**是对TCP/IP协议的封装，Socket本身并不是协议，而是一个调用接口（API），通过Socket，我们才能使用TCP/IP协议。
## socket连接的三次握手 ##
    第一次握手：客户端发送syn包(syn=j)到服务器，并进入SYN_SEND状态，等待服务器确认；

    第二次握手：服务器收到syn包，必须确认客户的SYN（ack=j+1），同时自己也发送一个SYN包（syn=k），即SYN+ACK包，此时服务器进入SYN_RECV状态；

    第三次握手：客户端收到服务器的SYN＋ACK包，向服务器发送确认包ACK(ack=k+1)，此包发送完毕，客户端和服务器进入ESTABLISHED状态，完成三次握手。
SYN:([https://baike.baidu.com/item/SYN/8880122?fr=aladdin](https://baike.baidu.com/item/SYN/8880122?fr=aladdin))
## Socket建立网络连接的步骤 ##
   建立Socket连接至少需要一对套接字，其中一个运行于客户端，称为ClientSocket ，另一个运行于服务器端，称为ServerSocket 。
   套接字之间的连接过程分为三个步骤：服务器监听，客户端请求，连接确认。

   1.服务器监听：服务器端套接字并不定位具体的客户端套接字，而是处于等待连接的状态，实时监控网络状态，等待客户端的连接请求。
    
   2.客户端请求：指客户端的套接字提出连接请求，要连接的目标是服务器端的套接字。为此，客户端的套接字必须首先描述它要连接的服务器的套接字，指出服务器端套接字的地址和端口号，然后就向服务器端套接字提出连接请求。
    
   3.连接确认：当服务器端套接字监听到或者说接收到客户端套接字的连接请求时，就响应客户端套接字的请求，建立一个新的线程，把服务器端套接字的描述发给客户端，一旦客户端确认了此描述，双方就正式建立连接。而服务器端套接字继续处于监听状态，继续接收其他客户端套接字的连接请求。

## TCP和UDP  ##
**TCP**：    传输控制协议，面向连接的的协议，稳定可靠。当客户和服务器彼此交换数据前，必须先在双方之间建立一个TCP连接，之后才能传输数据。

**UDP**：    广播式数据传输，UDP不提供可靠性，它只是把应用程序传给IP层的数据报发送出去，但是并不能保证它们能到达目的地。由于UDP在传输数据报前不用在客户和服务器之间建立一个连接，且没有超时重发等机制，故而传输速度很快。
## HTTP 与socket比较 ##

**1.关系**

**socket**是对TCP/IP协议的封装和应用，**socket**又分TCP和UDP两种。
而HTTP协议是建立在TCP协议之上的一种应用:
HTTP连接最显著的特点是客户端发送的每次请求都需要服务器回送响应，在请求结束后，会主动释放连接。从建立连接到关闭连接的过程称为“一次连接”。
可以看出：HTTP是基于socket。

**2.优缺点：**

        1）.socket
    优点：	
    		（1）.传输数据为字节级，传输数据可自定义，数据量小。相应的移动端开发，手机费用低
    	   	（2）.传输数据时间短，性能高
    	   	（3）.适合Client/Server(C/S)之间信息实时交互
    	  	（4）.可以加密，数据安全性高
    缺点： 	
    		（1）.需要对传输的数据进行解析，转化为应用级的数据
     		（2）.对开发人员的开发水平要求高
    		（3）.相对于Http协议传输，增加了开发量
    		（4）.tcp比http性能消耗要高，开销大。
    	2）.http
    	优点：
    		（1）.基于应用级的接口使用方便
    	   	（2）.要求的开发水平不高，容错性强
    	缺点： 
    		（1）.传输速度慢，数据包大。
    		（2）.如实现实时交互，服务器性能压力大
    		（3）.数据传输安全性差
	
**3.对于消耗内存比较**

![avatar](https://github.com/Oslanka/SocketPush/blob/master/SocketAndHttp.png)

可以看出，性能上socket比http更快，但内存上，socket也更占用内存。socket服务器也对配置要求更高。所以socket综合起来，比http开发成本要高。
## xmpp简介##
全称:可扩展通讯和表示协议
简介:可扩展通讯和表示协议 (XMPP) 可用于服务类实时通讯、表示和需求响应服务中的XML数据元流式传输。XMPP以Jabber协议为基础，而Jabber是即时通讯中常用的开放式协议。
XMPP（可扩展消息处理现场协议）是基于可扩展标记语言（XML）的协议，它用于即时消息（IM）以及在线现场探测。
XMPP的前身是Jabber，一个开源形式组织产生的网络即时通信协议。
（XMPP这里不深讲，有兴趣同学，可以自己研究研究）。

## 我们为什么选socket ##
首先排除使用http，相比http，socket能满足我们长连接需求，而http不行。其次，虽然XMPP同样可以实现本库功能，但XMPP更侧重IM（即时通讯），切XMPP没有Socket使用灵活。 故最终选用 Socket 作为Push主框架。

    
## 二、            Push sdk android用法  ##
## 1. 引入pushlib 库 ##
    compile project(':pushlib')
## 2.初始化 ##
    Push.init(this,apiId);

    /*初始化,如果 不用start 只用通知功能，可只调用init 而不调用。。。其中 apiId由服务端与客户端协商。
    在PAD点餐项目中，这个apiId使用了商户的id，推送时根据商户的id推送，其他商家注册的apiId不会被推送。*/

    Push.setDebug(true);//是否使用测试环境 服务器  10.168.31.69
## 3.使用Push ##
     public class MainActivity extends AppCompatActivity {
    	private TextView textValue;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        textValue = (TextView) findViewById(R.id.text_value);
	        Push.start(this, "1234", new MessageListener() {
	            @Override
	            public void onGetKey(String id) {
					//获取的服务端 唯一指定id
	            }
	
	            @Override
	            public void onMessageReceive(String message) {
	                textValue.setText(message);
    				//接收到的数据
	            }
	
	            @Override
	            public void onHeartBeat(String beatTime) {
	                Log.i("push------>time", beatTime);
    				//心跳检测
	            }
	
	            @Override
	            public void onError(String str) {
	                Log.i("push------>", str);
    				//异常错误
	            }
	        });
	
	    }

		@Override
	    protected void attachBaseContext(Context base) {
	        Push.proguard(base);
    		//调用此方法，会启动进程守护，会在程序被杀死的情况下依然连接socket
	        super.attachBaseContext(base);
	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        Push.stop(this);
	    }
    }
## 4.PushSDK推送数据结构 ##
   说明：pushSDK 推送数据结构 采用json形式，json形式无限制，但action字段必须固定.

	    {
		    "action":"notification",
		    "orderid":"38468",
		    "type":"new"
	    }
### "action":"notification" ### 
   如想使用Notification，只需指定
 `"action":"notification"`
，当action不指定为"notification"，为普通消息。

![avatar](https://github.com/Oslanka/SocketPush/blob/master/notification.gif)
## 5.其他 ##
### android进程守护参考 ：[https://github.com/Marswin/MarsDaemon](https://github.com/Marswin/MarsDaemon)

   说明：A lite library, you can make your project depend it easily, and your project will be UNDEAD
   （这是一个轻量级的库，保证你的程序不死）本使用已对MarsDaemon做修改，不必继承DaemonApplication
   直接调用 Push.proguard(base);

**进程守护实现**

        1.提高进程优先级，降低被回收或杀死概率
    	2.在进程被干掉后，进行拉起

   程序保活方法很多种，不一一列举实现。

### 启用进程守护与否 ###

![avatar](https://github.com/Oslanka/SocketPush/blob/master/proguard.gif)
   
## 三、            PushLib 我们做了什么  ##
## 1.流程 ##
    init=>获取唯一id=>启动服务=>socket请求连接=>连接成功

## 2.Socket连接中的SSL双向认证 ##
请转向[https://github.com/Oslanka/SocketPush/blob/master/SSL.md](https://github.com/Oslanka/SocketPush/blob/master/SSL.md)
## 3.Socket 字节规定 ##
## Socket 传送内容字节规定 ##

规定字节排列方式，用于取对应字节，处理逻辑

![avatar](https://github.com/Oslanka/SocketPush/blob/master/byte.png)

packet 4字节，header 2字节，version 2字节，operation 4字节，sequence 4 字节，后面是body 理论上不限制。

##  Contact me  ##

Email: [cainingning@lashou-inc.com](cainingning@lashou-inc.com)