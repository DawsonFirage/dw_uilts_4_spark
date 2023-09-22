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
source "${ADMIN_PATH}/.bashrc"
#source "${ADMIN_PATH}/${DW_DB}/bin/common/common.sh"

# 需要预先配置环境变量
# SPARK_HOME=
echo "SPARK_HOME=${SPARK_HOME}"
# 设置hive metastore
HIVE_METASTORE_URIS="thrift://mdw:9083"
# 设置Spark thrift-server的host和port
#如果要更改thrift-server的port，记得同步修改spark-thriftserver-monitor.sh中的port。
THRIFT_SERVER_HOST="mdw"
THRIFT_SERVER_PORT="10086"
# 设置thrift-server资源
DRIVER_MEMORY="2G"
EXECUTOR_CORES="2"
EXECUTOR_MEMORY="4G"
NUM_EXECUTORS="3"
DRIVER_MAX_RESULT_SIZE="1G"

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
    rm -rf ${SPARK_HOME}/logs/spark-admin-org.apache.spark.sql.hive.thriftserver.HiveThriftServer2-1-mdw.out*

    # 启动 ThriftServer
    ${SPARK_HOME}/sbin/start-thriftserver.sh \
    --master yarn \
    --deploy-mode client \
    --name spark-thriftserver \
    --driver-memory ${DRIVER_MEMORY} \
    --executor-cores ${EXECUTOR_CORES} \
    --executor-memory ${EXECUTOR_MEMORY} \
    --num-executors ${NUM_EXECUTORS} \
    --conf "spark.driver.maxResultSize=${DRIVER_MAX_RESULT_SIZE}" \
    --conf "spark.hadoop.mapreduce.fileoutputcommitter.marksuccessfuljobs=false" \
    --hiveconf "hive.metastore.uris=${HIVE_METASTORE_URIS}" \
    --hiveconf "hive.server2.thrift.bind.host=${THRIFT_SERVER_HOST}" \
    --hiveconf "hive.server2.thrift.port=${THRIFT_SERVER_PORT}" \
    --hiveconf "hive.exec.stagingdir=/tmp/hive/.hive-staging" \
    --hiveconf "hive.insert.into.multilevel.dirs=true" \
#    --jars ${JOB_HUTOOL_PATH},${JOB_HTTPCORE_PATH},${JOB_HTTPCLIENT_PATH},${JOB_HTTPMIME_PATH}

    # 等待 Thrift Server 开启，最多等待一分钟
    for((i=1;i<=6;i++));
    do
        echo "等待Thrift Server启动中，当前已等待`expr ${i} \* 10`秒。"
        sleep 10s
        #查看 Thrift Server 端口是否开启
        netstat -nltp | grep ${THRIFT_SERVER_PORT} > /dev/null
        if [ $? -eq 0 ]; then
            break
        fi
    done

    ps -aux | grep ${THRIFT_SERVER_PORT} | grep spark-thriftserver
    if [ $? -eq 0 ]; then
        echo "Thrift Server启动成功！"
    else
        echo "[ERROR] Thrift Server启动失败，请查看日志！！"
        cat ${SPARK_HOME}/logs/spark-admin-org.apache.spark.sql.hive.thriftserver.HiveThriftServer2-1-mdw.out
    fi
    
# 关闭 Thrift Server
elif [ "${arg}" == "stop" ];then
    ps -aux | grep ${THRIFT_SERVER_PORT} | grep spark-thriftserver
    if [ $? -eq 0 ]; then
        ps -aux | grep ${THRIFT_SERVER_PORT} | grep spark-thriftserver | gawk '{print $2}' | xargs -n1 kill -15
    else
        echo "[ERROR] 未发现Thrift Server进程！"
    fi

    # kill 后等待程序关闭，端口释放
    sleep 5s
    ps -aux | grep ${THRIFT_SERVER_PORT} | grep spark-thriftserver
    if [ $? -eq 0 ]; then
        echo "Thrift Server关闭成功！"
    else
        echo "[ERROR] Thrift Server关闭失败！请查看日志！！"
    fi
else
    echo "请使用:spark-thriftserver.sh <start/stop>"
    exit 1
fi

