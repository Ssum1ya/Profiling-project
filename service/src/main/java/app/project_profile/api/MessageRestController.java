package app.project_profile.api;

import app.project_profile.api.dto.request.MessageRequestDto;
import app.project_profile.api.dto.response.MessageResponseDto;
import app.project_profile.application.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageRestController {
    private final MessageService messageService;

    @PostMapping("/process")
    public MessageResponseDto process(@RequestBody MessageRequestDto request) {
        log.info("Received request: message = {}", request.message());
        return messageService.process(request);
    }
}
