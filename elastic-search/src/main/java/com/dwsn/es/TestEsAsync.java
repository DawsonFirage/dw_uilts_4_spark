package com.dwsn.es;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import com.dwsn.es.client.ESClient;

public class TestEsAsync {
    public static void main(String[] args) throws Exception {
        ElasticsearchAsyncClient asyncClient = ESClient.getAsyncClient();

        asyncClient.indices().create(req -> req.index("async_index"))
                .whenComplete(
                        (resq, error) -> {
                            System.out.println("回调方法...");
                            if (resq != null) {
                                System.out.println(resq.acknowledged());
                            } else {
                                error.printStackTrace();
                            }

                        }
                );

        System.out.println("主线程方法...");

        ESClient.stop();
    }
}
