#!/usr/bin/env bash

# 清空文件（覆盖）
cat /dev/null > target_file

# 写入多行数据（覆盖）
cat > target_file << EOF
这是一个由shell创建的文件
this is a file created by shell.
we want to make a good world.
EOF

# 获取当前文件实际路径（非软连接）
file_path="$(dirname "$(readlink -f "$0")")"

# 修改文件所属用户及组
chown -R user:group file

# 设置脚本为可直接执行
chmod -R 755 script_file.sh

# 服务器间文件传输
scp -r 'local_path' 'host':'path2'