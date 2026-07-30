package app.project_profile.api;

import app.project_profile.application.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/process/{uuid}")
    public String getProcessedInfo(
            Model model,
            @PathVariable("uuid") UUID uuid
    ) {
        log.info("Received get processed info request: uuid = {}", uuid);
        model.addAttribute(
                "random",
                messageService.getProcessedInfoInRedis(uuid)
        );
        return "processed-page";
    }
}
