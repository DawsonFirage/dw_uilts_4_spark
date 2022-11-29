#!/usr/bin/env bash

set -u

function read_dir() {
  declare start_path="$1"
  now_files=$(ls "${start_path}")
  echo "start_path=${start_path}"

  for file in ${now_files}; do
    if [ -d "${start_path}/${file}" ]
      then
        echo "file=${file}"
        read_dir "${start_path}/${file}"
        # 对文件夹的操作
        # ...
      else
        echo "unfile=${file}"
        # 对非文件夹的操作
        # ...
    fi
  done
}

read_dir "$1"