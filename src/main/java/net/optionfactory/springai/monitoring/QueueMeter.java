package net.optionfactory.springai.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.function.Supplier;

import static net.optionfactory.springai.monitoring.MetricNameProvider.getMetricName;

public class QueueMeter {
    private final MeterRegistry meterRegistry;
    private final Counter enqueuedCounter;
    private final Counter dequeuedCounter;
    private Gauge queueLength;

    public QueueMeter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        queueLength = Gauge.builder(getMetricName("queue.length"), () -> 0)
                .register(meterRegistry);

        this.enqueuedCounter = Counter.builder(getMetricName("queue.enqueued"))
                .register(meterRegistry);
        this.dequeuedCounter = Counter.builder(getMetricName("queue.dequeued"))
                .register(meterRegistry);
    }

    public void enqueued() {
        enqueuedCounter.increment();
    }

    public void enqueued(double amount) {
        enqueuedCounter.increment(amount);
    }

    public void dequeued() {
        dequeuedCounter.increment();
    }

    public void dequeued(double amount) {
        dequeuedCounter.increment(amount);
    }

    public void setQueueLengthSupplier(Supplier<Number> supplier) {
        meterRegistry.remove(queueLength);
        this.queueLength = Gauge.builder(getMetricName("queue.length"), supplier)
                .register(meterRegistry);
    }
}
