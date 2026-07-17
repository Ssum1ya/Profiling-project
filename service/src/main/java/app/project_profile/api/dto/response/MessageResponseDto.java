package app.project_profile.api.dto.response;

import java.util.UUID;

public record MessageResponseDto(
   String message,
   UUID uuid
) {}
