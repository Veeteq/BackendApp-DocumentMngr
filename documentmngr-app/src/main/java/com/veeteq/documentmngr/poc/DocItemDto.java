package com.veeteq.documentmngr.poc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocItemDto {

    @JsonProperty(value = "tag_nm")
    private Integer nm;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "value")
    private String tag;

    @JsonProperty(value = "version")
    private Integer version;

    public Integer getNm() {
        return nm;
    }

    public DocItemDto setNm(Integer nm) {
        this.nm = nm;
        return this;
    }

    public String getType() {
        return type;
    }

    public DocItemDto setType(String type) {
        this.type = type;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public DocItemDto setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public DocItemDto setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
