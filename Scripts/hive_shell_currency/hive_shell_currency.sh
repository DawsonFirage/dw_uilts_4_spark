#!/usr/bin/env bash
# 数仓执行脚本 V1
#
# file: .key
# e.g
# export KEY_NAME=value # 可以使用Shell数据结构
# RUN_SUB_PROJECT_DIR   # Shell目录 默认：/
FLAG_PATH_PREFIX="/etl/success_flg"      # 前缀文件，默认：/jkd_dw/success_flg
# HIVE_HOST, HIVE_PORT, HIVE_DB, HIVE_USER, HIVE_PASS
HIVE_HOST=mdw
HIVE_PORT=10086
HIVE_DB=sxmhww_dw
HIVE_USER=admin
HIVE_PASS=admin

# config env for scripts
# scripts of time
#partition_day=`date +%Y%m%d`
systoday=`date +%Y%m%d`
partition_day=`date -d "1 day ago ${systoday}" +%Y%m%d`
partition_month=`date -d "1 month ago $(date +%Y%m15)" +%Y%m`
partition_year=${partition_day:0:4}
today_day=`date +%Y-%m-%d`
# shift
sub_project=$1
echo "sub_project="$sub_project
dw_dir="/home/admin/${HIVE_DB}"
echo "dw_dir="$dw_dir
arr=(${sub_project//_/ })
sub_project_base_dir=$dw_dir/${arr[0]}
# dir
#sub_project_base_dir=${RUN_SUB_PROJECT_DIR:-$PWD}  # 寻找执行路径
#sub_project_base_dir=$(pwd)  # 寻找执行路径
echo "sub_project_base_dir="$sub_project_base_dir
# config env for scripts end

echo "systoday="$systoday
echo "partition_day="$partition_day
echo "partition_year="$partition_year
echo "partition_month="$partition_month
echo "today_day="$today_day
# include sub projects key
[ -z "${sub_project}" ] && {
	echo "[BASE] 为获取到项目参数，请检查输入！"
	exit 1
} || {
	sub_project_dir=${sub_project_base_dir}/${sub_project}
	[ ! -d ${sub_project_dir} ] && {
		echo "[BASE] 未找到项目"
		exit 1
	}

	# Import key
	# Base Key
	base_key_file=${dw_dir}/run/base.key
	[ -f ${base_key_file} ] && source ${base_key_file}
	# Sub key
	# config.key -> DEP_JOB       # 依赖文件，默认: ""
	sub_key_file=${sub_project_base_dir}/${sub_project}/config.key
	[ -f ${sub_key_file} ] && source ${sub_key_file}

}

# scripts file and flag file
job_name=${sub_project}
# exec sql file
job_pathdir=${sub_project_dir}/${job_name}.sql
# flag_path_prefix=${FLAG_PATH_PREFIX:-/dw/success_flg}
flag_path_prefix=${FLAG_PATH_PREFIX}/${arr[0]}/$partition_day
#定义依赖项的成功标记位
dependent_path_prefix=${DEP_JOB:-}
#定义成功文件名称
success_path=${flag_path_prefix}/${job_name}.log

echo "job_name="$job_name
echo "job_pathdir="$job_pathdir
echo "success_path="$success_path

#开始检查依赖
for i in ${DEP_JOB[@]}
do
echo "dependent_path=${i}.log"
	hdfs dfs -ls "${i}.log"
	if [[ $? -ne 0 ]];then
		echo "依赖不满足 $?"
		exit 1
	fi
done
echo "依赖满足，继续加载"
#创建日期目录
hdfs dfs -mkdir $flag_path_prefix
#删除目录
hdfs dfs -rm -r $success_path

#记录开始加载时间
start=`date '+%Y-%m-%d %H:%M:%S'`
startTime=`date +%H:%M:%S`
echo "开始加载"$start

#执行sql
beeline \
	-u "jdbc:hive2://${HIVE_HOST:-mdw}:${HIVE_PORT:-10086}/${HIVE_DB:-dw};" \
	-n ${HIVE_USER:-admin} \
	-p ${HIVE_PASS:-admin} \
	-hiveconf partition_day=$partition_day \
	-hiveconf partition_year=$partition_year \
	-hiveconf partition_month=$partition_month \
	-f $job_pathdir

#检查sql脚本报错情况
if [[ $? -ne 0 ]];then
    echo "ERROR：$?"
    exit 1
fi
echo "sql脚本加载成功"

#创建结果目录
hdfs dfs -mkdir $success_path
if [[ $? -ne 0 ]];then
    echo "ERROR：$?"
    exit 1
fi
echo "成功标记目录创建成功"

#记录结束时间
end=`date '+%Y-%m-%d %H:%M:%S'`
echo "结束加载"$end
endTime=`date  +%H:%M:%S`

sT=`date +%s -d$startTime`
eT=`date +%s -d$endTime`

#计算脚本耗时
let useTime=`expr $eT - $sT`


echo "耗时情况：""
Run `basename $0` ok !
	startTime = $startTime
	  endTime = $endTime
	  useTime = $useTime(s) !"
