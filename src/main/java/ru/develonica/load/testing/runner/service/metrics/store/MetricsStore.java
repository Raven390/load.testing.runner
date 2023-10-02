package ru.develonica.load.testing.runner.service.metrics.store;

import ru.develonica.load.testing.common.model.generated.TimeBasedMetric;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsStore {

    public static ConcurrentHashMap<Integer, Long> RESPONSE_STATUS_METRIC_STORE = new ConcurrentHashMap<>(0);
    public static ConcurrentHashMap<String, TimeBasedMetric.Builder> MEMORY_METRIC_STORE = new ConcurrentHashMap<>(0);

    public static Map<String, TimeBasedMetric> getCurrentMetrics() {
        Map<String, TimeBasedMetric> result = new HashMap<>();

        for (Map.Entry<String, TimeBasedMetric.Builder> entry : MEMORY_METRIC_STORE.entrySet()) {
            result.put(entry.getKey(), entry.getValue().build());
        }
        return result;
    }
}
