# 1 连续问题

如下数据为蚂蚁森林中用户领取的减少碳排放量

```
id,datetime,lowcarbon
1001,2021-12-12,123
1002,2021-12-12,45
1001,2021-12-13,23
1001,2021-12-13,45
1001,2021-12-13,23
1002,2021-12-14,45
1001,2021-12-14,230
1002,2021-12-15,45
1001,2021-12-15,23
```

找出连续3天及以上减少碳排放量在100以上的用户。

## 1.1 准备数据

```sql
DROP TABLE IF EXISTS test1;
CREATE TABLE test1 (
    id STRING,
    dt STRING,
    lowcarbon STRING
)STORED AS ORC;

INSERT OVERWRITE TABLE test1 VALUES
('1001','2021-12-12','123'),
('1002','2021-12-12','45'),
('1001','2021-12-13','23'),
('1001','2021-12-13','85'),
('1001','2021-12-13','23'),
('1002','2021-12-14','45'),
('1001','2021-12-14','230'),
('1002','2021-12-15','45'),
('1001','2021-12-15','23');
```

## 1.2 解题思路（等差数列法）

**等差数列法**：两个等差数列如果等差相同，则相同位置的数据相减会得到相同的结果。
即步长为1的序列 `13,14,15,16,17`
与序列 `1,2,3,4,5`相减则得到 `12,12,12,12,12`
因此，若有序列 `13,14,15,17,18,19`，获取其中所有步长为一的序列
则可通过减去步长为1的序列 `1,2,3,4,5,6` 得到 `12,12,12,13,13,13`
可知，差值为12的为同一序列，差值为13的为另一序列。
在此问题中，即差值为12的数据连续，差值为13的数据为连续。

```sql
WITH t1 AS (
    -- 1 按照用户ID、时间字段分组，计算每个用户单日减少的碳排放量，并过滤出每日碳排放超过100的数据
    SELECT
         id
        ,dt
        ,sum(lowcarbon) AS daily_lowcarbon
    FROM test1
    GROUP BY id,dt
    HAVING daily_lowcarbon > 100
),t2 AS (
    -- 2 根据用户分组开窗，根据时间排序，获得所有日期的排序号
    SELECT
        id
        ,dt
        ,rank() OVER (PARTITION BY id ORDER BY dt) AS rn
    FROM t1
),t3 AS (
    -- 3 用日期减去排序号，连续的日期则会获得相同的值
    SELECT
         id
        ,date_sub(dt, rn) AS flag
    FROM t2
)
SELECT id, count(1) AS days_of_continuous
FROM t3
GROUP BY id, flag
HAVING days_of_continuous >= 3;
```

# 2 分组问题

如下为电商公司用户访问时间数据

```
id,ts
1001,17523641234
1001,17523641256
1002,17523641278
1001,17523641334
1002,17523641434
1001,17523641534
1001,17523641544
1002,17523641634
1001,17523641638
1001,17523641654
```

某个用户连续访问记录如果时间间隔小于60秒，则分为同一个组，结果为：

```
1001,17523641234,1
1001,17523641256,1
1001,17523641334,2
1001,17523641534,3
1001,17523641544,3
1001,17523641638,4
1001,17523641654,4
1002,17523641278,1
1002,17523641434,2
1002,17523641634,3
```

## 2.1 准备数据

```sql
DROP TABLE IF EXISTS test2;
CREATE TABLE test2 (
    id STRING,
    ts STRING
)STORED AS ORC;

INSERT OVERWRITE TABLE test2 VALUES
('1001','17523641234'),
('1001','17523641256'),
('1002','17523641278'),
('1001','17523641334'),
('1002','17523641434'),
('1001','17523641534'),
('1001','17523641544'),
('1002','17523641634'),
('1001','17523641638'),
('1001','17523641654');
```

## 2.2 解题思路（择项累加法）

**择项累加法**：根据数据的值判断应当+1 -1或是不改变，或进行其他操作。通过聚合函数将这类操作进行聚合，从而完成跨行操作。

此题中，同一用户连续访问记录时间间隔小于60秒，则分为同一个组。通过上下两组之间的差值，可知其每条记录与其上一条访问记录的时间间隔。
此处有两种情况：
1 若其间隔小于60秒，则本条数据与上一条数据是同一组，组号不必累加；
2 若其间隔大于等于60秒，则本条数据与上一条数据不是同一组，组号+1；
分析问题边界：
当记录为第一条时，此时没有上一条数据，应当给与其默认值。常见特殊值选择如null、0、最小开始时间等，不难看出，为避免特殊处理，此处选择0或者其他不大于第一条记录时间的值最好。
当记录为最后一条时，满足正常条件，无需特殊处理。

