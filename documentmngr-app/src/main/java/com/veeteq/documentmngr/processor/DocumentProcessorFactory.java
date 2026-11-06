package com.veeteq.documentmngr.processor;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class DocumentProcessorFactory {

    /**
     * Spring boot's dependency injection feature will construct this map for us
     * and include all implementations available in the map with the key as the bean name
     * Logically, the map will look something like below
     * {
     *   "CSV": CsvFileParser,
     *   "XML": XmlFileParser,
     *   "JSON": JsonFileParser
     * }
     */
    private final Map<String, DocumentProcessor> documentProcessors;

    public DocumentProcessorFactory(Map<String, DocumentProcessor> documentProcessors) {
        this.documentProcessors = documentProcessors;
    }

    /**
     * Return's the appropriate FileParser impl given a file type
     * @param fileType one of the file types mentioned in class FileType
     * @return FileParser
     */
    public DocumentProcessor get(String fileType) {
        var processor = documentProcessors.get(fileType);
        if (processor == null) throw new IllegalArgumentException("Unsupported document processor: " + fileType);
        return processor;
    }
}
