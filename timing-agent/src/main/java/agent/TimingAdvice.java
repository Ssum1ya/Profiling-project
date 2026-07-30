package agent;

import net.bytebuddy.asm.Advice;

public class TimingAdvice {
    @Advice.OnMethodEnter
    public static long enter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Enter long startTime,
                            @Advice.Origin String methodName) {
        long elapsedNanos = System.nanoTime() - startTime;
        String simplifiedName = MethodNameUtil.simplify(methodName);
        MetricsRegistry.write(simplifiedName, elapsedNanos);
    }
}
