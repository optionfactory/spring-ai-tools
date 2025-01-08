package net.optionfactory.springai.monitoring;

public interface MetricNameProvider {
    static String getMetricName(String name) {
        return "ditabit.monitoring." + name;
    }
}
