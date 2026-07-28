package app.project_profile.api;

import app.project_profile.application.MessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageRestController {
    private final MessageService messageService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/process")
    public ResponseEntity<?> process(
            @RequestBody String rawRequestBody,
            HttpServletRequest request
    ) throws IOException {
        ObjectNode requestNode = (ObjectNode) objectMapper.readTree(rawRequestBody);
        String jsonRequest = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestNode);
        log.info("Received process request = {}", jsonRequest);

        String fullUrl = request.getRequestURL().toString();

        ObjectNode responseNode = messageService.process(requestNode, fullUrl);
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseNode);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonResponse);
    }
}