```sql
WITH t1 AS (
    -- 1 利用lag函数获取上一条数据
    SELECT
         id
        ,ts
        ,lag(ts, 1, 0) OVER (PARTITION BY id ORDER BY ts) AS lagts
    FROM test2
), t2 AS (
    -- 2 计算每两条数据之间的时间差值
    SELECT
         id
        ,ts
        ,lagts
        ,(ts - lagts) AS ts_diff
    FROM t1
)
-- 3 当ts_diff>=60时，数据为一组；反之，则开新的一组。
SELECT
     id
    ,ts
    ,lagts
    ,ts_diff
    ,sum(
        if(ts_diff < 60, 0, 1)
     ) OVER (PARTITION BY id ORDER BY ts) AS group_no
FROM t2;
```

# 3 间隔连续问题

某游戏公司记录的用户每日登录数据

```
id,dt
1001,2021-12-12
1002,2021-12-12
1001,2021-12-13
1001,2021-12-14
1001,2021-12-16
1002,2021-12-16
1001,2021-12-19
1002,2021-12-17
1001,2021-12-20
```

计算每个用户最大的连续登录天数，可以间隔一天。解释：如果一个用户在1,3,5,6登
录游戏，则视为连续6天登录。

## 3.1 准备数据

```sql
DROP TABLE IF EXISTS test3;
CREATE TABLE test3 (
    id STRING,
    dt STRING
)STORED AS ORC;

INSERT OVERWRITE TABLE test3 VALUES
('1001','2021-12-12'),
('1002','2021-12-12'),
('1001','2021-12-13'),
('1001','2021-12-14'),
('1001','2021-12-16'),
('1002','2021-12-16'),
('1001','2021-12-19'),
('1002','2021-12-17'),
('1001','2021-12-20');
```

## 3.2 解题思路一（等差数列法）（不建议）

```sql
WITH t1 AS (
    -- 1 获取排序
    SELECT
         id
        ,dt
        ,row_number() OVER (PARTITION BY id ORDER BY dt) AS rn
    FROM test3
), t2 AS (
    -- 2 由等差数列法可知，连续的数据sub_dt值相同，由于rn的步长为1
    -- 故知，当一条数据与上一条间隔一天时，sub_dt差值也为一，即sub_dt也是等差数列。
    -- 但此时数据具有重复，row_number已经不适用，应用dense_rank代替
    SELECT
         id
        ,dt
        ,date_sub(dt, rn) AS sub_dt
        ,dense_rank() OVER (PARTITION BY id ORDER BY date_sub(dt, rn)) AS rn2
    FROM t1
), t3 AS (
    -- 3 将隔一天的数据也并入连续
    SELECT
         id
        ,dt
        ,sub_dt
        ,date_sub(sub_dt, rn2) AS sub_dt2
    FROM t2
), t4 AS (
    -- 4 统计每个用户的所有最长间隔一天的连续登录天数
    SELECT
        id
        ,count(1) AS days
    FROM t3
    GROUP BY id, sub_dt2
)
-- 5 获取最大连续天数
SELECT id, max(days) AS max_days
FROM t4
GROUP BY id;
```

## 3.3 解题思路二（择项累加法）

```sql
WITH t1 AS (
    -- 1 将上一行时间数据下移
    SELECT
         id
        ,dt
        ,lag(dt, 1, '1970-01-01') OVER (PARTITION BY id ORDER BY dt) AS lag_dt
    FROM test3
), t2 AS (
    -- 2 将当前行时间减去上一行时间数据
    SELECT
         id
        ,dt
        ,lag_dt
        ,datediff(dt, lag_dt) AS diff_dt
    FROM t1
), t3 AS (
    -- 3 按照用户分组，同时按照时间排序，计算从第一行到当前行大于2的数据的总条数
    SELECT
         id
        ,dt
        ,lag_dt
        ,diff_dt
        ,sum(if(diff_dt > 2, 1, 0)) OVER (PARTITION BY id ORDER BY dt) AS group_no
    FROM t2
), t4 AS (
    -- 4 按照用户和flag分组，求最大时间减去最小时间并+1
    SELECT
         id
        ,datediff(max(dt), min(dt)) + 1 AS days_of_continuous
    FROM t3
    GROUP BY id, group_no
)
-- 5 每个用户连续登录天数的最大值
SELECT id, max(days_of_continuous) AS max_days
FROM t4
GROUP BY id;
```

