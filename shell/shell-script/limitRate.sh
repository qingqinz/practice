#!/bin/sh



# "./limitRate.sh help （帮助）"
# "./limitRate.sh stop （停止限速）"
# "./limitRate.sh 参数1 参数2 参数3 参数4（参数代表：网卡总速 下载限速 允许峰值 限速端口），流量单位:Mbit"
# "./limitRate.sh 无参数则默认 100 90 90 9080"
#eg: ./limitRate.sh 100 90 90 9080(启动限速命令，网卡总速100Mbit 下载限速90Mbit允许峰值90Mbit限速端口9080)

if [ "$1" = "stop" ];then
   tc qdisc del dev eth0 root;#清除限速
   echo "stop success..."
elif [ "$1" = "help" ];then
   echo "./limitRate.sh stop （停止限速）"
   echo "./limitRate.sh 参数1 参数2 参数3 参数4（参数代表：网卡总速 下载限速 允许峰值 限速断口），流量单位:Mbit"
   echo "./limitRate.sh 无参数则默认 100 90 90 9080"
else
   totalRate=$1
   limitRate=$2
   limitMaxRate=$3
   limitPort=$4
   if [ ! $totalRate ];then
      totalRate=100
      echo "totalRate默认100Mbit..."
   fi
   if [ ! $limitRate ];then
      limitRate=85
      echo "limitRate默认90Mbit..."
   fi
   if [ ! $limitMaxRate ];then
      limitMaxRate=85
      echo "limitMaxRate默认90Mbit..."
   fi
   if [ ! $limitPort ];then
      limitPort=9080
      echo "limitPort默认9080..."
   fi
   #网卡eth0对这个网卡进行带宽的限制#建立eth0队列 命令解释:将一个htb队列绑定在eth0上,编号为1:0，设置默认号是 20
   tc qdisc add dev eth0 root handle 1: htb default 20;
   #建立跟分类 命令解释:在队列1:0上创建根分类1:1 限速，类别htb,限速100Mbit
   tc class add dev eth0 parent 1:0 classid 1:1 htb  rate ${totalRate}"Mbit";
   #创建分类 以根分类1:1为父类创建分类1:20 ，类别为htb 限速 90Mbit 最大90Mbit
   tc class add dev eth0 parent 1:1 classid 1:20 htb rate ${limitRate}"Mbit" ceil ${limitMaxRate}"Mbit" ;
   #添加公平队列 命令解释:sfq是公平队列 ，防止一个会话占用全部带宽
   tc qdisc add dev eth0 parent 1:20 handle 20: sfq perturb 10;
   #.创建分类过滤器 命令解释:以分类1:20为父类创建编号为1:20的过滤器 ,加载u32模块，指定端口
   tc filter add dev eth0 protocol ip parent 1:0 prio 1 u32 match ip dport ${limitPort} 0xffff flowid 1:20
   #输出
   echo "limitRate start..."
fi