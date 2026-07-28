package app.project_profile.diagnostics.dto;

import java.util.List;

public record AllocationAnalysis(
        List<AllocationVsSurvival> stats
) {}
