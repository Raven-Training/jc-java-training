package com.raven.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de la aplicación
 */
@Configuration
public class AppConfig {

    /**
     * Configuración del RestTemplate para realizar peticiones HTTP
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 segundos de tiempo de espera para conectar
        factory.setReadTimeout(5000);    // 5 segundos de tiempo de espera para leer
        return new RestTemplate(factory);
    }
}
