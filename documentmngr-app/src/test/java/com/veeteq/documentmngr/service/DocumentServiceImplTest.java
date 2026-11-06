package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.mapper.DocumentResponseFactory;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.processor.DocumentProcessor;
import com.veeteq.documentmngr.processor.DocumentProcessorFactory;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import com.veeteq.documentmngr.service.jpa.DocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceImplTest {

    @Mock
    private DocumentProcessorFactory documentProcessorFactory;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentResponseFactory documentResponseFactory;

    @Mock
    private DocumentProcessor documentProcessor;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void shouldCreateDocument() {
        // Arrange
        var request = new DocumentRequestDto()
                .documentDate(LocalDate.now())
                .documentType(DocumentTypeDto.BILL)
                .accountId(1L);

        var document = mock(Document.class);
        var savedDocument = mock(Document.class);
        var response = new DocumentResponseDto();

        var processorType = DocumentType.BILL.getProcessorType();

        when(documentProcessorFactory.get(processorType)).thenReturn(documentProcessor);
        when(documentProcessor.process(request)).thenReturn(document);
        when(documentRepository.save(document)).thenReturn(savedDocument);
        when(documentResponseFactory.create(savedDocument)).thenReturn(response);

        // Act
        var result = documentService.createDocument(request);

        // Assert
        assertThat(result).isSameAs(response);
        verify(documentProcessorFactory).get(processorType);
        verify(documentProcessor).process(request);
        verify(documentRepository).save(document);
        verify(documentResponseFactory).create(savedDocument);
        verifyNoMoreInteractions(documentProcessorFactory, documentProcessor, documentRepository, documentResponseFactory);
    }
}
