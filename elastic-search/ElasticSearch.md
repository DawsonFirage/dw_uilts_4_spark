# 0 Elasticsearch 和 Kibana

只针对es 8.x版本。

选用 es 和 kibana 版本为**8.5.1**。

# 1 ES 安装

## 1.1 创建 es 用户

由于es的安全限制，不支持root用户启动，因此需要首先为es创建用户。

```shell
[root@test ~]# groupadd es 
[root@test ~]# useradd es -g es -p es
```

## 1.2 es 解压缩

```shell
[root@test ~]# tar -zxvf elasticsearch-8.5.1-linux-x86_64.tar.gz -C /opt/apps/
# 将es所属用户改为es
[root@test ~]# chown es:es -R /opt/apps/elasticsearch-8.5.1/

# 登录es用户
[root@test ~]# su - es
[es@test ~]$ cd /opt/apps/elasticsearch-8.5.1/

# 创建数据文件目录
[es@test elasticsearch-8.5.1]$ mkdir data

# 创建证书目录
[es@test elasticsearch-8.5.1]$ mkdir config/certs
```

## 1.3 设置安全证书

es采用安全认证方式访问。

### 1.3.1 设置集群多节点通信秘钥

```shell
[es@test elasticsearch-8.5.1]$ cd /opt/apps/elasticsearch-8.5.1

# 签发ca证书，过程中需要按两次回车键
[es@test elasticsearch-8.5.1]$ bin/elasticsearch-certutil ca

# 用ca证书签发节点证书，过程中需要按三次回车键
[es@test elasticsearch-8.5.1]$ bin/elasticsearch-certutil cert --ca elastic-stack-ca.p12

# 将生成的证书文件移动到config/certs
[es@test elasticsearch-8.5.1]$ mv elastic-stack-ca.p12 elastic-certificates.p12 config/certs
```

### 1.3.2 设置集群多节点HTTP证书

```shell
[es@test elasticsearch-8.5.1]$ cd /opt/apps/elasticsearch-8.5.1
# 签发Https证书，期间会有多个问题，按如下输入（没有内容即直接回车）
[es@test elasticsearch-8.5.1]$ bin/elasticsearch-certutil http
# 问题&输入内容
Generate a CSR? [y/N]n

Use an existing CA? [y/N]y

CA Path: certs/elastic-stack-ca.p12

Password for elastic-stack-ca.p12:

For how long should your certificate be valid? [5y] 5y

Generate a certificate per node? [y/N]n

Enter all the hostnames that you need, one per line.
When you are done, press <ENTER> once more to move on to the next step.
# 输入es集群的所有主机名，我这边是单点因此只有一个，多个则每输入一台主机名后按一次回车。输入所有主机名后按两次回车确认。
test

Is this correct [Y/n]y

Enter all the IP addresses that you need, one per line.
When you are done, press <ENTER> once more to move on to the next step.
# 输入es集群的所有IP地址。同上。
192.168.37.200

Is this correct [Y/n]y

Do you wish to change any of these options? [y/N]n

Provide a password for the "http.p12" file:  [<ENTER> for none]

What filename should be used for the output zip file? [/opt/apps/elasticsearch-8.5.1/elasticsearch-ssl-http.zip]
```

操作完成后，证书的压缩文件会生成在最后一步的目录下，我们将它解压缩到config/certs下。

```shell
[es@test elasticsearch-8.5.1]$ unzip elasticsearch-ssl-http.zip -d config/certs

[es@test elasticsearch-8.5.1]$ cd config/certs
[es@test certs]$ mv elasticsearch/http.p12 kibana/elasticsearch-ca.pem ./
[es@test certs]$ rm -rf elasticsearch kibana
```

## 1.4 修改es配置

```shell
[es@test elasticsearch-8.5.1]$ cd /opt/apps/elasticsearch-8.5.1
[es@test elasticsearch-8.5.1]$ vim config/elasticsearch.yml
```

在配置文件中修改下面的几个配置项：

