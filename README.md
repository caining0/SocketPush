# # SocketPush #
## TCP、UDP和HTTP关系  ##
> TCP/IP是个协议组，可分为三个层次：网络层、传输层和应用层。 
> 网络层：有 IP协议、ICMP协议、ARP协议、RARP协议和BOOTP协议。 
> 传输层：有TCP协议与UDP协议。TCP和UDP区别：TCP提供有保证的数据传输，而UDP不提供。 
> 应用层：FTP、HTTP、SMTP、DNS等协议。因此，HTTP本身就是一个协议，是从Web服务器传输超文
本到本地浏览器的传送协议。

##**socket 简介**


> ###**Socket**的英文原义是“孔”或“插座”。通常也称作"套接字"，用于描述IP地址和端口，是一个通信链的句柄，可以用来实现不同虚拟机或不同计算机之间的通信。
>####socket是对TCP/IP协议的封装，Socket本身并不是协议，而是一个调用接口（API），通过Socket，我们才能使用TCP/IP协议。

# Push sdk android用法 #
## 1. 引入pushlib 库 ##
2. 
    ` @Override
    public void onCreate() {
        super.onCreate();
        Push.init(this,"1234");//初始化,如果 不用start 只用通知功能，可只调用init 而不调用start
    }`