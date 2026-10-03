package com.veeteq.documentmngr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "document")
public class DocumentProperties {

    private long transferItemId;

    public long getTransferItemId() {
        return transferItemId;
    }

    public void setTransferItemId(long transferItemId) {
        this.transferItemId = transferItemId;
    }
}
