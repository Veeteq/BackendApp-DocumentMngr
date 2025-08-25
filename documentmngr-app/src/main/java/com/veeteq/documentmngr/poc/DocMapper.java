package com.veeteq.documentmngr.poc;

import com.veeteq.documentmngr.model.DocumentType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class DocMapper {

    public Doc toEntity(DocDto dto) {
        var entity = new Doc();
        updateWith(entity, dto);
        return entity;
    }

    public Doc updateWith(Doc entity, DocDto dto) {
        entity.setParam1(dto.getParam1())
              .setParam2(dto.getParam2())
              .setVersion(dto.getVersion());
        if (dto.getTags() != null && dto.getTags().size() > 0) {
            dto.getTags().stream()
                    .map(this::toEntity)
                    .forEach(entity::addToDocItems);
        }
        return entity;
    }

    public DocItem toEntity(DocItemDto dto) {
        var entity = new DocItem()
                .setType(dto.getType())
                .setVersion(dto.getVersion());
        if (dto.getType().equalsIgnoreCase("LEFT")) {
            var left = new Left()
                    .setData(dto.getTag());
            entity.setLeft(left);
        } else if (dto.getType().equalsIgnoreCase("RIGHT")) {
            var right = new Right()
                    .setData(dto.getTag());
            entity.setRight(right);
        }
        return entity;
    }

    public DocDto toDto(Doc entity) {
        var dto = new DocDto()
                .setId(entity.getId())
                .setParam1(entity.getParam1())
                .setParam2(entity.getParam2())
                .setVersion(entity.getVersion())
                .setTags(entity.getDocItems().stream().map(this::toDto).toList());
        return dto;
    }

    public DocItemDto toDto(DocItem entity) {
        var dto = new DocItemDto()
                .setNm(entity.getId().getSequence())
                .setType(entity.getType())
                .setTag(entity.getTag())
                .setVersion(entity.getVersion());
        return dto;
    }
}
