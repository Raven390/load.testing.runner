package ru.develonica.load.testing.runner.service.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.develonica.load.testing.common.model.generated.TimeBasedMetric;
import ru.develonica.load.testing.common.model.generated.TimeBasedMetricMap;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static ru.develonica.load.testing.runner.service.metrics.store.MetricsStore.MEMORY_METRIC_STORE;

@Service
public class MetricsCollector extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollector.class);
    public static final String USED = "used";
    public static final String COMMITTED = "committed";
    public static final String INIT = "init";
    public static final String MAX = "max";
    public static final String HEAP_MEMORY_USAGE = "HeapMemoryUsage";

    private String JMXHost = "";
    private Integer JMXPort = null;

    public MetricsCollector() {
        MEMORY_METRIC_STORE.put(USED, TimeBasedMetric.newBuilder());
        MEMORY_METRIC_STORE.put(COMMITTED, TimeBasedMetric.newBuilder());
        MEMORY_METRIC_STORE.put(INIT, TimeBasedMetric.newBuilder());
        MEMORY_METRIC_STORE.put(MAX, TimeBasedMetric.newBuilder());
    }

    public void run() {
        try {
            JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + JMXHost + ":" + JMXPort + "/jmxrmi");
            MBeanServerConnection serverConnection;
            try (JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL)) {
                serverConnection = jmxConnector.getMBeanServerConnection();
                while (!interrupted()) {
                    ObjectName memoryMBeanName = new ObjectName("java.lang:type=Memory");
                    CompositeDataSupport compositeData = (CompositeDataSupport) serverConnection.getAttribute(memoryMBeanName, HEAP_MEMORY_USAGE);
                    String timestamp = OffsetDateTime.now().toString();

                    // Retrieve individual MemoryUsage attributes
                    long usedMemory = (Long) compositeData.get(USED);
                    putMetric(timestamp, usedMemory, USED);

                    long committedMemory = (Long) compositeData.get(COMMITTED);
                    putMetric(timestamp, committedMemory, COMMITTED);

                    long initMemory = (Long) compositeData.get(INIT);
                    putMetric(timestamp, initMemory, INIT);

                    long maxMemory = (Long) compositeData.get(MAX);
                    putMetric(timestamp, maxMemory, MAX);

                    LOG.info("MBean {}  attributeValue: initMemory {}, commitMemory {}, usedMemory {}, maxMemory: {}", HEAP_MEMORY_USAGE, initMemory, committedMemory, usedMemory, maxMemory);
                    sleep(2000);

                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public void setJmxParams(String jmxHost, Integer jmxPort) {
        this.JMXHost = jmxHost;
        this.JMXPort = jmxPort;
    }

    private void putMetric(String timestamp, long usedMemory, String metricName) {
        TimeBasedMetricMap usedMemoryMetricMap = TimeBasedMetricMap.newBuilder().putData(timestamp, usedMemory).build();
        MEMORY_METRIC_STORE.get(metricName).addMetricsMap(usedMemoryMetricMap).build();
    }
}
