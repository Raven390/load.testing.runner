package ru.develonica.load.testing.runner.service.http;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.develonica.load.testing.common.model.HttpMethod;
import ru.develonica.load.testing.common.model.TestCaseRequest;
import ru.develonica.load.testing.runner.property.HttpClientProperties;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.develonica.load.testing.runner.service.metrics.store.MetricsStore.RESPONSE_STATUS_METRIC_STORE;

public class HttpSender extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSender.class);

    private final CloseableHttpClient client;
    private final TestCaseRequest request;

    public HttpSender(PoolingHttpClientConnectionManager connectionManager,
                      HttpClientProperties properties, TestCaseRequest testCaseRequest) {
        request = testCaseRequest;
        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultCredentialsProvider(new BasicCredentialsProvider());

        int evictConnectionTime = properties.getEvictIdleConnectionTime();
        this.client = builder.setConnectionManager(connectionManager)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.of(evictConnectionTime, TimeUnit.SECONDS))
                .build();

    }

    public void run() {
        while (!interrupted()) {
            String url = request.getUrl();
            HttpMethod method = request.getMethod();
            String body = request.getBody();
            Map<String, String> headerMap = request.getHeader();

            HttpUriRequestBase httpRequest = null;

            switch (method) {
                case GET -> httpRequest = new HttpGet(url);
                case POST -> httpRequest = new HttpPost(url);
                case PUT -> httpRequest = new HttpPut(url);
                case DELETE -> httpRequest = new HttpDelete(url);
                case PATCH -> httpRequest = new HttpPatch(url);
                case HEAD -> httpRequest = new HttpHead(url);
                case TRACE -> httpRequest = new HttpTrace(url);
                case OPTIONS -> httpRequest = new HttpOptions(url);
            }

            if (body != null) {
                StringEntity entity = new StringEntity(body);
                httpRequest.setEntity(entity);
            }

            if (headerMap != null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpRequest.setHeader(entry.getKey(), entry.getValue());
                }
            }

            try {
//                LOG.info("URL: {}, HEADER: {}, BODY: {}", httpRequest.getRequestUri(), Arrays.toString(httpRequest.getHeaders()), new String(httpRequest.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8));
                int code = client.execute(httpRequest, (classicHttpResponse -> {
                    int responseCode = classicHttpResponse.getCode();
//                    LOG.info("Response body: {}", new String(classicHttpResponse.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8));
                    return responseCode;
                }));
                if (RESPONSE_STATUS_METRIC_STORE.containsKey(code)) {
                    RESPONSE_STATUS_METRIC_STORE.replace(code, RESPONSE_STATUS_METRIC_STORE.get(code) + 1);
                } else {
                    RESPONSE_STATUS_METRIC_STORE.put(code, 1L);
                }
//                LOG.info("Request executed, code: {}", code);
            } catch (IOException e) {
                LOG.error("Error sending request: {}", e.getMessage());
            }
        }
    }

}
