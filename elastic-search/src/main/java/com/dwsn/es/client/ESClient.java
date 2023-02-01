package com.dwsn.es.client;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class ESClient {

    /**
     * 同步客户端对象
     */
    private static ElasticsearchClient client;
    /**
     * 异步客户端对象
     */
    private static ElasticsearchAsyncClient asyncClient;
    /**
     * 请求传输对象 （官方给出的介绍：A transport layer implementation. This is where all HTTP request handling takes place.）
     */
    private static ElasticsearchTransport transport;

    public static ElasticsearchClient getClient() throws Exception {
        if (null == client) {
            initESConnection();
        }
        return client;
    }

    public static ElasticsearchAsyncClient getAsyncClient() throws Exception {
        if (null == asyncClient) {
            initESConnection();
        }
        return asyncClient;
    }

    /**
     * 获取客户端对象
     *
     * @throws Exception
     */
    private static void initESConnection() throws Exception {
        // elasticsearch安全认证
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "NPQfnbpss5=KNgHEu2Rv"));

        Path caCertificatePath = Paths.get("elastic-search/src/main/resources/es-api-ca.crt");
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        Certificate trustedCa;
        try (InputStream is = Files.newInputStream(caCertificatePath)) {
            trustedCa = factory.generateCertificate(is);
        }
        KeyStore trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);
        SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
        final SSLContext sslContext = sslContextBuilder.build();


        RestClientBuilder builder = RestClient.builder(new HttpHost("test", 9200, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .setDefaultCredentialsProvider(credentialsProvider));

        // Create the low-level client
        RestClient restClient = builder.build();

        // Create the transport with a Jackson mapper
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        client = new ElasticsearchClient(transport);
        asyncClient = new ElasticsearchAsyncClient(transport);
    }

    /**
     * 关闭连接
     */
    public static void stop() throws Exception {
        client = null;
        asyncClient = null;

        transport.close();
    }
}
