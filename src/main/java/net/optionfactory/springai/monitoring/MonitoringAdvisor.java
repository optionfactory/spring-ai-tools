package net.optionfactory.springai.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.optionfactory.springai.monitoring.MetricNameProvider.getMetricName;

public class MonitoringAdvisor implements CallAdvisor, StreamAdvisor {


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
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        threadCallStartTimesMs.put(Thread.currentThread().getName(), Instant.now().toEpochMilli());
        try {
            return callAdvisorChain.nextCall(chatClientRequest);
        } finally {
            threadCallStartTimesMs.remove(Thread.currentThread().getName());

        }
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        threadStreamStartTimesMs.put(Thread.currentThread().getName(), Instant.now().toEpochMilli());
        try {
            return streamAdvisorChain.nextStream(chatClientRequest);
        } finally {
            threadStreamStartTimesMs.remove(Thread.currentThread().getName());
        }
    }
}
