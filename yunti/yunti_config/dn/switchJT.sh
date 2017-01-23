#!/bin/bash
switchJT()
{
    echo "Start @ `date`"
    
    echo "Down VIP on $ACTIVE_HOST" 
    ssh $ACTIVE_HOST -- sudo /sbin/ifconfig bond0:0 down
    
    echo "Stop Active JT on $ACTIVE_HOST"
    "$bin"/hadoop mradmin -stopActive $ACTIVE_HOST 

    ssh $ACTIVE_HOST -- sync
    
    echo "Wait at most 10 seconds"
    date
    "$bin"/hadoop mradmin -waitStandbyDone $STANDBY_HOST 10000
    date
    
    echo "Standby To Active JT on $STANDBY_HOST"
    "$bin"/hadoop mradmin -standbyToActive $STANDBY_HOST 
    
    echo "Mount VIP on $STANDBY_HOST"
    ssh $STANDBY_HOST -- sudo /sbin/ifconfig bond0:0 $VIP netmask $NETMASK
    
    echo "ARP Ping..."
    ssh $STANDBY_HOST -- sudo /sbin/arping -q -c 1 -s $VIP -I eth0 $ARP_PING
    
    echo "Over @ `date`"
}
restartJT()
{
    HOST=$1
    echo "Start @ `date`"
    
    echo "Stop JobTracker on $HOST"
    ssh $HOST -- $HADOOP stop jobtracker
    
    echo "Sleep 5s ..."
    sleep 5
    
    echo "Clear new history ... @ $HOST"
    ssh $HOST -- find /home/hadoop/cluster-data/logs/newHistory/ -type f -delete 
    
    echo "Clear xml ... @ $HOST"
    ssh $HOST -- find /home/hadoop/cluster-data/logs/*.xml -type f -delete
    
    echo "Start Standby JobTracker on $HOST"
    ssh $HOST -- $HADOOP start jobtracker -standby
    
    echo "Over @ `date`"
}

JT1=r02i11001.yh.aliyun.com
JT2=r02i11003.yh.aliyun.com
VIP=10.249.65.101
ARP_PING=10.249.65.255
NETMASK=255.255.255.0

HADOOP=/home/hadoop/hadoop-current/bin/hadoop-daemon.sh
JT1_STATUS=`/home/hadoop/hadoop-current/bin/hadoop mradmin -getHAStatus $JT1`
JT2_STATUS=`/home/hadoop/hadoop-current/bin/hadoop mradmin -getHAStatus $JT2`

if [ "$JT1_STATUS" == "ACTIVE" ]
then
    ACTIVE_HOST=$JT1
    STANDBY_HOST=$JT2
else
    ACTIVE_HOST=$JT2
    STANDBY_HOST=$JT1
fi

echo "the active host is $ACTIVE_HOST"
echo "the standby host is $STANDBY_HOST"

echo -n "Are you sure to contiune, will be switch JT from $ACTIVE_HOST to $STANDBY_HOST?(y/n)"
read YON
if [[ "$YON" != "y" && "$YON" != "Y" && "$YON" != "yes" && "$YON" != "Yes" ]]; then
    exit 1;
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "we start switchJT"
switchJT 

echo "sleep 5s"
sleep 5

echo "we restart the offline JT to STANDBY"
restartJT $ACTIVE_HOST