# 4 日期交叉问题

如下为平台商品促销数据：字段为品牌，打折开始日期，打折结束日期

```
id,stt,edt
oppo,2021-06-05,2021-06-09
oppo,2021-06-11,2021-06-21
vivo,2021-06-05,2021-06-15
vivo,2021-06-09,2021-06-21
redmi,2021-06-05,2021-06-21
redmi,2021-06-09,2021-06-15
redmi,2021-06-17,2021-06-26
huawei,2021-06-05,2021-06-26
huawei,2021-06-09,2021-06-15
huawei,2021-06-17,2021-06-21
```

计算每个品牌总的打折销售天数，注意其中的交叉日期，比如vivo品牌，第一次活动时间为2021-06-05到2021-06-15，第二次活动时间为2021-06-09到2021-06-21其中9号到15号为重复天数，只统计一次。即vivo总打折天数为2021-06-05到2021-06-21共计17天。

## 4.1 准备数据

```sql
DROP TABLE IF EXISTS test4;
CREATE TABLE test4(
     id STRING
    ,stt STRING
    ,edt STRING
)STORED AS ORC;

INSERT OVERWRITE TABLE test4 VALUES
('oppo','2021-06-05','2021-06-09'),
('oppo','2021-06-11','2021-06-21'),
('vivo','2021-06-05','2021-06-15'),
('vivo','2021-06-09','2021-06-21'),
('redmi','2021-06-05','2021-06-21'),
('redmi','2021-06-09','2021-06-15'),
('redmi','2021-06-17','2021-06-26'),
('huawei','2021-06-05','2021-06-26'),
('huawei','2021-06-09','2021-06-15'),
('huawei','2021-06-17','2021-06-21');
```

## 4.2 解题思路一（流式数据法）

**流式数据法**：将一条数据转化为多条有顺序的数据，其中的每种时间的区别转化为状态字段做区分。转化为流式数据后，一般通过累加状态的方式来判断当前的数据状态，从而解决问题。

在该问题中，因为数据间的开始时间与结束时间存在重复部分。则可以通过将其转化为流式数据来判断当前活动的状态是开始还是结束。从而可得到每次打折活动的实际开始与结束时间，从而算的总时间。

```sql
WITH t1 AS (
    -- 1 将数据转化为流式数据，将开始打折记录为1，结束打折记录为-1
    SELECT id, stt AS dt, 1 AS state FROM test4
    UNION ALL
    SELECT id, edt AS dt, -1 AS state FROM test4
), t2 AS (
    -- 2 将同id的数据状态根据dt排序累加，可知当累加状态值为0时，表示进入一次打折活动的彻底结束
    SELECT
         id
        ,dt
        ,state
        ,sum(state) OVER (PARTITION BY id ORDER BY dt) AS sum_state_until_current
    FROM t1
), t3 AS (
    -- 3 当上一行的sum_state_until_current=0时，表示该数据为一个新的打折活动的开始。
    SELECT
         id
        ,dt
        ,state
        ,sum_state_until_current
        ,lag(sum_state_until_current, 1, 0) OVER (PARTITION BY id ORDER BY dt) AS lag_sum_state_until_current
    FROM t2
), t4 AS (
    -- 4 一次打折活动视为一个分组，利用择项累加法分组。
    SELECT
         id
        ,dt
        ,state
        ,sum_state_until_current
        ,lag_sum_state_until_current
        ,sum(if(lag_sum_state_until_current = 0, 1, 0)) OVER (PARTITION BY id ORDER BY dt) AS group_no
    FROM t3
), t5 AS (
    -- 5 同一组内，最小的dt即为打折活动开始时间，最大的dt即为打折活动结束时间
    SELECT
         id
        ,min(dt) AS stt
        ,max(dt) AS edt
        ,datediff(max(dt), min(dt)) + 1 AS num_of_days
    FROM t4
    GROUP BY id, group_no
)
-- 6 同一id，所有组的打折天数之和，则为每个品牌的总打折天数
SELECT id, sum(num_of_days) AS total_num_of_days
FROM t5
GROUP BY id;
```

