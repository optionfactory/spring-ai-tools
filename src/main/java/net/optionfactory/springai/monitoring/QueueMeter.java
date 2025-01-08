package net.optionfactory.springai.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.function.Supplier;

import static net.optionfactory.springai.monitoring.MetricNameProvider.getMetricName;

public class QueueMeter {
    public QueueMeter(MeterRegistry meterRegistry, Supplier<Number> queueLengthSupplier) {
        Gauge.builder(getMetricName("queue.length"), queueLengthSupplier)
                .register(meterRegistry);
    }
}
