#!/bin/bash

# 清空文件（覆盖）
cat /dev/null >target_file

# 写入多行数据（覆盖）
cat>target_file<<EOF
这是一个由shell创建的文件
this is a file created by shell.
we want to make a good world.
EOF