package com.dwsn.es.api;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.*;

/**
 * @author Dawson
 * @date 2023-01-31
 */
public class Index {

    private ElasticsearchIndicesClient indices;

    public Index(ElasticsearchClient client) {
        // 获取索引客户端对象
        indices = client.indices();
    }

    public boolean exists(String indexName) throws Exception {
        // 判断索引是否存在
        ExistsRequest existsRequest = new ExistsRequest.Builder().index(indexName).build();

        return indices.exists(existsRequest).value();
    }

    public boolean existsLambda(String indexName) throws Exception {
        return indices.exists(req -> req.index(indexName) ).value();
    }

    public CreateIndexResponse create(String indexName) throws Exception {
        // 需要采用构建器方式来构建对象，ESAPI的对象基本上都是要采用这种方式
        CreateIndexRequest request = new CreateIndexRequest.Builder().index(indexName).build();

        return indices.create(request);
    }

    public CreateIndexResponse createLambda(String indexName) throws Exception {
        return indices.create(req -> req.index(indexName) );
    }

    public GetIndexResponse get(String indexName) throws Exception {
        GetIndexRequest getIndexRequest = new GetIndexRequest.Builder().index(indexName).build();

        return indices.get(getIndexRequest);
    }

    public GetIndexResponse getLambda(String indexName) throws Exception {
        return indices.get(req -> req.index(indexName) );
    }

    public DeleteIndexResponse delete(String indexName) throws Exception {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexName).build();

        return indices.delete(deleteIndexRequest);
    }

    public DeleteIndexResponse deleteLambda(String indexName) throws Exception {
        return indices.delete(req -> req.index(indexName) );
    }

}