```yml
# 集群名称 一个集群的节点该参数要保持一致
cluster.name: my-application
# 节点名称 一个集群的节点该参数要*互相区别*
node.name: node-1
# 数据、日志保存目录
path.data: /opt/apps/elasticsearch-8.5.1/data
path.logs: /opt/apps/elasticsearch-8.5.1/logs
# 启动时锁定内存
bootstrap.memory_lock: false
# 网络访问节点
network.host: test
# 网络访问端口
http.port: 9200
# 初始节点
discovery.seed_hosts: ["test"]

# 安全认证
xpack.security.enabled: true

xpack.security.enrollment.enabled: true

# Enable encryption for HTTP API client connections, such as Kibana, Logstash, and Agents
xpack.security.http.ssl:
  enabled: true
  keystore.path: /opt/apps/elasticsearch-8.5.1/config/certs/http.p12
  truststore.path: /opt/apps/elasticsearch-8.5.1/config/certs/http.p12

# Enable encryption and mutual authentication between cluster nodes
xpack.security.transport.ssl:
  enabled: true
  verification_mode: certificate
  keystore.path: /opt/apps/elasticsearch-8.5.1/config/certs/elastic-certificates.p12
  truststore.path: /opt/apps/elasticsearch-8.5.1/config/certs/elastic-certificates.p12
# Create a new cluster with the current node only
# Additional nodes can still join the cluster later
# 注意：此处为上面配置的es节点名称
cluster.initial_master_nodes: ["node-1"]

http.host: [_local_, _site_]
ingest.geoip.downloader.enabled: false
xpack.security.http.ssl.client_authentication: none
```

**NOTE**

1. `cluster.name`和`discovery.seed_hosts`用于确定集群，统一集群内的节点中这两项配置需要相同
2. `node.name`和`network.host`为单个节点的配置，每个节点都需要配置为本节点的参数值

## 1.5 Linux系统配置

运行es需要对linux系统进行一些配置，需要在root用户下执行

```shell
# 退回root用户
[es@test kibana-8.5.1]$ exit

# 每个进程可以打开的文件数的限制
[root@test ~]# vim /etc/security/limits.conf
# 在文件末尾添加以下内容
es soft nofile 65536
es hard nofile 65536

# 每个进程可以打开的文件数的限制
[root@test ~]# vim /etc/security/limits.d/20-nproc.conf
# 在文件末尾添加以下内容
es soft nofile 65536
es hard nofile 65536

####################################################
# 对于上面修改的两个文件。es表示是对es用户的配置。
# 原文件中的配置都是*，表示是Linux所有用户。
####################################################

# 一个进程可以拥有的VMA（虚拟内存区域）的数量，默认值为65536
[root@test ~]# vim /etc/sysctl.conf
# 在文件末尾添加以下内容
vm.max_map_count=655360

# 重新加载Linux内核参数
[root@test ~]# sysctl -p
# 重启服务器以生效所有配置
```

## 1.6 启动es

```shell
[root@test ~]# su - es
# 启动es
[es@test ~]$ cd /opt/apps/elasticsearch-8.5.1/
[es@test elasticsearch-8.5.1]$ bin/elasticsearch
```

es默认前台运行，开启另一个会话窗口，执行如下命令。

```shell
[es@test elasticsearch-8.5.1]$ jps
# es启动成功后，后台会运行这两个程序
# CliToolLauncher
# org.elasticsearch.bootstrap.Elasticsearch
[es@test elasticsearch-8.5.1]$ netstat -nltp
# **es 端口：**
#|---9200： http协议访问端口
#|---9300： 集群间通信端口
```

> 后台启动es。（第一次启动es时会打印密码等项，因此第一次启动时不要采用后台启动的方式。）

```shell
[es@test elasticsearch-8.5.1]$ nohup bin/elasticsearch >logs/es.log 2>&1 &
```

浏览器打开 https://test:9200 (注意是https)。提示有安全问题直接跳过。输入用户名elastic，和密码登录。

登录成功会返回集群状态如下所示。

```json
{
  "name" : "node-1",
  "cluster_name" : "my-application",
  "cluster_uuid" : "9Xts-ilfTM2OvlucKcHgSA",
  "version" : {
    "number" : "8.5.1",
    "build_flavor" : "default",
    "build_type" : "tar",
    "build_hash" : "c1310c45fc534583afe2c1c03046491efba2bba2",
    "build_date" : "2022-11-09T21:02:20.169855900Z",
    "build_snapshot" : false,
    "lucene_version" : "9.4.1",
    "minimum_wire_compatibility_version" : "7.17.0",
    "minimum_index_compatibility_version" : "7.0.0"
  },
  "tagline" : "You Know, for Search"
}
```

