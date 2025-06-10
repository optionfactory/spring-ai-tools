package net.optionfactory.springai.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.function.Supplier;

import static net.optionfactory.springai.monitoring.MetricNameProvider.getMetricName;

public class QueueMeter {
    private final Counter enqueuedCounter;
    private final Counter dequeuedCounter;

    public QueueMeter(MeterRegistry meterRegistry, Supplier<Number> queueLengthSupplier) {
        Gauge.builder(getMetricName("queue.length"), queueLengthSupplier)
                .register(meterRegistry);

        this.enqueuedCounter = Counter.builder(getMetricName("queue.enqueued"))
                .register(meterRegistry);
        this.dequeuedCounter = Counter.builder(getMetricName("queue.dequeued"))
                .register(meterRegistry);
    }

    public void enqueued() {
        enqueuedCounter.increment();
    }

    public void dequeued() {
        dequeuedCounter.increment();
    }
}
