package com.dwsn.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import com.dwsn.es.api.Document;
import com.dwsn.es.api.Index;
import com.dwsn.es.client.ESClient;
import com.dwsn.es.pojo.User;

import java.util.ArrayList;

public class TestEs {

    public static void main(String[] args) throws Exception {
        // 获取es client
        ElasticsearchClient client = ESClient.getClient();

        final String INDEX_NAME = "es_test";
        // 操作索引
//        operationIndex(client, INDEX_NAME);

        // 操作文档
//        operationDocument(client, INDEX_NAME);

        // 查询文档
        searchDocument(client);
        searchDocumentLambda(client);

        // 关闭连接
        ESClient.stop();
    }

    public static void searchDocument(ElasticsearchClient client) throws Exception {
        MatchQuery matchQuery = new MatchQuery.Builder()
                .field("age").query(30)
                .build();

        Query query = new Query.Builder()
                .match(matchQuery)
                .build();

        SearchRequest searchRequest = new SearchRequest.Builder()
                .query(query)
                .build();

        SearchResponse<User> searchResponse = client.search(searchRequest, User.class);
        System.out.println("查询结果响应对象：" + searchResponse);
    }

    public static void searchDocumentLambda(ElasticsearchClient client) throws Exception {
        SearchResponse<User> searchResponse = client.search(
                req -> {
                    req.query(
                            query -> query.match(
                                    match -> match.field("name").query("zhangsan1")
                            )
                    );

                    return req;
                },
                User.class
        );
        System.out.println("查询结果响应对象（lambda版）：" + searchResponse);
    }

    public static void operationDocument(ElasticsearchClient client, String indexName) throws Exception {
        User user = new User();
        user.setId(1001);
        user.setName("zhangsan");
        user.setAge(30);

        Document document = new Document(client);

        // 增加文档 - 单个
        CreateResponse createResponse = document.insert(indexName, "1001", user);
        System.out.println("文档创建响应对象" + createResponse);

        // 批量添加数据
        ArrayList<User> docs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            docs.add(new User(3000 + i, "zhangsan" + i, 30 + i));
        }

        BulkResponse bulkResponse = document.bulkInsert(indexName, docs);
        BulkResponse bulkResponseLambda = document.bulkInsertLambda(indexName, docs);
        System.out.println("批量新增数据的相应对象：" + bulkResponse);
        System.out.println("批量新增数据的相应对象（lambda版）：" + bulkResponseLambda);

        // 删除
        DeleteResponse deleteResponse = document.delete(indexName, "1001");
        System.out.println("删除数据的相应对象：" + deleteResponse);

    }

    public static void operationIndex(ElasticsearchClient client, String indexName) throws Exception {
        Index indexClient = new Index(client);
        // 判断索引是否存在
        boolean flg = indexClient.exists(indexName);
        if (flg) {
            System.out.println("索引 " + indexName + "已经存在");
        } else {
            // 创建索引
            CreateIndexResponse createIndexResponse = indexClient.create(indexName);
            System.out.println("创建索引的响应对象：" + createIndexResponse);
        }

        // 查询索引
        GetIndexResponse getIndexResponse = indexClient.get(indexName);
//        IndexState indexState = getIndexResponse.get(INDEX_NAME);
        System.out.println("查询索引的响应对象：" + getIndexResponse);

        // 删除索引
        DeleteIndexResponse deleteIndexResponse = indexClient.delete(indexName);
        System.out.println("删除索引的响应对象：" + deleteIndexResponse);
    }

}
