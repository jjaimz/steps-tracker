package com.steps.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
//    @JsonProperty("type")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;
    @JsonProperty("title")
    private String title;
    @JsonProperty("detail")
    private String detail;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
