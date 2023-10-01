package ru.develonica.load.testing.runner.service.load;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.develonica.load.testing.common.model.TestCaseRequest;
import ru.develonica.load.testing.common.model.generated.Metrics;
import ru.develonica.load.testing.common.model.generated.Status;
import ru.develonica.load.testing.runner.property.HttpClientProperties;
import ru.develonica.load.testing.runner.service.http.HttpSender;
import ru.develonica.load.testing.runner.service.metrics.MetricsCollector;
import ru.develonica.load.testing.runner.service.metrics.store.MetricsStore;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import static ru.develonica.load.testing.runner.service.metrics.store.MetricsStore.RESPONSE_STATUS_METRIC_STORE;

@Service
public class LoadTestingService {
    private static final Logger LOG = LoggerFactory.getLogger(LoadTestingService.class);

    private final PoolingHttpClientConnectionManager connectionManager;
    private final HttpClientProperties properties;
    private ThreadPoolExecutor httpExecutor;
    private ThreadPoolExecutor metricsExecutor;
    private MetricsCollector metricsCollector;

    public LoadTestingService(PoolingHttpClientConnectionManager connectionManager, HttpClientProperties properties) {
        this.connectionManager = connectionManager;
        this.properties = properties;
    }

    public String start(Duration duration, Integer parallelRequests, TestCaseRequest testCaseRequest, String jmxHost, Integer jmxPort) {
        LOG.info("Starting: {}", Thread.currentThread());
        httpExecutor = ThreadPoolFactory.newThreadPool(parallelRequests);

        IntStream.rangeClosed(1, parallelRequests)
                .mapToObj(i -> new HttpSender(connectionManager, properties, testCaseRequest))
                .forEach(httpExecutor::submit);

        if (!jmxHost.isBlank() && jmxPort != 0) {
            metricsExecutor = ThreadPoolFactory.newThreadPool(1);
            metricsCollector.setJmxParams(jmxHost, jmxPort);
            metricsExecutor.submit(metricsCollector);
        }


        Thread stopThread = new Thread(() -> {
            try {
                Thread.sleep(duration.toMillis());
                stopExecutor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        stopThread.start();

        return Status.STARTED.name();
    }

    public Metrics getMetrics() {
        return Metrics.newBuilder()
                .putAllMemoryMetrics(MetricsStore.getCurrentMetrics())
                .putAllCodesReceived(Map.copyOf(RESPONSE_STATUS_METRIC_STORE))
                .build();
    }

    private void stopExecutor() {
        if (httpExecutor != null) {
            httpExecutor.shutdownNow();
        }

        if (metricsExecutor != null) {
            metricsExecutor.shutdownNow();
        }
    }

    @Autowired
    public void setMetricsCollector(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }
}
