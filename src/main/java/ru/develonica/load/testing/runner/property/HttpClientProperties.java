package ru.develonica.load.testing.runner.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.http-client")
public class HttpClientProperties {

    private Integer evictIdleConnectionTime;
    private HttpClientConnectionPool connectionPool;

    public Integer getEvictIdleConnectionTime() {
        return evictIdleConnectionTime;
    }

    public void setEvictIdleConnectionTime(Integer evictIdleConnectionTime) {
        this.evictIdleConnectionTime = evictIdleConnectionTime;
    }

    public HttpClientConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(HttpClientConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
