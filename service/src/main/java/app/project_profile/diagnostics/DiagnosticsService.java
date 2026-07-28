package app.project_profile.diagnostics;

import app.project_profile.diagnostics.dto.AllocationAnalysis;
import app.project_profile.diagnostics.dto.AllocationVsSurvival;
import jdk.jfr.consumer.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiagnosticsService {
    private static final String APP_BASE_PACKAGE = "app.";
    private static final long SAMPLING_PERIOD_MS = 10;

    public AllocationAnalysis analyzeAllocations(Path jfrFile) throws IOException {
        Map<String, Long> allocatedCountByClass = new HashMap<>();
        Map<String, Long> survivedCountByClass = new HashMap<>();

        try (RecordingFile recordingFile = new RecordingFile(jfrFile)) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                String eventName = event.getEventType().getName();

                if (eventName.equals("jdk.ObjectAllocationInNewTLAB")
                        || eventName.equals("jdk.ObjectAllocationOutsideTLAB")) {

                    RecordedStackTrace stackTrace = event.getStackTrace();
                    if (stackTrace == null || !isFromUserCode(stackTrace)) continue;

                    String className = event.getClass("objectClass").getName();
                    allocatedCountByClass.merge(className, 1L, Long::sum);

                } else if (eventName.equals("jdk.OldObjectSample")) {

                    RecordedObject object = event.getValue("object");
                    RecordedClass objectClass = object.getValue("type");
                    String className = objectClass.getName();
                    survivedCountByClass.merge(className, 1L, Long::sum);
                }
            }
        }

        List<AllocationVsSurvival> churnCandidates = allocatedCountByClass.entrySet().stream()
                .map(e -> new AllocationVsSurvival(
                        e.getKey(),
                        e.getValue(),
                        survivedCountByClass.getOrDefault(e.getKey(), 0L)))
                .sorted(Comparator.comparingLong(AllocationVsSurvival::allocatedCount).reversed())
                .limit(20)
                .toList();

        return new AllocationAnalysis(churnCandidates);
    }

    private boolean isFromUserCode(RecordedStackTrace stackTrace) {
        return stackTrace.getFrames().stream()
                .anyMatch(frame -> frame.getMethod().getType().getName()
                        .startsWith("app.project_profile."));
    }
}
