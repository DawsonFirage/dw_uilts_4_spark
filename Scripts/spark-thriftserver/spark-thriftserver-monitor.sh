#!/bin/bash
# 脚本说明
# 此脚本用于监控Spark Thrift Server服务状态，如若服务异常关闭，则重启该服务。
# 服务地址 192.168.54.7
# 服务端口号 10086

# 创建 flag 标志，确定服务是否需要重启

# 查看Hive Metastore是否开启，若Hive Matastore不在运行则退出程序
ps -aux | grep hive | grep metastore > /dev/null
if [ $? -ne 0 ]; then
    echo "[ERROR]Hive Matastore未开启！请联系系统管理员开启服务！"
    exit 1
fi
echo "Hive Matastore正在运行中..."

#查看 Thrift Server 端口是否开启
netstat -nltp | grep 10086  > /dev/null
if [ $? -ne 0 ]; then
    echo "Thrift Server端口异常！"
    ps -aux | grep 10086 | grep spark-thriftserver
    if [ $? -ne 0 ]; then
        echo "Thrift Server服务未开启！"
        echo "正在开启Thrift Server服务..."
        sh /home/admin/script/spark-thriftserver.sh start
    else
        echo "Thrift Server服务异常！"
        echo "正在重启Thrift Server服务..."
        sh /home/admin/script/spark-thriftserver.sh stop
        sleep 2s
        sh /home/admin/script/spark-thriftserver.sh start
    fi
fi
