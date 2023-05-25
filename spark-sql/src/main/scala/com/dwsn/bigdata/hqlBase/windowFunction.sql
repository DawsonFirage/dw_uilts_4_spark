USE d_test;

-- 准备数据
DROP TABLE IF EXISTS business;
CREATE TABLE business (
    name STRING,
    orderdate STRING,
    cost STRING
) STORED AS ORC;
INSERT OVERWRITE TABLE business VALUES
('jack', '2017-01-01', '10'),
('tony', '2017-01-02', '15'),
('jack', '2017-02-03', '23'),
('tony', '2017-01-04', '29'),
('jack', '2017-01-05', '46'),
('jack', '2017-04-06', '42'),
('tony', '2017-01-07', '50'),
('jack', '2017-01-08', '55'),
('mart', '2017-04-08', '62'),
('mart', '2017-04-09', '68'),
('nei1', '2017-05-10', '12'),
('mart', '2017-04-11', '75'),
('nei1', '2017-06-12', '80'),
('mart', '2017-04-13', '94');

SELECT * FROM business;

-- OVER()::指定分析函数工作的数据窗口大小，这个数据窗口大小可能会随着行的变而变化。
-- CURRENT ROW: 当前行
-- n PRECEDING: 往前n行数据
-- n FOLLOWING: 往后n行数据
-- UNBOUNDED::起点
--     UNBOUNDED PRECEDING: 表示从前面的起点
--     UNBOUNDED FOLLOWING: 表示到后面的终点
-- LAG(col, n, default_val):往前第n行数据
-- LEAD(col, n, default_val):往后第n行数据u
-- NTILE(n):把有序窗口的行分发到指定数据的组中，各个组有编号，编号从1开始，对于每一行，NTLE返回此行所属的组的编号。注意：n必须为it类型。

---------------------------------------------------------------
-- START 1 查询在2017年4月份购买过的顾客及总人数
SELECT
     name
    ,count(1) OVER() AS cnt
FROM business
WHERE substring(orderdate, 1, 7) = '2017-04'
GROUP BY name;
-- 由此可见：
-- over可根据当前SQL的查询结果框定数据范围。
-- 如果不看over所在的查询。在GROUP BY name分组后，数据只有两条，即：
-- +------+
-- |  name|
-- +------+
-- |  jack|
-- |  mart|
-- +------+
-- 而由查询结果（如下所示）可知，over所在的查询发生在了除over以外的查询之后。
-- +------+-----+
-- |  name|  cnt|
-- +------+-----+
-- |  mart|    2|
-- |  jack|    2|
-- +------+-----+
-- 也就是说，over查询的数据范围，是查询语句中除去over以外的sql执行后的数据范围。
-- END 1
---------------------------------------------------------------

-- 2 查询顾客的购买明细及月购买总额
SELECT
     name
    ,orderdate
    ,cost
    ,month(orderdate) AS order_month
    ,sum(cost) OVER(PARTITION BY month(orderdate)) AS month_cost
FROM business;

---------------------------------------------------------------
-- START 3 上述的场景，将每个顾客的cost按照日期进行累加
SELECT
     name
    ,orderdate
    ,cost
    ,sum(cost) OVER(PARTITION BY name ORDER BY orderdate) AS sum_cost_until_now
    -- 效果相同
    ,sum(cost) OVER(PARTITION BY name ORDER BY orderdate ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW ) AS sum_cost_until_now_2
FROM business;
-- 可见，ORDER BY不仅是排序，也能够更进一步的控制数据范围。
-- END 3
---------------------------------------------------------------

-- 4 查询每个顾客上次的购买时间
SELECT
     name
    ,orderdate
    ,cost
    ,lag(orderdate) OVER (PARTITION BY name ORDER BY orderdate) AS last_orderdate
    ,lag(orderdate, 2) OVER (PARTITION BY name ORDER BY orderdate) AS last_orderdate_2
    ,lag(orderdate, 1, '1970 -01-01') OVER (PARTITION BY name ORDER BY orderdate) AS last_orderdate_3
FROM business;

-- 5 查询前20%时间的订单信息
SELECT *
FROM (
    SELECT
         name
        ,orderdate
        ,cost
        ,ntile(5) OVER (ORDER BY orderdate) group_id
    FROM business
    ) AS t
WHERE group_id = 1;