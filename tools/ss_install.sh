!/bin/sh

add_source() {
  # fix apt-get update 404 not found
  # http://askubuntu.com/questions/244822/errors-running-apt-get-update-and-apt-get-install-mysql-server
  cat << EOF
deb http://us.archive.ubuntu.com/ubuntu lucid main multiverse universe
deb http://us.archive.ubuntu.com/ubuntu lucid-security main multiverse universe
deb http://us.archive.ubuntu.com/ubuntu lucid-updates main multiverse universe
EOF
}

passwd="$1"
port=$2

# up date apt-get
sudo -s
add_source >> /etc/apt/source.list
apt-get -y update
apt-get -y install build-essential python-pip python-m2crypto python-dev
pip install gevent shadowsocks

gen_config() {
  cat << EOF
{
  "server":"0.0.0.0",
  "server_port":$port, 
  "local_address": "127.0.0.1",
  "local_port":1080,
  "password":"$passwd",
  "timeout":300,
  "method":"aes-256-cfb",
  "fast_open": false,
  "workers": 2
}
EOF
}
gen_config > /etc/shadowsocks.json
ssserver -c /etc/shadowsocks.json -d start
