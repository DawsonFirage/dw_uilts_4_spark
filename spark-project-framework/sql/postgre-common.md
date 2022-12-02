# 0 说明

postgreSQL的一些通用操作汇总

# 1 整库导入导出

**导出**

```sh
pg_dump -h localhost -p 5432 -F p -b -v -f db_name.sql db_name
```

**导入**

```sh
psql -h localhost -p 5432 -f /tmp/db_name.sql db_name
```

