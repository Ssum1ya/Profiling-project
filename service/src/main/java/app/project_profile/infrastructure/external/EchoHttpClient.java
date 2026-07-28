package app.project_profile.infrastructure.external;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = "application/xml", contentType = "application/xml", url = "/")
public interface EchoHttpClient {

    @PostExchange()
    String echoRequest(@RequestBody String request);
}
