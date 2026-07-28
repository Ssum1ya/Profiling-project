package app.project_profile.application;

import app.project_profile.infrastructure.external.EchoHttpClient;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import  com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MeterRegistry meterRegistry;

    private final StringRedisTemplate stringRedisTemplate;

    private final EchoHttpClient echoHttpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    XmlMapper xmlMapper = new XmlMapper();

    @Value("${pathToJson}")
    private String pathtojson;

    public ObjectNode process(ObjectNode requestNode, String url) throws IOException {
        String xmlRequestToMock = xmlMapper.writeValueAsString(requestNode);

        Timer.Sample sample = Timer.start(meterRegistry);
        String stringXmlResponse;
        try {
            stringXmlResponse = echoHttpClient.echoRequest(xmlRequestToMock);
        } finally {
            sample.stop(Timer.builder("external.echo.request.time")
                    .description("Time spent calling external echo service")
                    .tag("client", "EchoHttpClient")
                    .register(meterRegistry));
        }

        ObjectNode nodeResponse = (ObjectNode) xmlMapper.readTree(stringXmlResponse);
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(nodeResponse);
        log.info("Received response from mock external system: response = {}", jsonResponse);

        UUID randomUuid = UUID.randomUUID();
        int randomNumber = ThreadLocalRandom.current().nextInt();

        stringRedisTemplate.opsForValue().set(
                randomUuid.toString(),
                Integer.toString(randomNumber),
                Duration.ofDays(1)
        );
        log.info("Request processed: uuid = {}, random = {}", randomUuid, randomNumber);

        ObjectNode node = (ObjectNode) objectMapper.readTree(
                Files.readString(Path.of(pathtojson + "MessageResponseDto.json"))
        );

        Iterator<Map.Entry<String, JsonNode>> fields = nodeResponse.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            if (node.get(key).asText().equals("")) {
                node.put(key, value);
            }
        }

        node.put("uuid", randomUuid.toString());
        node.put("url", url);

        return node;
    }

    public Integer getProcessedInfoInRedis(UUID uuid) {
        String stringNumber = stringRedisTemplate.opsForValue().get(uuid.toString());
        if (stringNumber == null) {
            throw new IllegalArgumentException();
        }

        return Integer.valueOf(stringNumber);
    }
}
