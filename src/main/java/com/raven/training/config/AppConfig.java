package com.raven.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration class for defining application-wide beans.
 * This class is responsible for setting up and configuring various components
 * that are used throughout the application, such as the RestTemplate.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Configuration
public class AppConfig {

    /**
     * Configures and provides a RestTemplate bean for making HTTP requests.
     * The RestTemplate is configured with custom timeouts for connection and reading
     * to prevent long-running requests from blocking the application.
     *
     * @return A pre-configured {@link RestTemplate} instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
