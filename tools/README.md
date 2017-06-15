# 使用手册

* 功能：批量登录、管理分布式集群，批量的文件分发及拷贝，特点是交互体验非常棒
* 核心命令：gpssh   gpscp    gpssh-exkeys(打通ssh登录)
* 使用方法：

  * 通过install.sh安装缺少的python库
  * 准备好机器列表：一个写满机器ip的列表文件

```
phrack$ cat machlist 
121.40.185.210
121.40.185.211
121.40.185.212
121.40.185.213
```
执行

    gpssh-exkeys -f machlist
    
根据提示输入密码等内容，出现`completed successfully`意味着打通成功。

然后就可以使用gpssh同时交互式管理所有机器，gpscp则可以将任意文件和目录分发至目标集群上。

```
[gpssh]$ ./gpssh -f machlist
=> ls
[121.42.185.210] bigdata gpssh nohup.out software
[121.42.185.211] bigdata gpssh nohup.out software
[121.42.185.212] bigdata gpssh nohup.out software
[121.42.185.213] bigdata gpssh nohup.out software
```