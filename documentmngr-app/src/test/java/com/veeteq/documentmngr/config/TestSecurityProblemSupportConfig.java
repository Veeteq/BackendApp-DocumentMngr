package com.veeteq.documentmngr.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@TestConfiguration
public class TestSecurityProblemSupportConfig {

    @Bean
    public SecurityProblemSupport securityProblemSupport(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return new SecurityProblemSupport(resolver);
    }
}
