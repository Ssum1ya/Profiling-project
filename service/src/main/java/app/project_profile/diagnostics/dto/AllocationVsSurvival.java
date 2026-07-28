package app.project_profile.diagnostics.dto;

public record AllocationVsSurvival(
        String className,
        long allocatedCount,
        long survivedCount
) {}
