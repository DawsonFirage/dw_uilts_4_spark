# 1 Grouping Sets

Grouping Sets 相当于是多次 Group By 结果的 Union。

Grouping Sets 语句需要跟在 Group By 语句之后，参与 Grouping Sets 的全部列都应包含在 Group By 选中的列中，并在其后的括号内列出需要的聚合模式。示例：

```sql
SELECT a, sum(c)
FROM table1
GROUP BY a
GROUPING SETS (a)
```

这相当于不使用 GROUPING SETS 语法时的以下SQL：

``` sql
SELECT a, sum(c)
FROM table1
GROUP BY a
```

以下将列出多个示例，方便理解。

1. 分别根据字段，a，b聚合。

```sql
-- USE GROUPING SETS
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b
GROUPING SETS (a, b);

-- NOT USE GROUPING SETS
SELECT a, NULL, sum(c)
FROM table1
GROUP BY a
UNION
SELECT NULL, b, sum(c)
FROM table1
GROUP BY b;
```

2. 根据列a和b，进行聚合

```sql
-- USE GROUPING SETS
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b
GROUPING SETS ((a, b));

-- NOT USE GROUPING SETS
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b;
```

3. 根据列a，列a和b，进行聚合

```sql
-- USE GROUPING SETS
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b
GROUPING SETS (a, (a, b));

-- NOT USE GROUPING SETS
SELECT a, NULL, sum(c)
FROM table1
GROUP BY a
UNION
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b;
```

4. 根据列a和b，全部数据进行聚合

```sql
-- USE GROUPING SETS
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b
GROUPING SETS ((a, b), ());

-- NOT USE GROUPING SETS
SELECT a, b, sum(c)
FROM table1
GROUP BY a, b
UNION
SELECT NULL, NULL, sum(c)
FROM table1;
```

# 2 With Cube

Cube 会输出当前 Group By 选中的列的所有组合形式。With Cube 语句需要跟在 Group By 语句之后。

```sql
-- USE CUBE
SELECT a, b, c, sum(d)
FROM table1
GROUP BY a, b, c
WITH CUBE;

-- USE GROUPING SETS
SELECT a, b, c, sum(d)
FROM table1
GROUP BY a, b, c
GROUPING SETS ((), a, b, c, (a, b), (a, c), (b, c), (a, b, c));
```

# 3 With Rollup

Rollup 会输出当前 Group By 选中的列的自上而下的所有组合形式。With Rollup 语句需要跟在 Group By 语句之后。

```sql
-- USE CUBE
SELECT a, b, c, sum(d)
FROM table1
GROUP BY a, b, c
WITH ROLLUP;

-- USE GROUPING SETS
SELECT a, b, c, sum(d)
FROM table1
GROUP BY a, b, c
GROUPING SETS ((), a, (a, b), (a, b, c));
```

