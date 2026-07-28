package app.project_profile.diagnostics.dto;

public record MethodStat(
        String methodName,
        long samples,
        long estimatedMs
) {}
