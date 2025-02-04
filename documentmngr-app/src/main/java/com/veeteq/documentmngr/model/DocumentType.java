package com.veeteq.documentmngr.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.veeteq.documentmngr.processor.DocumentProcessor;

import java.util.stream.Stream;

public enum DocumentType {
    TRANSFER("Transfer", DocumentProcessor.Type.BASE),
    NOTE("Note", DocumentProcessor.Type.BASE),
    BILL("Bill", DocumentProcessor.Type.BASE),
    INVOICE("Invoice", DocumentProcessor.Type.TRANSFER);

    private final String displayName;
    private final String processorType;

    DocumentType(String displayName, String processorType) {
        this.displayName = displayName;
        this.processorType = processorType;

    }

    @JsonValue
    public String getProcessorType() {
        return processorType;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static DocumentType findByValue(String displayName) {
        return Stream.of(DocumentType.values())
                .filter(el -> el.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
