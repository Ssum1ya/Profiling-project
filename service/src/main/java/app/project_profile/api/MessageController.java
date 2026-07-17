package app.project_profile.api;

import app.project_profile.application.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/process/{uuid}")
    public String getProcessedInfo(
            Model model,
            @PathVariable("uuid") UUID uuid
    ) {
        model.addAttribute(
                "random",
                messageService.getProcessedInfoInRedis(uuid)
        );
        return "processed-page";
    }
}