## 1.7 修改es密码

es第一次启动成功时，会自动生成密码，并打印到窗口需要将其保存下来。再次启动时则不会再提供密码，如果要修改密码，要在es启动状态下执行以下指令。

```shell
bin/elasticsearch-reset-password -u elastic
```

## 1.8 配置集群的其他节点

从节点操作基本与主节点相同。其中差异有两点需要注意。

1. 安全证书要从主节点拷贝。不要在从节点上再生成新的安全证书。
2. 配置文件`config/elasticsearch.yml`中，需要将如下配置修改为当前从节点的值。

```yml
# 节点名称 一个集群的节点该参数要*互相区别*
node.name: node-2
# 网络访问节点
network.host: test2
```

> 建议从节点的配置方法
>
> 1. 将主节点安装好的es分发到各个从节点
> 2. 各从节点配置 Linux 系统配置
> 3. 修改各从节点的 config/elasticsearch.yml

# 2 Kibana 安装

## 2.1 Kibana 解压缩

```shell
[root@test ~]# tar -zxvf kibana-8.5.1-linux-x86_64.tar.gz -C /opt/apps/

# 将 kibana 所属用户改为es
[root@test ~]# chown es:es -R /opt/apps/kibana-8.5.1/

# 登录es用户
[root@test ~]# su - es
```

## 2.2 给Kibana生成证书文件

```shell
[es@test ~]$ cd /opt/apps/elasticsearch-8.5.1
# 回车即可
[es@test elasticsearch-8.5.1]$ bin/elasticsearch-certutil csr -name kibana -dns test
# 会在当前目录下生成 csr-bundle.zip
# 解压到 *kibana* 的 config 目录下
[es@test elasticsearch-8.5.1]$ unzip csr-bundle.zip -d /opt/apps/kibana-8.5.1/config
# 将解压缩后的证书文件移动到 *kibana* 的 config 目录下
[es@test elasticsearch-8.5.1]$ cd /opt/apps/kibana-8.5.1/config/
[es@test config]$ mv kibana/* ./
[es@test config]$ rm -rf kibana
# 生成crt证书文件
[es@test config]$ openssl x509 -req -in kibana.csr -signkey kibana.key -out kibana.crt
```

## 2.3 为 kibana 创建es用户

```shell
[es@test ~]$ cd /opt/apps/elasticsearch-8.5.1/
[es@test elasticsearch-8.5.1]$ bin/elasticsearch-reset-password -u kibana
# 保存这个密码用于下一步修改配置文件
```

## 2.3 修改Kibana配置

```shell
[es@test ~]$ cd /opt/apps/kibana-8.5.1/
[es@test kibana-8.5.1]$ vim config/kibana.yml
```

在配置文件中修改下面的几个配置项：

```yml
# Kibana服务的端口和地址
server.port: 5601
server.host: "test"

# kibana 安全认证
server.ssl.enabled: true
server.ssl.certificate: /opt/apps/kibana-8.5.1/config/kibana.crt
server.ssl.key: /opt/apps/kibana-8.5.1/config/kibana.key

# es服务主机地址 注意协议是https
elasticsearch.hosts: ["https://test:9200"]

# 访问 es 的账号密码
elasticsearch.username: "kibana"
elasticsearch.password: "*0Wodr0F=GAKlWqlL0aq"

# es安全证书 及 验证模式
elasticsearch.ssl.certificateAuthorities: [ "/opt/apps/elasticsearch-8.5.1/config/certs/elasticsearch-ca.pem" ]
elasticsearch.ssl.verificationMode: none

# 国际化 - 中文
i18n.locale: "zh-CN"
```

## 2.3 启动 Kibana

```shell
# 启动Kibana
[es@test elasticsearch-8.5.1]$ cd /opt/apps/kibana-8.5.1/
[es@test kibana-8.5.1]$ nohup bin/kibana >logs/kibana.log 2>&1 &

# 查看Kibana是否启动成功
[es@test kibana-8.5.1]$ netstat -nltp | grep 5601
```

