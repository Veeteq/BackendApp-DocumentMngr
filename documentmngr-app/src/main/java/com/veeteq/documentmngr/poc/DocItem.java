package com.veeteq.documentmngr.poc;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "docitems")
public class DocItem {

    @EmbeddedId
    private DocItemId id;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "doc_id", referencedColumnName = "doc_id", nullable = false)
    private Doc doc;

    @Column(name = "type")
    private String type;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "left_id", referencedColumnName = "id", nullable = true)
    private Left left;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "right_id", referencedColumnName = "id", nullable = true)
    private Right right;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "version")
    private Integer version;

    public DocItem() {
    }

    public DocItemId getId() {
        return id;
    }

    public DocItem setId(DocItemId id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public DocItem setType(String type) {
        this.type = type;
        return this;
    }

    public Doc getDoc() {
        return doc;
    }

    public DocItem setDoc(Doc doc) {
        this.doc = doc;
        return this;
    }

    public Left getLeft() {
        return left;
    }

    public DocItem setLeft(Left left) {
        this.left = left;
        return this;
    }

    public Right getRight() {
        return right;
    }

    public DocItem setRight(Right right) {
        this.right = right;
        return this;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public DocItem setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public DocItem setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public DocItem setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getTag() {
        return this.left != null ? this.left.getData() : this.right.getData();
    }
}
