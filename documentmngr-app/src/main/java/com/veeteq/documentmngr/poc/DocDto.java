package com.veeteq.documentmngr.poc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DocDto {

    private Long id;

    @JsonProperty(value = "param_1")
    private String param1;

    @JsonProperty(value = "param_2")
    private String param2;

    @JsonProperty(value = "version")
    private Integer version;

    @JsonProperty(value = "tags")
    private List<DocItemDto> tags;

    public Long getId() {
        return id;
    }

    public DocDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getParam1() {
        return param1;
    }

    public DocDto setParam1(String param1) {
        this.param1 = param1;
        return this;
    }

    public String getParam2() {
        return param2;
    }

    public DocDto setParam2(String param2) {
        this.param2 = param2;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public DocDto setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public List<DocItemDto> getTags() {
        return tags;
    }

    public DocDto setTags(List<DocItemDto> tags) {
        this.tags = tags;
        return this;
    }
}
