package com.veeteq.documentmngr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "document")
public record DocumentProperties(
        long transferItemId,
        String addressbookApiUrl) {}

