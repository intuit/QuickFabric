package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class UserRole {

    @JsonIgnore
    private int id;

    private String name;

    private List<SegmentVO> segments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SegmentVO> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentVO> segments) {
        this.segments = segments;
    }
}
