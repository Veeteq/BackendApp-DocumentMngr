package com.veeteq.documentmngr.poc;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "doc_right")
public class Right {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "right_generator")
    @SequenceGenerator(name = "right_generator", sequenceName = "right_seq", allocationSize = 1)
    private Long id;

    @Column(name = "data")
    private String data;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "version")
    private Integer version;

    public Long getId() {
        return id;
    }

    public Right setId(Long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public Right setData(String data) {
        this.data = data;
        return this;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public Right setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public Right setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public Right setVersion(Integer version) {
        this.version = version;
        return this;
    }

}