# 3 ES 数据结构

| 名称    | 翻译 | 对应表的概念 |
| ------- | ---- | ------------ |
| Index   | 索引 | 表           |
| Mapping | 映射 | 元数据、约束 |
| Doc     | 文档 | 数据->每行   |

**NOTE**

1. 关于Type、\_doc、source的区别。8.0版本彻底删除了Type的概念，每个索引只有一个Type，即\_doc，数据存储结构中，文档实际存储在source下。

## 3.1 Index 索引

### 3.1.1 查看所有索引

```json
GET /_cat/indices?v
```

### 3.1.2 判断索引是否存在

```json
HEAD /test_index
```

存在则状态码为200，不存在则为404

### 3.1.3 创建

```json
# 快速创建一个索引
PUT /test_index

# 带配置项的创建索引
# 创建索引 test_index_1，配置别名为 test_index_aliases，且包含 id | name 两个字段
PUT /test_index_1
{
  "aliases": {
    "test_index_aliases": {}
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "integer"
      },
      "name": {
        "type": "text"
      }
    }
  }
}

# 使用别名查看索引
GET /test_index_aliases
```

**NOTE**

1. 索引信息只在创建时添加，且不可修改。

### 3.1.4 删除

```json
DELETE /test_index
```

### 3.1.5 查看索引信息

```json
GET /test_index
```

## 3.2 Document 文档

```json
PUT /test_doc
```

每个文档都有_id属性，相当于数据库中的主键，在一个索引中标志这文档的唯一性。

### 3.2.1 添加

```json
# PUT 添加数据时，必须指定id
PUT /test_doc/_doc/001
{
  "num": 1001,
  "name": "张三",
  "age": 30
}

# POST 添加数据时，可以指定id也可以不指定，不指定则使用自动生成的id
POST /test_doc/_doc
{
  "num": 1002,
  "name": "李四",
  "age": 18
}

POST /test_doc/_doc/003
{
  "num": 1003,
  "name": "王五",
  "age": 26
}
```

**多条添加**

多条添加是利用`_bulk`API，进行多条文档的插入。
[bulk API 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/8.5/docs-bulk.html)

```json
PUT /test_doc_1
# 指定index和_id
POST /_bulk
{"index": {"_index": "test_doc_1", "_id":"1001"}}
{"id": "1001", "name": "zhang san", "age": 30}
{"index": {"_index": "test_doc_1", "_id":"1002"}}
{"id": "1002", "name": "li si", "age": 40}
{"index": {"_index": "test_doc_1", "_id":"1003"}}
{"id": "1003", "name": "wang wu", "age": 50}

# 简化版本（不指定id）
PUT /test_doc_1/_bulk
{"index": {}}
{"id": "1004", "name": "zhangsan", "age": 30}
{"index": {}}
{"id": "1005", "name": "lisi", "age": 40}
{"index": {}}
{"id": "1006", "name": "wangwu", "age": 50}
```

**NOTE**

1. 每条数据前都要加index 或者 create。
2. 数据内容必须在一行。

### 3.2.2 修改

**整体替换**（修改整条数据）

整体替换的修改方式与添加文档操作相同。

```json
# PUT 和 POST 都可修改数据
PUT /test_doc/_doc/001
{
  "num": 1001,
  "name": "张三",
  "age": 35
}

POST /test_doc/_doc/003
{
  "num": 1003,
  "name": "王五"
}
```

**局部修改**

```json
POST /test_doc/_update/003
{
  "doc": {
    "name": "赵六",
    "age": 24,
    "address": "萍水街54号"
  }
}
```

### 3.2.3 删除

```json
DELETE /test_doc/_doc/003
```

### 3.2.4 查询

