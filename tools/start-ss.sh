#!/bin/bash 

echo "Hello World.  The time is now $(date -R)!" | tee /root/output.txt

apt-get update
apt-get install -y git
pip install git+https://github.com/shadowsocks/shadowsocks.git@master
/usr/local/bin/ssserver -p 9292 -k helloworld -m aes-256-cfb --user nobody -d start
