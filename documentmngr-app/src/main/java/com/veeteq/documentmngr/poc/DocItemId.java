package com.veeteq.documentmngr.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DocItemId implements Serializable {

    @Column(name = "doc_id")
    private Long id;

    @Column(name = "seq_nm")
    private Integer sequence;

    public DocItemId() {
    }

    public DocItemId(Long id, Integer sequence) {
        this.id = id;
        this.sequence = sequence;
    }

    public Long getId() {
        return id;
    }

    public DocItemId setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getSequence() {
        return sequence;
    }

    public DocItemId setSequence(Integer sequence) {
        this.sequence = sequence;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocItemId that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(sequence, that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sequence);
    }
}
