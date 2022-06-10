#!/bin/bash
# 脚本说明
# 此脚本用于开启和关闭Spark Thrift Server服务。
# 使用方法 sh spark-thriftserver.sh <start/stop>
# 服务地址 192.168.54.7
# 服务端口号 10086

# 防止使用未定义过的变量
#set -u

# 生效对应的环境变量
export ADMIN_PATH=/home/admin
source ${ADMIN_PATH}/bin/common/common.sh


# 需要预先配置环境变量
# SPARK_HOME=
source /home/admin/.bashrc

arg=$1
# 启动 Thrift Server
if [ "${arg}" == "start" ];then

    # 查看Hive Metastore是否开启，若Hive Matastore不在运行则退出程序
    ps -aux | grep hive | grep metastore > /dev/null
    if [ $? -ne 0 ]; then
        echo "[ERROR] Hive Matastore未开启！请联系系统管理员开启服务！"
        exit 1
    fi
    echo "Hive Matastore正在运行中..."
    echo "正在启动Thrift Server..."

    # 删除历史log数据
    rm -rf ${SPARK_HOME}/log/spark-admin-org.apache.spark.sql.hive.thriftserver.HiveThriftServer2-1-mdw.out*

    # 启动 ThriftServer
    ${SPARK_HOME}/sbin/start-thriftserver.sh \
    --master yarn \
    --deploy-mode client \
    --name spark-thriftserver \
    --driver-memory 2G \
    --executor-cores 4 \
    --executor-memory 4G \
    --num-executors 3 \
    --conf "spark.driver.maxResultSize=4G" \
    --conf "spark.hadoop.mapreduce.fileoutputcommitter.marksuccessfuljobs=false" \
    --hiveconf "hive.metastore.uris=thrift://mdw:9083" \
    --hiveconf "hive.server2.thrift.port=10086" \
    --hiveconf "hive.server2.thrift.bind.host=mdw" \
    --hiveconf "hive.exec.stagingdir=/tmp/hive/.hive-staging" \
    --hiveconf "hive.insert.into.multilevel.dirs=true" \
#    --jars ${JOB_HUTOOL_PATH},${JOB_HTTPCORE_PATH},${JOB_HTTPCLIENT_PATH},${JOB_HTTPMIME_PATH}

    # 等待 Thrift Server 开启，最多等待一分钟
    for((i=1;i<=6;i++));
    do
        echo "等待Thrift Server启动中，当前已等待`expr ${i} \* 10`秒。"
        sleep 10s
        #查看 Thrift Server 端口是否开启
        netstat -nltp | grep 10086 > /dev/null
        if [ $? -eq 0 ]; then
            break
        fi
    done

    ps -aux | grep 10086 | grep spark-thriftserver
    if [ $? -eq 0 ]; then
        echo "Thrift Server启动成功！"
    else
        echo "Thrift Server启动失败，请查看日志！！"
        cat ${SPARK_HOME}/log/spark-admin-org.apache.spark.sql.hive.thriftserver.HiveThriftServer2-1-mdw.out
    fi
    
# 关闭 Thrift Server
elif [ "${arg}" == "stop" ];then
    ps -aux | grep 10086 | grep spark-thriftserver
    if [ $? -eq 0 ]; then
        ps -aux | grep 10086 | grep spark-thriftserver | gawk '{print $2}' | xargs -n1 kill -9
    fi
else
    echo "请使用:spark-thriftserver.sh <start/stop>"
    exit 1
fi

