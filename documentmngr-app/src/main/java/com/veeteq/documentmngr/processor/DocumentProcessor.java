package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;

/**
 * https://medium.com/codex/implementing-the-strategy-design-pattern-in-spring-boot-df3adb9ceb4a
 */
public interface DocumentProcessor {

    Document process(DocumentRequestDto documentDto);

    Document process(Document document, DocumentRequestDto documentDto);

    class Type {
        public final static String BASE = "BASE";
        public final static String TRANSFER = "TRANSFER";
    }
}