```json
# 准备查询数据
PUT /test_query
POST /_bulk
{"index": {"_index": "test_query", "_id":"1001"}}
{"id": "1001", "name": "zhang san", "age": 30}
{"index": {"_index": "test_query", "_id":"1002"}}
{"id": "1002", "name": "li si", "age": 40}
{"index": {"_index": "test_query", "_id":"1003"}}
{"id": "1003", "name": "wang wu", "age": 50}
{"index": {"_index": "test_query", "_id":"1004"}}
{"id": "1004", "name": "zhangsan", "age": 30}
{"index": {"_index": "test_query", "_id":"1005"}}
{"id": "1005", "name": "lisi", "age": 40}
{"index": {"_index": "test_query", "_id":"1006"}}
{"id": "1006", "name": "wangwu", "age": 50}
```

#### 3.2.4.1 查询全部

```json
GET /test_query/_search
{
  "query": {
    "match_all": {}
  }
}
# 简化版本
GET /test_query/_search
```

#### 3.2.4.2 词查询

词查询是我们使用es的最主要原因。

```json
GET /test_query/_search
{
  "query": {
    "match": {
      "name": "zhang li"
    }
  }
}
```

上面例子可以查询出`name`中包含 zhang 或 li 的所有文档。

**NOTE**

1. 并不等同于sql中的like。
2. es对**英文**的分词默认使用**空格**切分，因此zhangsan会被视为一个词，因此查询zhang时，zhangsan不会被查询出，而zhang san会被查询出。
3. es对**中文**的分词默认为**单字**切分。而单字切分其实很难满足我们日常的使用需要，因此在实际使用中要引入中文分词器。

#### 3.2.4.3 筛选结果列

即sql查询中select的作用。

```json
# 查询 name 中包含zhang的文档的 name和age
GET /test_query/_search
{
  "_source": ["name", "age"], 
  "query": {
    "match": {
      "name": "zhang"
    }
  }
}
```

#### 3.2.4.4 条件查询

**must 与**

```json
# name包含zhang 且 age = 30
GET /test_query/_search
{
  "query": {
    "bool": {
      "must": [
        {"match": {"name": "zhang"}},
        {"match": {"age": 30}}
      ]
    }
  }
}
```

相当于SQL：

```sql
select * 
from test_query
where name match 'zhang'
  and age = 30
```

**must_not 非**

```json
# name包含zhang 且 age = 30
GET /test_query/_search
{
  "query": {
    "bool": {
      "must_not": [
        {"match": {"name": "zhang"}},
        {"match": {"age": 30}}
      ]
    }
  }
}
```

相当于SQL：

```sql
select * 
from test_query
where name not match 'zhang'
  and age != 30
```


**should 或**

```json
# name包含zhang 或 age = 30
GET /test_query/_search
{
  "query": {
    "bool": {
      "should": [
        {"match": {"name": "zhang"}},
        {"match": {"age": 30}}
      ]
    }
  }
}
```

相当于SQL：

```sql
select * 
from test_query
where name match 'zhang'
   or age = 30
```

#### 3.2.4.5 排序

```json
GET /test_query/_search
{
  "sort": [
    {
      "age": {
        "order": "desc"
      }
    }
  ]
}
```

#### 3.2.4.6 分页

```json
GET /test_query/_search
{
  "from": 0,
  "size": 2
}
```

#### 3.2.4.6 聚合

```json
GET /test_query/_search
{
  "aggs": {
    /* ageGroup 为此聚合结果名，相当SQL中给字段取的别名 */
    "ageGroup": {
      "terms": {
        "field": "age"
      }
    }
  },
  "size": 0
}

GET /test_query/_search
{
  "aggs": {
    /* ageGroup 为此聚合结果名，相当SQL中给字段取的别名 */
    "ageGroup": {
      "terms": {
        "field": "age"
      },
      "aggs": {
        /* ageSum 为此聚合结果名，相当SQL中给字段取的别名 */
        "ageSum": {
          "sum": {
            "field": "age"
          }
        }
      }
    }
  },
  "size": 0
}
```

## 3.3 索引模板

索引模板用于快速创建一些具有**相同配置项**的索引。

### 3.3.1 创建/更新

创建一个索引模板`my_template`，使得索引名称以my|dwd开头的索引使用此模板配置。

```json
PUT /_template/my_template
{
  "index_patterns": ["my*", "dwd*"],
  "settings": {
    "index" : {
      "number_of_shards": 2
    }
  },
  "mappings": {
    "properties": {
      "create_date": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss"
      }
    }
  }
}
```

更新操作与创建相同。

