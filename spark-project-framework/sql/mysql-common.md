# 0 说明

mysql的一些通用操作汇总

# 1 整库导入导出

结构及数据

**导出**

导出根据mysql自带的mysqldump命令，将数据库导出为sql文件

```sh
# 导出指定数据库全部（-R包括函数和存储过程 -E事件）
mysqldump -S'/tmp/mysqld.sock' -u'user' -p'password' -R -E 数据库名 > 数据库名.sql
```

**导入**

```sh
mysql -u'user' -p'password'
    DROP DATABASE IF EXISTS house;
    CREATE DATABASE house;
    use house;
    source /opt/house.sql
```

# 2 导出单张表

```sh
mysqldump -u 'root' -p 'passwd' dbname \
test1 \
test2 \
test3 \
> db.sql
```

