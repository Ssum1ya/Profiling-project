package app.project_profile.infrastructure.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class EchoHttpClientConfig {

    @Value("${mockExternalSystemUrl}")
    private String mockExternalSystemUrl;

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    RestClient echoRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(mockExternalSystemUrl)
                .build();
    }

    @Bean
    EchoHttpClient echoHttpClient(RestClient restClient) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(restClient))
                .build()
                .createClient(EchoHttpClient.class);
    }
}