创建索引时只需要以my|dwd开头即可。

```json
PUT /my_template_index
GET /my_template_index
```

可见索引`my_template_index`已经使用了`my_template`模板的配置。

### 3.3.2 查看

```json
GET /_template/my_template
```

### 3.3.3 删除

```json
DELETE /_template/my_template
```

## 3.x 一点点提升

### 3.x.1 关于自动映射（mappings）

1. 可以创建索引时指定映射（mappings），但如果没有指定。在添加数据后，es会自动生成映射。
2. 已有的映射字段内容不可更改。当插入数据中包含索引中没有的字段时，es会自动添加新字段的映射。（与创建索引时是否指定了映射无关，即使 指定了映射，有新字段时也会es自动添加新的字段。）
3. 如果新插入的数据字段的数据类型与已有映射的数据类型不符，能够自动转换的则可以通过，否则会报错。

```json
# 创建索引（此处指定或者不指定映射不会影响结果测试。）
PUT /test_mappings

# 添加初始数据，初始化映射
POST /test_mappings/_doc
{
  "id": 1001,
  "name": "张三",
  "age": 30
}

# 查看映射
GET /test_mappings/_mapping

# 添加数据，缺失字段是被允许的
POST /test_mappings/_doc
{
  "id": 1002,
  "name": "李四",
}

# 添加数据，新增address字段，查看映射后发现映射中也添加了address字段，且自动识别其类型为text
POST /test_mappings/_doc
{
  "id": 1003,
  "name": "王五",
  "age": 30,
  "address": "五常大厦1032号"
}

# 添加数据，虽然age值给了text类型，address值给了int类型，但可以自动转换为映射类型，因此添加成功
POST /test_mappings/_doc
{
  "id": 1004,
  "name": "赵六",
  "age": "27",
  "address": 1066
}

# 查看插入的数据，发现age值仍未text类型，address值仍为int类型。说明es并不会修改文档的内容。
GET /test_mappings/_search
{
  "query": {
    "match": {
      "id": 1004
    }
  }
}

# 添加数据，提供的id值无法转换为映射的类型，因此报错，插入失败
POST /test_mappings/_doc
{
  "id": "西溪湿地98号",
  "name": "陈七",
  "age": 36,
  "address": "西溪湿地98号"
}
```




# 4 IK分词器

