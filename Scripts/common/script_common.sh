#!/bin/bash

# 获取当前文件实际路径（非软连接）
file_path="$(dirname "$(readlink -f "$0")")"