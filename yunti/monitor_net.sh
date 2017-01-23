#!/bin/bash
# monitor the interface's network traffic.
if [ $# -ne 3 ];then
  echo Useage : $0 interface interval count
  echo Example: $0 eth0 2 10
  exit
fi

eth=$1
count=$3
interval=$2
inbytesfirst=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $2}')

if [ -z "$inbytesfirst" ];then
  echo The network interface $eth is not exits!
  exit 1;
fi

outbytesfirst=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $10}')
inpacketsfirst=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $3}')
outpacketsfirst=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $11}')
sleep $interval"s"

i=0
while [ "$i" -lt "$count" ]
do
  inbytesend=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $2}')
  outbytesend=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $10}')
  inpacketsend=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $3}')
  outpacketsend=$(cat /proc/net/dev |tr ':' ' '|awk '/'$eth'/{print $11}')

  bytesin=$(($inbytesend-$inbytesfirst))
  bytesout=$(($outbytesend-$outbytesfirst))
  packetsin=$(($inpacketsend-$inpacketsfirst))
  packetsout=$(($outpacketsend-$outpacketsfirst))

  if [ "$bytesin" -lt "0" ];then
    bytesin=$((4294967295-$inbytesfirst+$inbytesend))
    #echo bytesin $bytesin $inbytesfirst $inbytesend
  fi
  if [ "$bytesout" -lt "0" ];then
    bytesout=$((4294967295-$outbytesfirst+$outbytesend))
    #echo bytesout $bytesout $outbytesfirst $outbytesend
  fi
  if [ "$packetsin" -lt "0" ];then
    packetsin=$((4294967295-$inpacketsfirst+$inpacketsend))
    #echo packetsin $packetsin $inpacketsfirst $inpacketsend
  fi
  if [ "$packetsout" -lt "0" ];then
    packetsout=$((4294967295-$outpacketsfirst+$outpacketsend))
    #echo packetsout $packetsout $outpacketsfirst $outpacketsend
  fi

  bytesin=$(($bytesin/$interval))
  bytesout=$(($bytesout/$interval))
  packetsin=$(($packetsin/$interval))
  packetsout=$(($packetsout/$interval))

  sumbytesin=$(($sumbytesin+$bytesin))
  sumbytesout=$(($sumbytesout+$bytesout))
  sumpacketsin=$(($sumpacketsin+$packetsin))
  sumpacketsout=$(($sumpacketsout+$packetsout))

  if [ $(($i%20)) -eq 0 ];then
    echo " ifname   | in_kbits/s out_kbits/s | in_kBytes/s out_kBytes/s | in_packets/s out_packets/s"
    echo "--------- | ---------- ----------- | ----------- ------------ | ------------ -------------"
  fi
  echo $eth $bytesin $bytesout $packetsin $packetsout |awk '{printf("%9s | %10d %11d | %11d %12d | %12d %13d\n",$1,$2/128,$3/128,$2/1024,$3/1024,$4,$5)}'
  inbytesfirst=$inbytesend
  outbytesfirst=$outbytesend
  inpacketsfirst=$inpacketsend
  outpacketsfirst=$outpacketsend

  i=$(($i+1))
  sleep $interval"s"
done

sumbytesin=$(($sumbytesin/$i))
sumbytesout=$(($sumbytesout/$i))
sumpacketsin=$(($sumpacketsin/$i))
sumpacketsout=$(($sumpacketsout/$i))

echo "--------- | ---------- ----------- | ----------- ------------ | ------------ -------------"
echo Average $sumbytesin $sumbytesout $sumpacketsin $sumpacketsout |awk '{printf("%9s | %10d %11d | %11d %12d | %12d %13d\n",$1,$2/128,$3/128,$2/1024,$3/1024,$4,$5)}'

