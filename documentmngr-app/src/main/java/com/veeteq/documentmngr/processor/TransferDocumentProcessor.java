package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import org.springframework.stereotype.Service;

@Service(value = DocumentProcessor.Type.TRANSFER)
public class TransferDocumentProcessor implements DocumentProcessor {

    @Override
    public Document process(DocumentRequestDto requestDto) {
        System.out.println("Processing using " + Type.TRANSFER);
        var document = Document.builder().build();
        return document;
    }
}
