package net.optionfactory.springai.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.optionfactory.springai.monitoring.MetricNameProvider.getMetricName;

public class MonitoringAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    public final Map<String, Long> threadCallStartTimesMs = new ConcurrentHashMap<>();
    public final Map<String, Long> threadStreamStartTimesMs = new ConcurrentHashMap<>();

    public MonitoringAdvisor(MeterRegistry meterRegistry) {
        Gauge.builder(getMetricName("gpt.live.oldest.callduration"), () -> {
                    final var now = Instant.now();
                    return threadCallStartTimesMs.values().stream().mapToLong(Long::longValue)
                            .min()
                            .stream()
                            .map(oldest -> now.toEpochMilli() - oldest)
                            .findFirst()
                            .orElse(0);
                })
                .register(meterRegistry);
        Gauge.builder(getMetricName("gpt.live.oldest.streamduration"), () -> {
                    final var now = Instant.now();
                    return threadStreamStartTimesMs.values().stream().mapToLong(Long::longValue)
                            .min()
                            .stream()
                            .map(oldest -> now.toEpochMilli() - oldest)
                            .findFirst()
                            .orElse(0);
                })
                .register(meterRegistry);
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        threadCallStartTimesMs.put(Thread.currentThread().getName(), Instant.now().toEpochMilli());
        final var advisedResponse = chain.nextAroundCall(advisedRequest);
        threadCallStartTimesMs.remove(Thread.currentThread().getName());
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        threadStreamStartTimesMs.put(Thread.currentThread().getName(), Instant.now().toEpochMilli());
        final var advisedResponse = chain.nextAroundStream(advisedRequest);
        threadStreamStartTimesMs.remove(Thread.currentThread().getName());
        return advisedResponse;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
