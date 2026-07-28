package app.project_profile.diagnostics;

import app.project_profile.diagnostics.dto.AllocationAnalysis;
import jdk.jfr.Recording;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@RestController
@RequestMapping("/diagnostic")
@RequiredArgsConstructor
public class DiagnosticsController {
    private final DiagnosticsService diagnosticsService;

    @GetMapping("/allocation-analysis")
    public AllocationAnalysis getAllocationAnalysis(
            @RequestParam(defaultValue = "10") int durationSeconds) throws Exception {

        Path jfrFile = Files.createTempFile("alloc-analysis-", ".jfr");

        try (Recording recording = new Recording()) {
            recording.enable("jdk.ObjectAllocationInNewTLAB");
            recording.enable("jdk.ObjectAllocationOutsideTLAB");

            recording.enable("jdk.OldObjectSample")
                    .withStackTrace()
                    .with("cutoff", "0 ms");

            recording.start();
            Thread.sleep(Duration.ofSeconds(durationSeconds));
            recording.stop();
            recording.dump(jfrFile);
        }

        AllocationAnalysis result = diagnosticsService.analyzeAllocations(jfrFile);
        Files.deleteIfExists(jfrFile);
        return result;
    }
}