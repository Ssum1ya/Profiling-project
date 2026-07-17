package app.project_profile.application;

import app.project_profile.api.dto.request.MessageRequestDto;
import app.project_profile.api.dto.response.MessageResponseDto;
import app.project_profile.api.dto.MessageXmlDto;
import app.project_profile.infrastructure.EchoHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final StringRedisTemplate stringRedisTemplate;
    private final EchoHttpClient echoHttpClient;

    public MessageResponseDto process(MessageRequestDto requestDto) {
        UUID randomUuid = UUID.randomUUID();
        int randomNumber = ThreadLocalRandom.current().nextInt();

        MessageXmlDto response = echoHttpClient.echoRequest(new MessageXmlDto(requestDto.message()));
        log.info("Received response from mock external system: message = {}", response.getMessage());

        stringRedisTemplate.opsForValue().set(
                randomUuid.toString(),
                Integer.toString(randomNumber),
                Duration.ofDays(1)
        );
        log.info("Message processed: uuid = {}, random = {}", randomUuid, randomNumber);

        return new MessageResponseDto(requestDto.message(), randomUuid);
    }

    public Integer getProcessedInfoInRedis(UUID uuid) {
        String stringNumber = stringRedisTemplate.opsForValue().get(uuid.toString());
        if (stringNumber == null) {
            throw new IllegalArgumentException();
        }

        return Integer.valueOf(stringNumber);
    }
}
