package app.project_profile.infrastructure;

import app.project_profile.api.dto.MessageXmlDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = "application/xml", contentType = "application/xml", url = "/")
public interface EchoHttpClient {

    @PostExchange()
    MessageXmlDto echoRequest(@RequestBody MessageXmlDto request);
}
