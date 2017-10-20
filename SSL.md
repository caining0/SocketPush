## SSL/TLS ##
	SSL：（Secure Socket Layer，安全套接字层），位于可靠的面向连接的网络层协议和应用层协议之间的一种协议层。SSL通过互相认证、使用数字签名确保完整性、使用加密确保私密性，以实现客户端和服务器之间的安全通讯。该协议由两层组成：SSL记录协议和SSL握手协议。
	TLS：(Transport Layer Security，传输层安全协议)，用于两个应用程序之间提供保密性和数据完整性。该协议由两层组成：TLS记录协议和TLS握手协议。

SSLSocket通信是对Socket通信的拓展。在Socket通信的基础上添加了一层安全性保护，提供了更高的安全性，包括身份验证、数据加密以及完整性验证。

SSLSocket双向认证实现技术： JSSE（Java Security Socket Extension），它实现了SSL和TSL（传输层安全）协议。在JSSE中包含了数据加密，服务器验证，消息完整性和客户端验证等技术。通过使用JSSE，可以在客户机和服务器之间通过TCP/IP协议安全地传输数据。为了实现消息认证：
## X.509是一种非常通用的证书格式 ##
[https://baike.baidu.com/item/x509/1240109?fr=aladdin](https://baike.baidu.com/item/x509/1240109?fr=aladdin "X.509百科")
符合ITU-T X.509国际标准,本库SSL加密使用的是kbs格式秘钥，符合X.509标准。

# 双向验证 #

### 服务器端需要： ##

  	1、KeyStore： 其中保存服务器端的私钥

  	2、Trust KeyStore： 其中保存客户端的授权证书
### 客户端需要： ##

  	1、KeyStore：其中保存客户端的私钥（BKS 文件（密码验证bks证书））

   	2、Trust KeyStore：其中保存服务端的授权证书
## SSL Socket双向认证的安全性： ##
	（1）可以确保数据传送到正确的服务器端和客户端。

	（2）可以防止消息传递过程中被窃取。

	（3）防止消息在传递过程中被修改.。



##  Contact me    ##

Email: [cainingning@lashou-inc.com](cainingning@lashou-inc.com)