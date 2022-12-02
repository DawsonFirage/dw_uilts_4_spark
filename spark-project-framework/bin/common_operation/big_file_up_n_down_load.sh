# 将 mydata.zip 文件拆分为最大3G，子文件名称前缀为 mydata_20221202.zip.
cat mydata.zip | split -b 3G - mydata_20221202.zip.

# 下载全部子文件
sz mydata_20221202.zip.*

# windows cmd 合并
copy /B mydata_20200302.zip.* mydata.zip
# linux shell 合并
cat mydata_20200302.zip.* > mydata.zip