package com.veeteq.documentmngr.poc;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "docs", uniqueConstraints = @UniqueConstraint(columnNames = "doc_id"))
public class Doc {

    @Id
    @Column(name = "doc_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_generator")
    @SequenceGenerator(name = "doc_generator", sequenceName = "doc_seq", allocationSize = 1)
    private Long id;

    @Column(name = "param_1", nullable = false)
    private String param1;

    @Column(name="param_2")
    private String param2;

    @OneToMany(mappedBy = "doc", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity=DocItem.class)
    private List<DocItem> docItems = new LinkedList<>();

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "version")
    private Integer version;

    public Doc addToDocItems(DocItem docItem) {
        var docItemId = new DocItemId(this.id, this.docItems.size() + 1);
        docItem.setId(docItemId);
        docItem.setDoc(this);
        docItems.add(docItem);
        return this;
    }

    public void clearDocItems() {
        for (var docItem : docItems) {
            docItem.setDoc(null);
        }
        this.docItems.clear();
    }

    public Long getId() {
        return id;
    }

    public Doc setId(Long id) {
        this.id = id;
        return this;
    }

    public String getParam1() {
        return param1;
    }

    public Doc setParam1(String param1) {
        this.param1 = param1;
        return this;
    }

    public String getParam2() {
        return param2;
    }

    public Doc setParam2(String param2) {
        this.param2 = param2;
        return this;
    }

    public List<DocItem> getDocItems() {
        return docItems;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public Doc setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public Doc setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public Doc setVersion(Integer version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return "Doc{id=" + id + ", param1='" + param1 + '\'' + ", param2='" + param2 + "'}";
    }

}
