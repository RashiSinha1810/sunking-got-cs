package com.example.got.got.dto;

import java.util.List;

public class RegionLocationDto {
    private String region;
    private List<String> locations;

    public RegionLocationDto(String region, List<String> locations) {
        this.region = region;
        this.locations = locations;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}