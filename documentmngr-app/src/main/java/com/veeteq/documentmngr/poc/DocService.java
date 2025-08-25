package com.veeteq.documentmngr.poc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocService {

    private final DocRepository docRepository;
    private final DocItemRepository docItemRepository;
    private final EntityManager entityManager;
    private final DocMapper docMapper;

    public DocService(DocRepository docRepository, DocItemRepository docItemRepository, EntityManager entityManager, DocMapper docMapper) {
        this.docRepository = docRepository;
        this.docItemRepository = docItemRepository;
        this.entityManager = entityManager;
        this.docMapper = docMapper;
    }

    @Transactional
    public DocDto save(DocDto dto) {
        var entity = docMapper.toEntity(dto);
        var saved = docRepository.save(entity);
        return docMapper.toDto(saved);
    }

    @Transactional
    public DocDto update(Long id, DocDto dto) {
        var updated = docRepository.findByIdWithLock(id)
                .map(entity -> {
                    entity.clearDocItems();
                    entity.getDocItems().forEach(docItem -> docItemRepository.delete(docItem));
                    var a = docMapper.updateWith(entity, dto);
                    return a;
                })
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        var saved = docRepository.save(updated);
        return docMapper.toDto(saved);
    }

    @Transactional
    public List<DocDto> findAll() {
        return docRepository.findAll().stream()
                .map(docMapper::toDto)
                .collect(Collectors.toList());
    }

    public DocDto findById(Long id) {
        var result = docRepository.findByIdWithLock(id)
                .map(docMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        return result;
    }

    public void delete(Long id) {
        var saved = docRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        docRepository.delete(saved);
    }

    public Long getCount() {
        return docRepository.count();
    }

}
