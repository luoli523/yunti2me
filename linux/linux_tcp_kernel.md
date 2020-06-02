## [Linux](https://www.kernel.org/doc/Documentation/networking/ip-sysctl.txt)

### Three system configuration parameters must be set to support a large number of open files and TCP connections with large bursts of messages. Changes can be made using the /etc/rc.d/rc.local or /etc/sysctl.conf script to preserve changes after reboot.

#### 1. /proc/sys/fs/file-max: The maximum number of concurrently open files.
    fs.file-max = 1000000
#### 2. /proc/sys/net/ipv4/tcp_max_syn_backlog: Maximum number of remembered connection requests, which are still did not receive an acknowledgment from connecting client. The default value is 1024 for systems with more than 128Mb of memory, and 128 for low memory machines.
    net.ipv4.tcp_max_syn_backlog = 3240000
#### 3. /proc/sys/net/core/somaxconn: Limit of socket listen() backlog, known in userspace as SOMAXCONN. Defaults to 128.  
    net.core.somaxconn = 3240000

### Other system configuration parameters are also optional in some cases.

#### 1. /proc/sys/net/ipv4/ip_local_port_range: Increase system ip port limits to allow for more connections
    net.ipv4.ip_local_port_range = 1024 65535
#### 2. /proc/sys/net/ipv4/tcp_wmem and /proc/sys/net/core/wmem_max: tcp send window,increasing the tcp send and receive buffers will increase the performance a lot if (and only if) you have a lot of large files to send.
    net.ipv4.tcp_wmem = 4096 65536 524288
    net.core.wmem_max = 1048576
#### 3. /proc/sys/net/ipv4/tcp_rmem and /proc/sys/net/core/rmem_max: tcp receive window,If you have a lot of large file uploads, increasing the receive buffers will help.
    net.ipv4.tcp_rmem = 4096 87380 524288
    net.core.rmem_max = 1048576
#### 4. some parameter associated with tcp time wait
    net.ipv4.tcp_max_tw_buckets = 6000
    net.ipv4.tcp_tw_recycle = 1
    net.ipv4.tcp_tw_reuse = 1
#### 5 .some parameters associated with security
    net.ipv4.tcp_syncookies = 1
    net.ipv4.tcp_max_orphans = 262144

## [References - Netty](http://www.infoq.com/news/2013/11/netty4-twitter)
## [References - Lighttpd](http://redmine.lighttpd.net/projects/1/wiki/Docs_Performance)
