package ru.develonica.load.testing.runner.config;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.develonica.load.testing.runner.property.HttpClientConnectionPool;
import ru.develonica.load.testing.runner.property.HttpClientProperties;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpManagerConfig {

    /**
     * Пул для HTTP соединений.
     * @param properties параметры http client-а
     * @return менеджер пула HTTP
     */
    @Bean(name = "PoolingHttpClientConnectionManager")
    public PoolingHttpClientConnectionManager getPoolingConnectionManager(HttpClientProperties properties) {

        PoolingHttpClientConnectionManager cm = new
                PoolingHttpClientConnectionManager();
        HttpClientConnectionPool connectionPool = properties.getConnectionPool();
        cm.setMaxTotal(connectionPool.getMaxTotal());
        cm.setDefaultMaxPerRoute(connectionPool.getMaxPerRoute());
        cm.setValidateAfterInactivity(TimeValue.of(connectionPool.getValidateAfterInactivityPeriod(), TimeUnit.MILLISECONDS));
        cm.closeIdle(TimeValue.of(connectionPool.getCloseIdleConnectionsTimeout(), TimeUnit.MICROSECONDS));
        return cm;
    }
}