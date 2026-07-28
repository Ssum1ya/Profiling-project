package app.project_profile.diagnostics.dto;

public record AllocationStat(
        String className,
        long count,
        long totalBytes
) {}
