package com.dwsn.es.api;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation;
import com.dwsn.es.pojo.User;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作文档与操作索引的区别：
 * 索引需要先从 ElasticsearchClient 中获取操作索引的客户端对象 ElasticsearchIndicesClient，
 * 指令需要用索引的客户端对象发出。
 * 而操作文档则不需要创建新的客户端对象，直接使用ElasticsearchClient即可。
 */
public class Document {
    private ElasticsearchClient client;

    public Document(ElasticsearchClient client) {
        this.client = client;
    }

    public <T> CreateResponse insert(String indexName, String id, T doc) throws IOException {
        CreateRequest<T> createRequest = new CreateRequest.Builder<T>()
                .index(indexName)
                .id(id)
                .document(doc)
                .build();

        return client.create(createRequest);
    }

    public <T> CreateResponse insertLambda(String indexName, String id, T doc) throws IOException {
        return client.create(req -> req.index(indexName).id(id).document(doc));
    }

    public <T> BulkResponse bulkInsert(String indexName, List<T> docs) throws IOException {
        List<BulkOperation> opts = docs.stream()
                .map(
                        doc -> {
                            CreateOperation<T> optObj = new CreateOperation.Builder<T>()
                                    .index(indexName)
//                                    .id(doc.getId().toString())
                                    .document(doc)
                                    .build();
                            BulkOperation opt = new BulkOperation.Builder().create(optObj).build();

                            return opt;
                        }
                ).collect(Collectors.toList());

        BulkRequest bulkRequest = new BulkRequest.Builder()
                .operations(opts)
                .build();
        return client.bulk(bulkRequest);
    }

    public <T> BulkResponse bulkInsertLambda(String indexName, List<T> docs) throws IOException {
        return client.bulk(
                req -> {
                    docs.forEach(
                            doc -> req.operations(
                                    bulkOperation -> bulkOperation.create(
                                            createOperation -> createOperation.index(indexName).document(doc)
                                    )
                            )
                    );
                    return req;
                }
        );
    }

    public DeleteResponse delete(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest.Builder()
                .index(indexName)
                .id(id)
                .build();
        return client.delete(deleteRequest);
    }

    public DeleteResponse deleteLambda(String indexName, String id) throws IOException {
        return client.delete(req -> req.index(indexName).id(id));
    }

}
