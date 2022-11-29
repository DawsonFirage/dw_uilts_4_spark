#!/usr/bin/env bash

set -u

function read_dir() {
  declare start_path="$1"
  now_files=$(ls "${start_path}")
  echo "start_path=${start_path}"

  for file in ${now_files}; do
    if [ -d "${start_path}/${file}" ]
      then
        # 对文件夹的操作
        echo "now in file: ${file}"
        read_dir "${start_path}/${file}"
        # ...
      else
        # 对非文件夹的操作
        echo "dos2unix ${file}..."
        # ...
        dos2unix ${file}
    fi
  done
}

read_dir "$1"