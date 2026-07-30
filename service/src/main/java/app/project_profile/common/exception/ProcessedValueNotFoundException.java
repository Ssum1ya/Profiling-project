package app.project_profile.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ProcessedValueNotFoundException extends RuntimeException {
    private final UUID uuid;
}