安装查看[ik-GitHub](https://github.com/medcl/elasticsearch-analysis-ik)

IK提供了两个分词算法ik_smart 和 ik_max_word，其中 ik_smart 为最少切分，ik_max_word为最细粒度划分。

| 算法        | 原文本     | 分词                                 |
| ----------- | ---------- | ------------------------------------ |
| ik_smart    | 我是程序员 | ["我", "是", "程序员"]               |
| ik_max_word | 我是程序员 | ["我", "是", "程序员", "程序", "员"] |

## 4.1 es默认分词器与ik分词器比较

**es自带中文分词器**

```json
GET /_analyze
{
  "analyzer": "chinese",
  "text": "我是程序员"
}
```

结果可知，自带的中文分词会诸字拆分，这显然是不适合我们使用的。

**使用ik分词**

```json
GET /_analyze
{
  "analyzer": "ik_smart",
  "text": "我是程序员"
}
```

## 4.2 使用ik分词器作为索引字段的分词算法

将`description`的分词器改为`ik_max_word`

```json
PUT /work_order_ik
{
  "mappings": {
    "properties":{
      "outid":{
        "type":"keyword"
      },
      "reporting_time":{
        "type":"date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      },
      "description":{
        "type":"text",
        /* 分词器设置为ik_max_word（执行时删除此注释） */
        "analyzer":"ik_max_word"
      },
      "processing_time":{
        "type":"date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      },
      "event_status":{
        "type":"keyword"
      },
      "create_time":{
        "type":"date",
        "format": "yyyy-MM-dd HH:mm:ss.S||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      },
      "update_time":{
        "type":"date",
        "format": "yyyy-MM-dd HH:mm:ss.S||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      }
    }
  }
}

# 插入数据
PUT /work_order_ik/_bulk?refresh
{"index": {}}
{"outid": "HZSDH20220144275","reporting_time": "2022-01-13 15:53:27","description": "【新型肺炎】来电反映：余杭区五常街道永福(地铁站)，要求乘客扫地铁的健康码乘车，乘客出示支付宝健康码无法乘车，其认为扫码耽误时间，其称浙江省卫健委已明确表示乘坐公共交通工具，市民可自行打开健康码核验，希望永福地铁站可检查乘客出示的健康码乘车，请相关部门酌情考虑。（余杭区卫生健康局：各单位要在自责范围内做好疫情防控工作，请转市地铁集团调处。）","processing_time": "2022-01-18 14:13:51","event_status": "已作出处理意见","create_time": "2022-06-10 11:58:57.6","update_time": "2022-06-10 11:58:57.6"}
{"index": {}}
{"outid": "2021120914963","reporting_time": "2021-12-12 12:19:41","description": "诉求：希望支付宝给我纰漏信息n事实和理由：我在2019年时候买理财时候将多笔钱转错到别人支付宝中，虽然钱没有多少，但是也没有放弃索要，我咨询当地公安机关，公安机关说你转错是属于民事纠纷让我去法院起诉，我又到当地法院立案，法院说你没有明确的被告不给立案，并且被告也不是我们这里的，我又去支付宝打电话有好几百次要求纰漏对方的身份证，支付宝客服多次说他们就是要求我去法院开具调查令，我又去法院去跟说了这个事，法院意思没有立案不能无缘无故开具调查令，我也没有钱请律师，律师说你就是进入一个死循环，永远的回到原点，马上3年了，在不起诉就没有机会了，希望支付宝给我纰漏个人信息，我也能去被告当地法院起诉，能维护我的合法权益，我也是一个农民，我不想因为这个信息，导致我原地踏步不动，没有钱没有势，支付宝根本不给你纰漏，律师也请不起，希望支付宝公司能把被告信息纰漏给我我拿去起诉，谢了n事发地：浙江省杭州市西湖区浙江省杭州市西湖区西溪路556号8层B段","processing_time": "2021-12-14 11:12:42","event_status": "不予受理","create_time": "2022-06-10 11:58:57.6","update_time": "2022-06-10 11:58:57.6"}
{"index": {}}
{"outid": "2021121513947","reporting_time": "2021-12-24 17:17:14","description": "诉求：迷信活动扰民n事实和理由：萧山区盈丰街道佳丰北苑有人员进行敲锣打鼓、焚烧纸币、办丧事等的迷信活动，噪音扰民严重，要求取加强管理。n事发地：浙江省杭州市萧山区盈丰街道佳丰北苑","processing_time": "2021-12-28 11:06:08","event_status": "已作出处理意见","create_time": "2022-06-10 11:58:57.6","update_time": "2022-06-10 11:58:57.6"}
```

**查询索引**

```json
# 多个关键词查询
GET /work_order_ik/_search
{
  "query": {
    "match": {
      "description": {
        "query": "健康 支付宝",
        "operator": "and", 
        "analyzer": "ik_smart"
      }
    }
  },
  "highlight": {
    "fields": {"description": {}}
  }
}

# 单个关键词查询
GET /work_order_ik/_search
{
  "query": {
    "term": {
      "description": {
        "value": "支付宝"
      }
    }
  },
  "highlight": {
    "fields": {"description": {}}
  }
}
```

# 5 文档评分机制

es采用的评分机制为**TF-IDF**公式
$$
score=boost * idf * tf
$$
其中，boost是用户对查询指定的权重，权重越高则评分越高。

当查询中有多个词时，将每个词的分数相加得总分数。

## 5.1 TF（词频）

Term Frequency：搜索文本中各个词条（term）在查询的文本中出现了多少次，出现次数越多，就越相关，得分越高。
$$
F(freq)=freq / (freq + k1 * (1 - b + b * dl / avgdl))
$$
**freq**, occurrences of term within document（词在当前文档中出现次数）

**k1**, term saturation parameter

**b**, length normalization parameter

**dl**, length of field

**avgdl**, average length of field

## 5.2 IDF（逆文档频率）

Inverse Document Frequency：搜索文本中各个词条（term）在整个索引的所有文档中出现的次数，出现次数越多，说明越不重要，就越不相关。
$$
f(n)=log(1 + (N - n + 0.5) / (n + 0.5))
$$
**N**: total number of documents with field（总的文本数量）

**n**: number of documents containing term（包含此词条的文本数量）

## 5.3 分析

由公式可知，
在单个词的查询中，idf值固定，因此词出现次数更多的文档会获得更高的分数。
在多个词的查询中，在全部文档中出现越多的词idf值越低，其对总分数的影响就越小。

> 因此，词出现次数越多的文档，分数越高；
> 在全部文档中出现次数越多的词，分数越低。

## 5.4 权重分析

```json
# 插入测试数据
PUT /test_score
PUT /test_score/_doc/1001
{
  "title": "Hadoop is a Framework.",
  "content": "Hadoop 是一个大数据基础框架"
}
PUT /test_score/_doc/1002
{
  "title": "Hive is a SQL tools.",
  "content": "Hadoop 是一个SQL工具"
}
PUT /test_score/_doc/1003
{
  "title": "Spark is a Framework.",
  "content": "Spark 是一个分布式计算引擎"
}
```

不加权重的查询。

```json
GET /test_score/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": "Hadoop"
          }
        },
        {
          "match": {
            "title": "Hive"
          }
        },
        {
          "match": {
            "title": "Spark"
          }
        }
      ]
    }
  }
}

# 结果
{
  "took": 1,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 3,
      "relation": "eq"
    },
    "max_score": 1.0630728,
    "hits": [
      {
        "_index": "test_score",
        "_id": "1001",
        "_score": 1.0630728,
        "_source": {
          "title": "Hadoop is a Framework.",
          "content": "Hadoop 是一个大数据基础框架"
        }
      },
      {
        "_index": "test_score",
        "_id": "1003",
        "_score": 1.0630728,
        "_source": {
          "title": "Spark is a Framework.",
          "content": "Spark 是一个分布式计算引擎"
        }
      },
      {
        "_index": "test_score",
        "_id": "1002",
        "_score": 0.9686552,
        "_source": {
          "title": "Hive is a SQL tools.",
          "content": "Hadoop 是一个SQL工具"
        }
      }
    ]
  }
}
```

可见，含有Hadoop的索引和含有Spark的索引分值是相同的。但如果我们希望让Spark的索引分值更高，能排在Hadoop索引之前，那我们可以通过更改权重来实现。

```json
# ES默认权重倍率为1。如果我们希望在搜索时，Spark的索引能靠前排放。可以将搜索Spark的权限值倍率调高。
GET /test_score/_search?explain=true
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": "Hadoop"
          }
        },
        {
          "match": {
            "title": "Hive"
          }
        },
        {
          "match": {
            "title": {
              "query": "Spark",
              /* 此处我们将此搜索的权重倍率调至1.5倍（运行代码时删除此行） */
              "boost": 2
            }
          }
        }
      ]
    }
  }
}
```

结果可知，Spark的查询结果中boost值翻了2倍，因此在搜索结果中排在了最前面。

# 6 Java API操作（8.0）

## 6.1 依赖

```xml
<dependencies>
    <!-- es -->
    <dependency>
        <groupId>org.elasticsearch.plugin</groupId>
        <artifactId>x-pack-sql-jdbc</artifactId>
        <version>8.5.1</version>
    </dependency>
    <dependency>
        <groupId>co.elastic.clients</groupId>
        <artifactId>elasticsearch-java</artifactId>
        <version>8.5.1</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.12.3</version>
    </dependency>
    <dependency>
        <groupId>jakarta.json</groupId>
        <artifactId>jakarta.json-api</artifactId>
        <version>2.0.1</version>
    </dependency>
</dependencies>
```

## 6.2 获取crt格式的安全认证证书

```shell
[es@test ~]$ cd /opt/apps/elasticsearch-8.5.1/config/certs
# 因为我们之前的安全认证都没有设置密码，因此直接回车即可
[es@test certs]$ openssl pkcs12 -in elastic-stack-ca.p12 -clcerts -nokeys -out es-api-ca.crt
```

将生成的安全认证证书拷贝到项目工程中。比如resources目录下。

> 安全认证 java代码 详情看 com.dwsn.es.client.ESClient