#!/usr/bin/env bash

# 下载
# -p 保留原文件信息（修改时间、访问权限等）
# -f 覆盖已有文件
hdfs dfs -get 'hdfs path...' \
'local path...'

# 上传
# -p 递归拷贝
# -f 覆盖已有文件
hdfs dfs -put -p 'local path...' \
'hdfs path...'

# hive 重新读取表数据
hive -e 'msck repair table ywmt_dw.dws_work_order_info_all_partition_dd_f'
