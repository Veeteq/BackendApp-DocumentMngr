package com.veeteq.documentmngr;

import com.veeteq.documentmngr.config.DocumentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableConfigurationProperties(DocumentProperties.class)
public class DocumentMngrApp {

    public static void main(String[] args) {
        SpringApplication.run(DocumentMngrApp.class, args);
    }

    @Bean
    RestClient restClient(DocumentProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.addressbookApiUrl())
                .build();
    }
}
