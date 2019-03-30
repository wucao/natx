# Natx
Natx 是一个基于 Java Netty 实现的可用于内网穿透的代理工具，支持 TCP 协议（如 HTTP 协议）。

Natx 包含服务端和客户端两部分，服务端运行在带有公网 IP 的服务器上，客户端运行在没有公网 IP 的机器上。

由于大部分联网设备只有内网 IP ，例如大部分家庭宽带，我们在本地启动的网络应用无法对外提供访问，这种场景下可以使用 Natx 将本地网络地址映射到外网，对外提供访问。

下载： https://github.com/wucao/natx/releases/tag/v1.0.1

## 启动
### 服务端启动
在带有公网 IP 的服务器上执行 Java 命令:
```
java -jar natx-server.jar
```
看到输出表示启动成功:
```
Natx server started on port 7731
```
默认服务端端口号是7731。注意这个端口号是 Natx 客户端连接 Natx 服务器的端口号，并非对外网提供访问的端口号。

指定端口号和 password :
```
java -jar natx-server.jar -port 9000 -password password123
```
默认情况下 Natx 服务端未指定 password ，任何客户端都可以直接连接并使用 Natx 服务器，这样很不安全，建议使用 Natx 服务端时指定一个 password 作为连接服务端的密码。

### 客户端启动
在没有公网 IP 的机器上执行 Java 命令:
```
java -jar natx-client.jar -server_addr 211.161.xxx.xxx -server_port 7731 -password password123 -proxy_addr localhost -proxy_port 8080 -remote_port 10000
```

参数说明:
- `server_addr` Natx 服务端的网络地址，即 Natx 服务端运行的服务器外网 IP 或 hostname
- `server_port` Natx 服务端的端口
- `password` Natx 服务端的 password
- `proxy_addr` 被代理的应用网络地址
- `proxy_port` 被代理的应用端口号
- `remote_port` Natx 服务端对外访问该应用的端口

启动成功后可以通过 server_addr:remote_port 访问被代理的应用，如果被代理的应用是 HTTP 应用，可以通过 http://211.161.xxx.xxx:10000 在外网访问。

## 典型使用场景
- 在开发微信公众号服务时，由于本机没有外网 IP ，微信的服务器无法访问到本机接口，调试很不方便，可以使用 Natx 将本地网络地址映射到外网，便于调试。
