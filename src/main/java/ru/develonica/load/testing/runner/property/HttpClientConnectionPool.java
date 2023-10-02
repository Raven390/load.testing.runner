package ru.develonica.load.testing.runner.property;

public class HttpClientConnectionPool {

    private int maxTotal;
    private int maxPerRoute;
    private int validateAfterInactivityPeriod;
    private int closeIdleConnectionsTimeout;

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public int getValidateAfterInactivityPeriod() {
        return validateAfterInactivityPeriod;
    }

    public void setValidateAfterInactivityPeriod(int validateAfterInactivityPeriod) {
        this.validateAfterInactivityPeriod = validateAfterInactivityPeriod;
    }

    public int getCloseIdleConnectionsTimeout() {
        return closeIdleConnectionsTimeout;
    }

    public void setCloseIdleConnectionsTimeout(int closeIdleConnectionsTimeout) {
        this.closeIdleConnectionsTimeout = closeIdleConnectionsTimeout;
    }
}
