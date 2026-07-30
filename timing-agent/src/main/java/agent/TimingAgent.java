package agent;

import com.sun.net.httpserver.HttpServer;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimingAgent {
    public static List<String> SCANNING_PACKAGES;

    private static final int METRICS_PORT = 9911;

    public static void premain(String args, Instrumentation instrumentation) throws IOException {
        System.err.println("[Timing agent] started");
        List<String> argsList = Arrays.stream(args.split(",")).collect(Collectors.toList());
        MethodNameUtil.TARGET_PACKAGE = argsList.getFirst();
        argsList.removeFirst();
        SCANNING_PACKAGES = argsList;

        ElementMatcher.Junction<TypeDescription> packageMatcher = ElementMatchers.none();
        for (String pkg : SCANNING_PACKAGES) {
            packageMatcher = packageMatcher.or(ElementMatchers.nameStartsWith(pkg));
        }

        new AgentBuilder.Default()
                .type(packageMatcher.and(ElementMatchers.not(ElementMatchers.nameContains("$$"))))
                .transform((builder, typeDescription, classLoader, module, domain) ->
                        builder.method(ElementMatchers.isMethod()
                                        .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                                        .and(ElementMatchers.not(ElementMatchers.isSynthetic())))
                                .intercept(Advice.to(TimingAdvice.class)))
                .installOn(instrumentation);

        startMetricsServer();
    }

    private static void startMetricsServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(METRICS_PORT), 0);

        server.createContext("/metrics", exchange -> {
            String body = MetricsRegistry.toPrometheusFormat();
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

            MetricsRegistry.resetMinMax();
        });

        server.setExecutor(null);
        server.start();
    }
}
