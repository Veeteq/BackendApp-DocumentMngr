package com.veeteq.documentmngr.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class DocumentItemId {

    private Long id;

    @Column(name = "sequ_nm")
    private Integer sequenceNumber;

    public DocumentItemId() {
    }

    public DocumentItemId(Long id, Integer sequenceNumber) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
    }

    public Long getId() {
        return id;
    }

    public DocumentItemId setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public DocumentItemId setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentItemId that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(sequenceNumber, that.sequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sequenceNumber);
    }
}
