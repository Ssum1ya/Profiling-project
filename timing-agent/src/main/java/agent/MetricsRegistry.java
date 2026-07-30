package agent;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class MetricsRegistry {
    public static final Map<String, LongAdder> callCountTotal = new ConcurrentHashMap<>();
    public static final Map<String, LongAdder> totalTimeNano = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> maxTimeNanos = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> minTimeNanos = new ConcurrentHashMap<>();

    public static void write(String method, long nanos) {
        callCountTotal.computeIfAbsent(method, k -> new LongAdder()).increment();

        totalTimeNano.computeIfAbsent(method, k -> new LongAdder()).add(nanos);

        maxTimeNanos.computeIfAbsent(method, k -> new AtomicLong(Long.MIN_VALUE))
                .updateAndGet(current -> Math.max(current, nanos));

        minTimeNanos.computeIfAbsent(method, k -> new AtomicLong(Long.MAX_VALUE))
                .updateAndGet(current -> Math.min(current, nanos));
    }

    public static String toPrometheusFormat() {
        StringBuilder sb = new StringBuilder();

        // total calls
        sb.append("# HELP method_calls_total Total number of method calls\n");
        sb.append("# TYPE method_calls_total counter\n");
        callCountTotal.forEach((method, count) -> {
            sb.append(String.format(Locale.US, "method_calls_total{method=\"%s\"} %d\n",
                    sanitizeLabel(method), count.sum()));
        });

        // Total ms
        sb.append("# HELP method_time_total_ms Total execution time in milliseconds\n");
        sb.append("# TYPE method_time_total_ms counter\n");
        totalTimeNano.forEach((method, nanos) -> {
            double totalMs = nanos.sum() / 1_000_000.0;
            sb.append(String.format(Locale.US, "method_time_total_ms{method=\"%s\"} %.3f\n",
                    sanitizeLabel(method), totalMs));
        });

        // Max time
        sb.append("# HELP method_time_max_ms Maximum observed execution time in milliseconds\n");
        sb.append("# TYPE method_time_max_ms gauge\n");
        maxTimeNanos.forEach((method, nanos) -> {
            double maxMs = nanos.get() / 1_000_000.0;
            sb.append(String.format(Locale.US, "method_time_max_ms{method=\"%s\"} %.3f\n",
                    sanitizeLabel(method), maxMs));
        });

        // Min time
        sb.append("# HELP method_time_min_ms Minimum observed execution time in milliseconds\n");
        sb.append("# TYPE method_time_min_ms gauge\n");
        minTimeNanos.forEach((method, nanos) -> {
            double minMs = nanos.get() / 1_000_000.0;
            sb.append(String.format(Locale.US, "method_time_min_ms{method=\"%s\"} %.3f\n",
                    sanitizeLabel(method), minMs));
        });

        return sb.toString();
    }

    private static String sanitizeLabel(String label) {
        return label.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static void resetMinMax() {
        maxTimeNanos.clear();
        minTimeNanos.clear();
    }
}