## 4.3 解题思路二（时间段去重法）

**时间段去重法**：问题主要复杂点在于，时间会重复，故若使得每段数据的包含的时间段不重复，则统计后的总时间则为不重复的时间。将所有的数据按开始时间排序，要使每条数据的时间段不重复，则每条数据的开始时间应小于其之前数据的最晚结束时间，若不满足此条件，则应将其开始时间替换为之前数据的最晚结束时间的后一天。

```sql
WITH t1 AS (
    -- 1 获取当前行以前的数据中最大的edt
    SELECT
         id
        ,stt
        ,edt
        ,max(edt) OVER (PARTITION BY id ORDER BY stt ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING) AS max_edt
    FROM test4
), t2 AS (
    -- 2 比较开始时间与移动下米的数据，如果开始时间大，则不需要操作，
    -- 反之则需要将移动下来的数据加一替换当前行的开始时可
    -- 如果是第一行数据，maxEDT为nul1,则不需要操作
    SELECT
         id
        ,stt
        ,edt
        ,CASE
            WHEN max_edt IS NULL THEN stt
            WHEN stt > max_edt THEN stt
            ELSE date_add(max_edt, 1)
         END AS new_stt
    FROM t1
), t3 AS (
    -- 3 将每行数据中的结束时间减去开始日期
    SELECT
         id
        ,stt
        ,edt
        ,new_stt
        ,datediff(edt, new_stt) + 1 AS num_of_days
    FROM t2
)
-- 4 按照品牌分组，计算每条日期的总和。若天数为负，则说明该条数据被其他数据完全包含，不计入统计。
SELECT
     id
    ,sum(if(num_of_days > 0, num_of_days, 0)) AS total_num_of_days
FROM t3
GROUP BY id
```

# 5 同时在线问题

如下为某直播平台主播开播及关播时间，根据该数据计算出平台最高峰同时在线的主播人数。

```
id,stt,edt
1001,2021-06-14 12:12:12,2021-06-14 18:12:12
1003,2021-06-14 13:12:12,2021-06-14 16:12:12
1004,2021-06-14 13:15:12,2021-06-14 20:12:12
1902,2021-06-14 15:12:12,2021-06-14 16:12:12
1005,2021-06-14 15:18:12,2021-06-14 20:12:12
1001,2021-06-14 20:12:12,2021-06-14 23:12:12
1006,2021-06-14 21:12:12,2021-06-14 23:15:12
1007,2021-06-14 22:12:12,2021-06-14 23:10:12
```

## 5.1 准备数据

```sql
DROP TABLE IF EXISTS test5;
CREATE TABLE test5(
     id STRING
    ,stt STRING
    ,edt STRING
)STORED AS ORC;

INSERT OVERWRITE TABLE test5 VALUES
('1001','2021-06-14 12:12:12','2021-06-14 18:12:12'),
('1003','2021-06-14 13:12:12','2021-06-14 16:12:12'),
('1004','2021-06-14 13:15:12','2021-06-14 20:12:12'),
('1902','2021-06-14 15:12:12','2021-06-14 16:12:12'),
('1005','2021-06-14 15:18:12','2021-06-14 20:12:12'),
('1001','2021-06-14 20:12:12','2021-06-14 23:12:12'),
('1006','2021-06-14 21:12:12','2021-06-14 23:15:12'),
('1007','2021-06-14 22:12:12','2021-06-14 23:10:12');
```

## 5.2 解题思路（流式数据法）

```sql
WITH t1 AS (
    -- 1 将数据转化为流式数据。
    -- 因为要求同时在线的主播数量，即当主播上线时，人数+1；主播下线时，人数减一
    -- 故令开始记录为1，结束记录为-1
    SELECT id, stt AS dt, 1 AS state FROM test5
    UNION ALL
    SELECT id, edt AS dt, -1 AS state FROM test5
), t2 AS (
    -- 2 每条记录发生时同时在线的主播数量，即为当前行至其之前所有记录状态值的总和
    SELECT
         id
        ,dt
        ,sum(state) OVER (ORDER BY dt) AS num_of_anchor
    FROM t1
)
-- 3 最大同时在线主播数
SELECT max(num_of_anchor) AS max_num_of_anchor
FROM t2
```

